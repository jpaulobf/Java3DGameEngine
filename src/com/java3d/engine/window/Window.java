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
    private boolean w, a, s, d; // Flags de controle da nave
    
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
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> w = false;
                    case KeyEvent.VK_S -> s = false;
                    case KeyEvent.VK_A -> a = false;
                    case KeyEvent.VK_D -> d = false;                    
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
                float moveSpeed = 0.2f;
                float rotSpeed = 2.5f;

                // Controle da Nave
                if (a) spaceship.setRy(spaceship.getRy() + rotSpeed);
                if (d) spaceship.setRy(spaceship.getRy() - rotSpeed);

                if (w) {
                    float rad = (float) Math.toRadians(spaceship.getRy());
                    float dx = (float) Math.sin(rad) * moveSpeed;
                    float dz = (float) Math.cos(rad) * moveSpeed;
                    spaceship.setPosition(spaceship.getX() + dx, spaceship.getY(), spaceship.getZ() + dz);
                }

                // Câmera em terceira pessoa seguindo a nave
                Camera cam = scene.getCamera();
                float camDist = 5.0f;
                float camHeight = 2.0f;

                float shipRad = (float) Math.toRadians(spaceship.getRy());
                float camX = spaceship.getX() - (float) (Math.sin(shipRad) * camDist);
                float camY = spaceship.getY() + camHeight;
                float camZ = spaceship.getZ() - (float) (Math.cos(shipRad) * camDist);
                
                cam.setPosition(camX, camY, camZ);
                cam.setYaw(spaceship.getRy()); // Câmera olha na mesma direção da nave
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
