package com.java3d.engine.renderer;

import com.java3d.engine.geometry.Mesh;
import com.java3d.engine.geometry.Triangle;
import com.java3d.engine.geometry.Vertex;
import com.java3d.engine.scene.Camera;
import com.java3d.engine.scene.GameObject;
import com.java3d.engine.scene.Starfield;
import com.java3d.engine.scene.PointLight;
import com.java3d.engine.scene.Scene;
import com.java3d.engine.window.Window;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Renderer {
    
    private BufferedImage image;
    private int[] pixels;
    private float[] zBuffer;
    private int w, h;

    private static class ProjectedTriangle {
        Vertex v1, v2, v3;
        float i1, i2, i3;
        Color color;
        ProjectedTriangle(Vertex v1, Vertex v2, Vertex v3, Color color, float i1, float i2, float i3) {
            this.v1 = v1; this.v2 = v2; this.v3 = v3;
            this.color = color; this.i1 = i1; this.i2 = i2; this.i3 = i3;
        }
    }

    public void render(Graphics2D g, Scene scene, int width, int height, float speed) {
        // Inicializar buffers se necessário (ou se a janela redimensionar)
        if (image == null || w != width || h != height) {
            w = width;
            h = height;
            image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
            zBuffer = new float[w * h];
        }

        // Limpar buffers
        Arrays.fill(pixels, 0); // Preto (0x000000)
        Arrays.fill(zBuffer, Float.MAX_VALUE); // Z infinito

        // Renderizar o Starfield primeiro (sem Z-buffer)
        Starfield starfield = scene.getStarfield();
        if (starfield != null) {
            Graphics2D ig = image.createGraphics();
            renderStarfield(ig, starfield, scene.getCamera(), width, height, speed);
            ig.dispose();
        }

        Camera camera = scene.getCamera();
        List<GameObject> gameObjects = scene.getGameObjects();

        double fovRad = Math.toRadians(camera.getFov());
        double f = (width / 2.0) / Math.tan(fovRad / 2.0);

        // Transformar a Luz para o Espaço da Câmera (View Space)
        PointLight pl = scene.getPointLight();
        Vertex lightView = worldToView(new Vertex(pl.getX(), pl.getY(), pl.getZ()), camera);

        List<ProjectedTriangle> trianglesToRaster = new ArrayList<>();

        for (GameObject obj : gameObjects) {
            Mesh mesh = obj.getMesh();
            
            // Transformar o Centro do Objeto para View Space (usado para calcular normais dos vértices)
            Vertex objView = worldToView(new Vertex(obj.getX(), obj.getY(), obj.getZ()), camera);

            // Itera sobre todos os triângulos da malha
            for (Triangle t : mesh.triangles) {
                // 1. Transformar para World Space (Rotação do Objeto + Translação)
                Vertex v1World = modelToWorld(t.v1, obj);
                Vertex v2World = modelToWorld(t.v2, obj);
                Vertex v3World = modelToWorld(t.v3, obj);

                // 2. Transformar para View Space (Câmera)
                Vertex v1View = worldToView(v1World, camera);
                Vertex v2View = worldToView(v2World, camera);
                Vertex v3View = worldToView(v3World, camera);

                float flatIntensity = 0;
                if (Window.flatLight) {
                    // --- Flat Shading (Iluminação por Face) ---
                    // Calcular vetores do triângulo no View Space
                    double ux = v2View.getX() - v1View.getX();
                    double uy = v2View.getY() - v1View.getY();
                    double uz = v2View.getZ() - v1View.getZ();

                    double vx = v3View.getX() - v1View.getX();
                    double vy = v3View.getY() - v1View.getY();
                    double vz = v3View.getZ() - v1View.getZ();

                    // Normal da Face (Produto Vetorial)
                    double nx = uy * vz - uz * vy;
                    double ny = uz * vx - ux * vz;
                    double nz = ux * vy - uy * vx;
                    double nLen = Math.sqrt(nx * nx + ny * ny + nz * nz);
                    if (nLen > 0) { nx /= nLen; ny /= nLen; nz /= nLen; }

                    // Vetor da Luz (Luz - Centro do Triângulo)
                    double cx = (v1View.getX() + v2View.getX() + v3View.getX()) / 3.0;
                    double cy = (v1View.getY() + v2View.getY() + v3View.getY()) / 3.0;
                    double cz = (v1View.getZ() + v2View.getZ() + v3View.getZ()) / 3.0;

                    double lx = lightView.getX() - cx;
                    double ly = lightView.getY() - cy;
                    double lz = lightView.getZ() - cz;
                    double lLen = Math.sqrt(lx * lx + ly * ly + lz * lz);
                    if (lLen > 0) { lx /= lLen; ly /= lLen; lz /= lLen; }

                    // Intensidade baseada no ângulo entre a Normal e a Luz
                    double dot = nx * lx + ny * ly + nz * lz;
                    flatIntensity = (float) Math.max(0.3, Math.min(1.0, Math.abs(dot)));
                }

                // 2. Recorte (Clipping) contra o Near Plane
                for (Triangle clipped : clipTriangle(v1View, v2View, v3View)) {
                    float i1, i2, i3;

                    if (Window.flatLight) {
                        i1 = i2 = i3 = flatIntensity;
                    } else {
                        // Calcular intensidade (Gouraud) para cada vértice do triângulo recortado
                        i1 = calculateIntensity(clipped.getV1(), objView, lightView);
                        i2 = calculateIntensity(clipped.getV2(), objView, lightView);
                        i3 = calculateIntensity(clipped.getV3(), objView, lightView);
                    }

                    Vertex p1 = projectToScreen(clipped.getV1(), f, width, height);
                    Vertex p2 = projectToScreen(clipped.getV2(), f, width, height);
                    Vertex p3 = projectToScreen(clipped.getV3(), f, width, height);

                    trianglesToRaster.add(new ProjectedTriangle(p1, p2, p3, Color.WHITE, i1, i2, i3));
                }
            }
        }

        // Rasterização Multithreaded (Dividindo a tela em faixas horizontais)
        int numThreads = Runtime.getRuntime().availableProcessors();
        int bandHeight = height / numThreads;

        IntStream.range(0, numThreads).parallel().forEach(i -> {
            int startY = i * bandHeight;
            int endY = (i == numThreads - 1) ? height : (startY + bandHeight);
            for (ProjectedTriangle pt : trianglesToRaster) {
                drawTriangle(pt.v1, pt.v2, pt.v3, pt.color, pt.i1, pt.i2, pt.i3, startY, endY);
            }
        });

        // Desenhar a imagem final na tela
        g.drawImage(image, 0, 0, null);
    }

    private void renderStarfield(Graphics2D g, Starfield starfield, Camera cam, int width, int height, float speed) {
        double fovRad = Math.toRadians(cam.getFov());
        double f = (width / 2.0) / Math.tan(fovRad / 2.0);
        float spread = starfield.getSpread();
        float halfSpread = spread / 2.0f;

        for (Vertex star : starfield.getStars()) {
            // Calcular posição relativa com "wrap around" (efeito infinito)
            // Isso faz com que as estrelas se repitam em blocos do tamanho de 'spread'
            double dx = (star.getX() - cam.getX()) % spread;
            if (dx > halfSpread) dx -= spread;
            if (dx < -halfSpread) dx += spread;

            double dy = (star.getY() - cam.getY()) % spread;
            if (dy > halfSpread) dy -= spread;
            if (dy < -halfSpread) dy += spread;

            double dz = (star.getZ() - cam.getZ()) % spread;
            if (dz > halfSpread) dz -= spread;
            if (dz < -halfSpread) dz += spread;

            // Rotação da Câmera (Manual aqui pois worldToView não suporta o wrap)
            double yawRad = Math.toRadians(cam.getYaw());
            double cosY = Math.cos(-yawRad);
            double sinY = Math.sin(-yawRad);
            
            double x1 = dx * cosY - dz * sinY;
            double z1 = dx * sinY + dz * cosY;
            
            double pitchRad = Math.toRadians(cam.getPitch());
            double cosP = Math.cos(-pitchRad);
            double sinP = Math.sin(-pitchRad);
            
            double y2 = dy * cosP - z1 * sinP;
            double z2 = dy * sinP + z1 * cosP;

            Vertex starView = new Vertex((float)x1, (float)y2, (float)z2);

            // Se a estrela estiver na frente da câmera
            if (starView.getZ() > 0.1) {
                Vertex starScreen = projectToScreen(starView, f, width, height);
                int x = (int) starScreen.getX();
                int y = (int) starScreen.getY();

                // Desenha o pixel se estiver dentro dos limites da tela
                // Efeito de rastro (Warp Speed)
                float baseSpeed = 0.3f;
                if (speed > baseSpeed + 0.1f) {
                    float centerX = width / 2.0f;
                    float centerY = height / 2.0f;
                    float vecX = x - centerX;
                    float vecY = y - centerY;
                    
                    // O comprimento do rastro depende da velocidade e da distância ao centro
                    float factor = Math.min(0.3f, (speed - baseSpeed) * 0.005f);
                    int x2 = (int) (x - vecX * factor);
                    int y2l = (int) (y - vecY * factor);

                    g.setColor(Color.WHITE);
                    g.drawLine(x, y, x2, y2l);
                } else if (x >= 0 && x < w && y >= 0 && y < h) {
                    pixels[y * w + x] = Color.WHITE.getRGB();
                }
            }
        }
    }

    private float calculateIntensity(Vertex v, Vertex objCenter, Vertex lightPos) {
        // Normal: Vetor do centro do objeto até o vértice (funciona bem para esferas)
        double nx = v.getX() - objCenter.getX();
        double ny = v.getY() - objCenter.getY();
        double nz = v.getZ() - objCenter.getZ();
        double nLen = Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (nLen > 0) { nx /= nLen; ny /= nLen; nz /= nLen; }

        // Vetor da Luz: Da luz até o vértice
        double lx = lightPos.getX() - v.getX();
        double ly = lightPos.getY() - v.getY();
        double lz = lightPos.getZ() - v.getZ();
        double lLen = Math.sqrt(lx * lx + ly * ly + lz * lz);
        if (lLen > 0) { lx /= lLen; ly /= lLen; lz /= lLen; }

        double dot = nx * lx + ny * ly + nz * lz;
        return (float) Math.max(0.3, Math.min(1.0, Math.abs(dot)));
    }

    private void drawTriangle(Vertex v1, Vertex v2, Vertex v3, Color color, float i1, float i2, float i3, int clipMinY, int clipMaxY) {
        // Coordenadas na tela
        int x1 = (int) v1.getX(); int y1 = (int) v1.getY(); float z1 = (float) v1.getZ();
        int x2 = (int) v2.getX(); int y2 = (int) v2.getY(); float z2 = (float) v2.getZ();
        int x3 = (int) v3.getX(); int y3 = (int) v3.getY(); float z3 = (float) v3.getZ();

        // Bounding Box (Caixa delimitadora) do triângulo
        int minX = Math.max(0, Math.min(x1, Math.min(x2, x3)));
        int minY = Math.max(clipMinY, Math.min(y1, Math.min(y2, y3)));
        int maxX = Math.min(w - 1, Math.max(x1, Math.max(x2, x3)));
        int maxY = Math.min(clipMaxY - 1, Math.max(y1, Math.max(y2, y3)));

        // Área do triângulo (usada para coordenadas baricêntricas)
        float area = (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1);
        if (area == 0) return;

        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                // Coordenadas Baricêntricas (pesos w1, w2, w3)
                float w1 = ((x2 - x) * (y3 - y) - (y2 - y) * (x3 - x)) / area;
                float w2 = ((x3 - x) * (y1 - y) - (y3 - y) * (x1 - x)) / area;
                float w3 = 1.0f - w1 - w2;

                // Se o pixel (x, y) está dentro do triângulo
                if (w1 >= 0 && w2 >= 0 && w3 >= 0) {
                    // Interpolação de profundidade (Z)
                    float depth = w1 * z1 + w2 * z2 + w3 * z3;
                    int index = y * w + x;

                    // Teste do Z-Buffer
                    if (depth < zBuffer[index]) {
                        zBuffer[index] = depth;
                        
                        // Interpolação de Intensidade (Gouraud Shading)
                        float intensity = w1 * i1 + w2 * i2 + w3 * i3;
                        
                        int finalR = Math.min(255, (int)(r * intensity));
                        int finalG = Math.min(255, (int)(g * intensity));
                        int finalB = Math.min(255, (int)(b * intensity));
                        
                        pixels[index] = (finalR << 16) | (finalG << 8) | finalB;
                    }
                }
            }
        }
    }

    private Vertex modelToWorld(Vertex v, GameObject obj) {
        double x = v.getX();
        double y = v.getY();
        double z = v.getZ();

        // Rotação X
        if (obj.getRx() != 0) {
            double rad = Math.toRadians(obj.getRx());
            double cos = Math.cos(rad);
            double sin = Math.sin(rad);
            double yNew = y * cos - z * sin;
            double zNew = y * sin + z * cos;
            y = yNew;
            z = zNew;
        }

        // Rotação Y
        if (obj.getRy() != 0) {
            double rad = Math.toRadians(obj.getRy());
            double cos = Math.cos(rad);
            double sin = Math.sin(rad);
            double xNew = x * cos + z * sin;
            double zNew = -x * sin + z * cos;
            x = xNew;
            z = zNew;
        }

        // Rotação Z
        if (obj.getRz() != 0) {
            double rad = Math.toRadians(obj.getRz());
            double cos = Math.cos(rad);
            double sin = Math.sin(rad);
            double xNew = x * cos - y * sin;
            double yNew = x * sin + y * cos;
            x = xNew;
            y = yNew;
        }

        return new Vertex((float)(x + obj.getX()), (float)(y + obj.getY()), (float)(z + obj.getZ()));
    }

    private Vertex worldToView(Vertex worldV, Camera cam) {
        // 2. World Space -> View Space (Relativo à Câmera)
        double viewX = worldV.getX() - cam.getX();
        double viewY = worldV.getY() - cam.getY();
        double viewZ = worldV.getZ() - cam.getZ();

        // Rotação da Câmera (World Space -> View Space)
        // 1. Yaw (Rotação em torno do eixo Y)
        // Rotacionamos o mundo na direção oposta à rotação da câmera (-yaw)
        double yawRad = Math.toRadians(cam.getYaw());
        double cosY = Math.cos(-yawRad);
        double sinY = Math.sin(-yawRad);
        
        double x1 = viewX * cosY - viewZ * sinY;
        double z1 = viewX * sinY + viewZ * cosY;
        
        // 2. Pitch (Rotação em torno do eixo X)
        double pitchRad = Math.toRadians(cam.getPitch());
        double cosP = Math.cos(-pitchRad);
        double sinP = Math.sin(-pitchRad);
        
        double y2 = viewY * cosP - z1 * sinP;
        double z2 = viewY * sinP + z1 * cosP;

        return new Vertex((float)x1, (float)y2, (float)z2);
    }

    private List<Triangle> clipTriangle(Vertex v1, Vertex v2, Vertex v3) {
        List<Triangle> result = new ArrayList<>();
        float near = 0.1f;

        // Verificar quais vértices estão dentro (Z >= near)
        boolean in1 = v1.getZ() >= near;
        boolean in2 = v2.getZ() >= near;
        boolean in3 = v3.getZ() >= near;
        int inCount = (in1 ? 1 : 0) + (in2 ? 1 : 0) + (in3 ? 1 : 0);

        if (inCount == 3) {
            result.add(new Triangle(v1, v2, v3));
        } else if (inCount == 1) {
            Vertex in = in1 ? v1 : (in2 ? v2 : v3);
            Vertex out1 = in1 ? v2 : (in2 ? v3 : v1);
            Vertex out2 = in1 ? v3 : (in2 ? v1 : v2);

            result.add(new Triangle(in, intersect(in, out1, near), intersect(in, out2, near)));
        } else if (inCount == 2) {
            Vertex out = !in1 ? v1 : (!in2 ? v2 : v3);
            Vertex in1v = !in1 ? v2 : (!in2 ? v3 : v1);
            Vertex in2v = !in1 ? v3 : (!in2 ? v1 : v2);

            Vertex int1 = intersect(in1v, out, near);
            Vertex int2 = intersect(in2v, out, near);

            result.add(new Triangle(in1v, in2v, int1));
            result.add(new Triangle(in2v, int2, int1));
        }
        return result;
    }

    private Vertex intersect(Vertex v1, Vertex v2, float zPlane) {
        float t = (zPlane - v1.getZ()) / (v2.getZ() - v1.getZ());
        float x = v1.getX() + t * (v2.getX() - v1.getX());
        float y = v1.getY() + t * (v2.getY() - v1.getY());
        return new Vertex(x, y, zPlane);
    }

    private Vertex projectToScreen(Vertex v, double f, int width, int height) {
        // 3. Projection (Perspectiva)
        // Evitar divisão por zero (embora o clipping deva prevenir isso)
        if (v.getZ() == 0) return new Vertex(0, 0, 0);

        double projX = (v.getX() * f) / v.getZ();
        double projY = (v.getY() * f) / v.getZ();

        // 4. Screen Space (Centralizar na tela e inverter Y)
        double screenX = projX + width / 2.0;
        double screenY = height / 2.0 - projY;

        return new Vertex((float)screenX, (float)screenY, (float)v.getZ());
    }
}