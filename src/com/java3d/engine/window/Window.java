package com.java3d.engine.window;

import com.java3d.engine.primitives.PrimitiveFactory;
import com.java3d.engine.renderer.Renderer;
import com.java3d.engine.scene.Camera;
import com.java3d.engine.scene.GameObject;
import com.java3d.engine.scene.Scene;
import com.java3d.engine.scene.Starfield;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Window extends JFrame {

    private Scene scene;
    private Renderer renderer;
    private JPanel canvas;
    private boolean w, a, s, d, space; // Flags de controle da nave
    private float currentSpeed = 0.3f;
    
    // Opção para alternar entre Renderização via Software (CPU) e Hardware (GPU/OpenGL)
    public static final boolean USE_GPU = false; 
    public static boolean flatLight = true; // Alternar entre Flat e Gouraud Shading

    public Window() {
        setTitle("Java 3D Game Engine");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicializar Cena e Renderer
        scene = new Scene();
        renderer = new Renderer();

        // Configurar Câmera
        scene.getCamera().setFov(75);
        
        // Configurar Luz
        scene.getPointLight().setPosition(0, 5, -10); // Luz mais atrás para iluminar a traseira da nave

        // Criar o campo de estrelas
        scene.setStarfield(new Starfield(2000, 200));

        // Adicionar a Nave
        GameObject spaceship = new GameObject(PrimitiveFactory.createSpaceship());
        spaceship.setPosition(0, 0, 0);
        scene.addGameObject(spaceship);

        // Configurar Canvas de Desenho
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Renderiza a cena passando o Graphics2D
                renderer.render((Graphics2D) g, scene, getWidth(), getHeight());
            }
        };
        add(canvas);

        // Controles de Teclado
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> w = true;
                    case KeyEvent.VK_S -> s = true;
                    case KeyEvent.VK_A -> a = true;
                    case KeyEvent.VK_D -> d = true;
                    case KeyEvent.VK_F -> flatLight = !flatLight; // Toggle Iluminação
                    case KeyEvent.VK_SPACE -> space = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> w = false;
                    case KeyEvent.VK_S -> s = false;
                    case KeyEvent.VK_A -> a = false;
                    case KeyEvent.VK_D -> d = false;
                    case KeyEvent.VK_SPACE -> space = false;
                }
            }
        });
        setFocusable(true);
        requestFocus();

        // Mouse Look (Controle de Câmera com Mouse)
        // Desativado para a cena do espaço

        // Game Loop (aprox. 60 FPS)
        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (scene.getGameObjects().isEmpty()) return;

                GameObject spaceship = scene.getGameObjects().get(0);
                float baseSpeed = 0.3f;
                float maxSpeed = baseSpeed * 100.0f;
                float acceleration = 0.1f;

                if (space) {
                    currentSpeed += acceleration;
                    if (currentSpeed > maxSpeed) currentSpeed = maxSpeed;
                } else {
                    currentSpeed -= (5 * acceleration);
                    if (currentSpeed < baseSpeed) currentSpeed = baseSpeed;
                }

                float strafeSpeed = 0.15f; // Velocidade lateral
                float tiltAngle = 20.0f; // Ângulo de inclinação ao virar

                // 1. Movimento Automático para Frente (Eixo Z)
                spaceship.setZ(spaceship.getZ() + currentSpeed);

                // 2. Controle Lateral e Rotação Visual (Inclinação)
                float targetRy = 0;
                float targetRx = 0;

                if (a) {
                    spaceship.setX(spaceship.getX() - strafeSpeed);
                    targetRy = -tiltAngle; // Inclina para esquerda
                } else if (d) {
                    spaceship.setX(spaceship.getX() + strafeSpeed);
                    targetRy = tiltAngle; // Inclina para direita
                }

                if (w) {
                    spaceship.setY(spaceship.getY() - strafeSpeed);
                    targetRx = tiltAngle; // Empina o nariz para cima
                } else if (s) {
                    spaceship.setY(spaceship.getY() + strafeSpeed);
                    targetRx = -tiltAngle; // Embica o nariz para baixo
                }
                
                // Suavização da rotação (Lerp simples)
                float currentRy = spaceship.getRy();
                spaceship.setRy(currentRy + (targetRy - currentRy) * 0.1f);

                float currentRx = spaceship.getRx();
                spaceship.setRx(currentRx + (targetRx - currentRx) * 0.1f);

                // 3. Câmera segue a posição da nave, mas com rotação fixa (olhando para frente)
                Camera cam = scene.getCamera();
                float camDist = 5.0f;
                float camHeight = 2.0f;
                
                // A câmera fica atrás da nave (Z - dist), mas alinhada no X e Y
                cam.setPosition(spaceship.getX(), spaceship.getY() + camHeight, spaceship.getZ() - camDist);
                cam.setYaw(0); // Câmera sempre alinhada com o horizonte (não gira com a nave)
                cam.setPitch(15); // Ajustado para deixar a nave mais acima na tela

                canvas.repaint();
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        if (USE_GPU) {
            System.out.println("Iniciando modo GPU (OpenGL/LWJGL)...");
            System.out.println("Atenção: Requer biblioteca LWJGL no classpath.");
            // Aqui iniciaríamos a janela GLFW e o loop OpenGL
            // runGpuLoop();
        } else {
            System.out.println("Iniciando modo CPU (Software Rendering)...");
            SwingUtilities.invokeLater(() -> {
                new Window().setVisible(true);
            });
        }
    }
}
