package com.java3d.engine.window;

import com.java3d.engine.geometry.Mesh;
import com.java3d.engine.geometry.Triangle;
import com.java3d.engine.geometry.Vertex;
import com.java3d.engine.renderer.Renderer;
import com.java3d.engine.scene.Camera;
import com.java3d.engine.scene.GameObject;
import com.java3d.engine.scene.Road;
import com.java3d.engine.scene.Scene;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class WindowRace extends JFrame {

    private Scene scene;
    private Renderer renderer;
    private JPanel canvas;
    private boolean left, right, accel;
    private float carPosOnTrackX = 0.0f; // Posição X do carro relativa ao centro da pista
    private float speed = 0.0f;
    private float maxSpeed = 1.5f;
    
    public WindowRace() {
        setTitle("Java 3D Racing Demo");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        scene = new Scene();
        renderer = new Renderer();

        // Ativar iluminação Flat para que o carro (feito de cubos) tenha faces definidas
        Window.flatLight = true;

        // Configurar Câmera
        scene.setBackgroundColor(0x87CEEB); // Céu Azul (Sky Blue)
        scene.setGroundColor(0x228B22); // Grama (Forest Green)
        scene.getCamera().setFov(80);
        scene.getCamera().setPosition(0, 3, -8); // Câmera mais alta e atrás
        scene.getCamera().setPitch(15);

        // Configurar Estrada
        Road road = new Road();
        scene.setRoad(road);

        // Adicionar o "Carro" (Modelo customizado dividido em partes coloridas)
        List<GameObject> carParts = createCarParts();
        for (GameObject part : carParts) {
            part.setPosition(0, -1.25f, 0); // Posicionado logo acima do chão (-2.0)
            scene.addGameObject(part);
        }

        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                renderer.render((Graphics2D) g, scene, getWidth(), getHeight(), speed * 10);
            }
        };
        add(canvas);

        // Controles
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A, KeyEvent.VK_LEFT -> left = true;
                    case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right = true;
                    case KeyEvent.VK_W, KeyEvent.VK_UP -> accel = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A, KeyEvent.VK_LEFT -> left = false;
                    case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right = false;
                    case KeyEvent.VK_W, KeyEvent.VK_UP -> accel = false;
                }
            }
        });
        setFocusable(true);
        requestFocus();

        // Game Loop
        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Camera cam = scene.getCamera();

                // Aceleração
                if (accel) {
                    speed += 0.02f;
                    if (speed > maxSpeed) speed = maxSpeed;
                } else {
                    speed -= 0.01f;
                    if (speed < 0) speed = 0;
                }

                // Movimento Lateral (relativo à pista)
                float turnSpeed = 0.2f;
                if (left && speed > 0) {
                    carPosOnTrackX -= turnSpeed;
                } else if (right && speed > 0) {
                    carPosOnTrackX += turnSpeed;
                }

                // Usar a primeira parte (Chassi) como referência para Z
                GameObject carRef = scene.getGameObjects().get(0);
                float carZ = carRef.getZ() + speed; // Calcula a nova posição Z

                // Obter o X do centro da pista na nova posição Z
                float trackCenterX = scene.getRoad().getTrackX(carZ);
                float carWorldX = trackCenterX + carPosOnTrackX;

                // 1. Calcular o ângulo da pista para o CARRO (tangente imediata)
                float carLookAhead = 5.0f; 
                float nextCarTrackX = scene.getRoad().getTrackX(carZ + carLookAhead);
                float carDx = nextCarTrackX - trackCenterX;
                float carTrackAngle = (float) Math.toDegrees(Math.atan2(carDx, carLookAhead));

                // 2. Calcular o ângulo da pista para a CÂMERA (horizonte distante)
                // Olhar bem à frente (200m) garante que a próxima reta fique centralizada na tela
                float camLookAhead = 200.0f; 
                float nextCamTrackX = scene.getRoad().getTrackX(carZ + camLookAhead);
                float camDx = nextCamTrackX - trackCenterX;
                float camTrackAngle = (float) Math.toDegrees(Math.atan2(camDx, camLookAhead));

                // Aplicar movimento a TODAS as partes do carro
                for (GameObject part : scene.getGameObjects()) {
                    part.setX(carWorldX);
                    part.setZ(carZ);

                    // Inclinação visual (Esterçamento)
                    float steeringAngle = 0;
                    if (left && speed > 0) steeringAngle = -10;
                    else if (right && speed > 0) steeringAngle = 10;

                    // Alinhar carro com a pista (imediata) + esterçamento
                    part.setRy(carTrackAngle + steeringAngle);
                }

                // Câmera segue o carro
                cam.setPosition(carWorldX, 3, carZ - 8);
                
                // Rotacionar câmera para acompanhar o horizonte (manter a próxima reta centralizada)
                cam.setYaw(-camTrackAngle);

                // Atualizar Estrada (Gerar segmentos à frente do carro)
                scene.getRoad().update(carZ);

                // Atualizar Posição do Sol (Luz) para seguir o carro
                scene.getPointLight().setPosition(carWorldX - 60, 100, carZ + 60);

                canvas.repaint();
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WindowRace().setVisible(true);
        });
    }

    private List<GameObject> createCarParts() {
        List<GameObject> parts = new ArrayList<>();

        // Cores
        Color redBody = new Color(220, 20, 60); // Vermelho Carmesim
        Color darkRed = new Color(100, 0, 0);   // Vermelho Escuro (Detalhes/Cabine)
        Color spoilerColor = new Color(40, 40, 40); // Cinza Quase Preto
        Color tireColor = new Color(20, 20, 20);    // Preto Pneu
        
        // Chassi Principal (Corpo do carro)
        List<Triangle> chassiTris = new ArrayList<>();
        addBox(chassiTris, 0, 0, 0, 1.8f, 0.6f, 4.0f);
        parts.add(new GameObject(new Mesh(chassiTris), redBody));
        
        // Cabine (Vidros/Teto) - Recuada um pouco
        List<Triangle> cabinTris = new ArrayList<>();
        addBox(cabinTris, 0, 0.5f, -0.5f, 1.4f, 0.5f, 2.0f);
        parts.add(new GameObject(new Mesh(cabinTris), darkRed));
        
        // Spoiler Traseiro
        List<Triangle> spoilerTris = new ArrayList<>();
        addBox(spoilerTris, -0.7f, 0.5f, -1.8f, 0.1f, 0.4f, 0.1f); // Pilar Esq
        addBox(spoilerTris, 0.7f, 0.5f, -1.8f, 0.1f, 0.4f, 0.1f);  // Pilar Dir
        addBox(spoilerTris, 0, 0.8f, -1.8f, 1.8f, 0.1f, 0.4f);     // Asa
        parts.add(new GameObject(new Mesh(spoilerTris), spoilerColor));
        
        // Rodas (Simplificadas)
        List<Triangle> wheelTris = new ArrayList<>();
        float wheelY = -0.3f;
        float wheelX = 0.8f;
        float wheelZ = 1.2f;
        float wSize = 0.6f;
        addBox(wheelTris, -wheelX, wheelY,  wheelZ, 0.3f, wSize, wSize); // Traseira Esq
        addBox(wheelTris,  wheelX, wheelY,  wheelZ, 0.3f, wSize, wSize); // Traseira Dir
        addBox(wheelTris, -wheelX, wheelY, -wheelZ, 0.3f, wSize, wSize); // Dianteira Esq
        addBox(wheelTris,  wheelX, wheelY, -wheelZ, 0.3f, wSize, wSize); // Dianteira Dir
        parts.add(new GameObject(new Mesh(wheelTris), tireColor));

        return parts;
    }

    private void addBox(List<Triangle> triangles, float x, float y, float z, float w, float h, float d) {
        float hw = w / 2;
        float hh = h / 2;
        float hd = d / 2;

        Vertex v1 = new Vertex(x - hw, y - hh, z - hd);
        Vertex v2 = new Vertex(x + hw, y - hh, z - hd);
        Vertex v3 = new Vertex(x + hw, y + hh, z - hd);
        Vertex v4 = new Vertex(x - hw, y + hh, z - hd);
        Vertex v5 = new Vertex(x - hw, y - hh, z + hd);
        Vertex v6 = new Vertex(x + hw, y - hh, z + hd);
        Vertex v7 = new Vertex(x + hw, y + hh, z + hd);
        Vertex v8 = new Vertex(x - hw, y + hh, z + hd);

        // Frente
        triangles.add(new Triangle(v1, v2, v3)); triangles.add(new Triangle(v1, v3, v4));
        // Trás
        triangles.add(new Triangle(v6, v5, v8)); triangles.add(new Triangle(v6, v8, v7));
        // Topo
        triangles.add(new Triangle(v4, v3, v7)); triangles.add(new Triangle(v4, v7, v8));
        // Base
        triangles.add(new Triangle(v1, v5, v6)); triangles.add(new Triangle(v1, v6, v2));
        // Direita
        triangles.add(new Triangle(v2, v6, v7)); triangles.add(new Triangle(v2, v7, v3));
        // Esquerda
        triangles.add(new Triangle(v5, v1, v4)); triangles.add(new Triangle(v5, v4, v8));
    }
}