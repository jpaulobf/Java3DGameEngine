package com.java3d.engine.window;

import com.java3d.engine.geometry.Mesh;
import com.java3d.engine.geometry.Triangle;
import com.java3d.engine.geometry.Vertex;
import com.java3d.engine.renderer.Renderer;
import com.java3d.engine.scene.Camera;
import com.java3d.engine.scene.GameObject;
import com.java3d.engine.scene.Road;
import com.java3d.engine.scene.Scene;

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
    private float speed = 0.0f;
    private float maxSpeed = 1.5f;
    
    public WindowRace() {
        setTitle("Java 3D Racing Demo");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        scene = new Scene();
        renderer = new Renderer();

        // Configurar Câmera
        scene.setBackgroundColor(0x87CEEB); // Céu Azul (Sky Blue)
        scene.setGroundColor(0x228B22); // Grama (Forest Green)
        scene.getCamera().setFov(80);
        scene.getCamera().setPosition(0, 3, -8); // Câmera mais alta e atrás
        scene.getCamera().setPitch(15);

        // Configurar Estrada
        Road road = new Road();
        scene.setRoad(road);

        // Adicionar o "Carro" (Modelo customizado)
        GameObject car = new GameObject(createCarMesh());
        car.setPosition(0, -1.25f, 0); // Posicionado logo acima do chão (-2.0)
        scene.addGameObject(car);

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
                GameObject car = scene.getGameObjects().get(0);
                Camera cam = scene.getCamera();

                // Aceleração
                if (accel) {
                    speed += 0.02f;
                    if (speed > maxSpeed) speed = maxSpeed;
                } else {
                    speed -= 0.01f;
                    if (speed < 0) speed = 0;
                }

                // Movimento Lateral
                float turnSpeed = 0.2f;
                if (left && speed > 0) {
                    car.setX(car.getX() - turnSpeed);
                    car.setRy(-10); // Inclina visualmente
                } else if (right && speed > 0) {
                    car.setX(car.getX() + turnSpeed);
                    car.setRy(10);
                } else {
                    car.setRy(0);
                }

                // Mover Carro para frente
                car.setZ(car.getZ() + speed);

                // Câmera segue o carro
                cam.setPosition(car.getX() * 0.5f, 3, car.getZ() - 8);

                // Atualizar Estrada (Gerar segmentos à frente do carro)
                scene.getRoad().update(car.getZ());

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

    private Mesh createCarMesh() {
        List<Triangle> triangles = new ArrayList<>();
        
        // Chassi Principal (Corpo do carro)
        addBox(triangles, 0, 0, 0, 1.8f, 0.6f, 4.0f); 
        
        // Cabine (Vidros/Teto) - Recuada um pouco
        addBox(triangles, 0, 0.5f, -0.5f, 1.4f, 0.5f, 2.0f);
        
        // Spoiler Traseiro
        // Pilares
        addBox(triangles, -0.7f, 0.5f, -1.8f, 0.1f, 0.4f, 0.1f);
        addBox(triangles, 0.7f, 0.5f, -1.8f, 0.1f, 0.4f, 0.1f);
        // Asa
        addBox(triangles, 0, 0.8f, -1.8f, 1.8f, 0.1f, 0.4f);
        
        // Rodas (Simplificadas)
        float wheelY = -0.3f;
        float wheelX = 0.8f;
        float wheelZ = 1.2f;
        float wSize = 0.6f;
        
        addBox(triangles, -wheelX, wheelY,  wheelZ, 0.3f, wSize, wSize); // Traseira Esq
        addBox(triangles,  wheelX, wheelY,  wheelZ, 0.3f, wSize, wSize); // Traseira Dir
        addBox(triangles, -wheelX, wheelY, -wheelZ, 0.3f, wSize, wSize); // Dianteira Esq
        addBox(triangles,  wheelX, wheelY, -wheelZ, 0.3f, wSize, wSize); // Dianteira Dir

        return new Mesh(triangles);
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