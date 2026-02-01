package com.java3d.engine.window;

import com.java3d.engine.renderer.IRenderer;
import com.java3d.engine.renderer.SoftwareRenderer;
import com.java3d.engine.scene.Camera;
import com.java3d.engine.scene.GameObject;
import com.java3d.engine.scene.Road;
import com.java3d.engine.scene.Scene;
import com.java3d.engine.scene.Car;

import java.awt.Color;
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

public class Race extends JFrame {

    private Scene scene;
    private IRenderer renderer;
    private JPanel canvas;
    private boolean left, right, accel;
    private float dayCycleTime = 0.0f;
    private float dayCycleDuration = 120.0f; // Duração do ciclo em segundos (2 minutos)
    private Car car;
    private int cameraMode = 0; // 0=Normal, 1=Helicopter, 2=First Person, 3=Rear Windshield
    
    public Race() {
        setTitle("Java 3D Racing Demo");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        scene = new Scene();
        renderer = new SoftwareRenderer();

        // Ativar iluminação Flat para que o carro (feito de cubos) tenha faces definidas
        Space.flatLight = true;

        // Configurar Câmera
        scene.setBackgroundColor(0x87CEEB); // Céu Azul (Sky Blue)
        scene.setGroundColor(0x228B22); // Grama (Forest Green)
        scene.getCamera().setFov(80);
        scene.getCamera().setPosition(0, 3, -8); // Câmera mais alta e atrás
        scene.getCamera().setPitch(15);

        // Configurar Estrada
        Road road = new Road();
        scene.setRoad(road);

        // Adicionar o "Carro"
        car = new Car();
        for (GameObject part : car.getParts()) {
            scene.addGameObject(part);
        }

        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                renderer.render((Graphics2D) g, scene, getWidth(), getHeight(), car.getSpeed() * 10);
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
                    case KeyEvent.VK_M -> car.toggleHeadlights(); // Alternar faróis
                    case KeyEvent.VK_C -> cameraMode = (cameraMode + 1) % 4; // Alternar Câmera
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

                // Atualizar Ciclo Dia/Noite
                updateDayNightCycle(0.016f);

                // Atualizar Carro
                car.update(accel, left, right, scene.getRoad());

                // 2. Calcular o ângulo da pista para a CÂMERA (horizonte distante)
                // Olhar bem à frente (200m) garante que a próxima reta fique centralizada na tela
                float camLookAhead = 200.0f; 
                float trackCenterX = scene.getRoad().getTrackX(car.getZ());
                float nextCamTrackX = scene.getRoad().getTrackX(car.getZ() + camLookAhead);
                float camDx = nextCamTrackX - trackCenterX;
                float camTrackAngle = (float) Math.toDegrees(Math.atan2(camDx, camLookAhead));

                // Câmera segue o carro (com efeito de shake se houver colisão)
                float collisionShake = car.getCollisionShake();
                float shakeX = (float) ((Math.random() - 0.5) * collisionShake);
                float shakeY = (float) ((Math.random() - 0.5) * collisionShake);
                
                switch (cameraMode) {
                    case 0: // Normal (3rd Person)
                        cam.setPosition(car.getX() + shakeX, 3 + shakeY, car.getZ() - 8);
                        cam.setPitch(15);
                        break;
                    case 1: // Helicopter (Top-Down)
                        cam.setPosition(car.getX(), 20, car.getZ() - 15);
                        cam.setPitch(40);
                        break;
                    case 2: // First Person (Bumper/Nose)
                        cam.setPosition(car.getX() + shakeX, -0.8f + shakeY, car.getZ() + 2.2f);
                        cam.setPitch(0);
                        break;
                    case 3: // Camera 4 (Afastada 2m do carro)
                        cam.setPosition(car.getX() + shakeX, 1f + shakeY, car.getZ() - 8.0f);
                        cam.setPitch(0);
                        break;
                }
                
                // Rotacionar câmera para acompanhar o horizonte (manter a próxima reta centralizada)
                cam.setYaw(-camTrackAngle);

                // Atualizar estado dos faróis na estrada
                scene.getRoad().setHeadlightsOn(car.isHeadlightsOn());

                // Atualizar Nuvens (Vento e Parallax)
                float windSpeed = 0.0005f; // Vento bem mais devagar
                scene.getClouds().update(windSpeed, camTrackAngle);

                // Atualizar Estrada (Gerar segmentos à frente do carro)
                scene.getRoad().update(car.getZ());

                // Atualizar Posição do Sol (Luz) para seguir o carro
                scene.getPointLight().setPosition(car.getX() - 60, 100, car.getZ() + 60);

                canvas.repaint();
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Race().setVisible(true);
        });
    }

    private void updateDayNightCycle(float dt) {
        dayCycleTime += dt;
        if (dayCycleTime >= dayCycleDuration) dayCycleTime %= dayCycleDuration;

        float t = dayCycleTime / dayCycleDuration; // 0.0 a 1.0

        // Cores do Céu
        Color daySky = new Color(135, 206, 235);     // Sky Blue
        Color sunsetSky = new Color(255, 69, 0);     // Orange Red
        Color nightSky = new Color(10, 10, 30);      // Very Dark Blue
        Color dawnSky = new Color(219, 112, 147);    // Pale Violet Red

        // Cores do Chão (Grama)
        Color dayGround = new Color(34, 139, 34);    // Forest Green
        Color nightGround = new Color(5, 20, 5);     // Very Dark Green

        Color currentSky;
        Color currentGround;

        if (t < 0.3f) { // Dia (30% do tempo)
            currentSky = daySky;
            currentGround = dayGround;
        } else if (t < 0.4f) { // Entardecer (10% do tempo)
            float localT = (t - 0.3f) / 0.1f;
            currentSky = lerpColor(daySky, sunsetSky, localT);
            currentGround = lerpColor(dayGround, nightGround, localT * 0.5f);
        } else if (t < 0.5f) { // Anoitecer (10% do tempo)
            float localT = (t - 0.4f) / 0.1f;
            currentSky = lerpColor(sunsetSky, nightSky, localT);
            currentGround = lerpColor(lerpColor(dayGround, nightGround, 0.5f), nightGround, localT);
        } else if (t < 0.8f) { // Noite (30% do tempo)
            currentSky = nightSky;
            currentGround = nightGround;
        } else if (t < 0.9f) { // Madrugada/Amanhecer (10% do tempo)
            float localT = (t - 0.8f) / 0.1f;
            currentSky = lerpColor(nightSky, dawnSky, localT);
            currentGround = lerpColor(nightGround, dayGround, localT * 0.5f);
        } else { // Nascer do Sol (10% do tempo)
            float localT = (t - 0.9f) / 0.1f;
            currentSky = lerpColor(dawnSky, daySky, localT);
            currentGround = lerpColor(lerpColor(nightGround, dayGround, 0.5f), dayGround, localT);
        }

        scene.setBackgroundColor(currentSky.getRGB());
        scene.setGroundColor(currentGround.getRGB());

        // Calcular brilho da pista baseado no ciclo (Dia = 1.0, Noite = 0.3)
        float brightness = 1.0f;
        if (t >= 0.3f && t < 0.4f) { // Entardecer
             brightness = 1.0f - ((t - 0.3f) / 0.1f) * 0.7f;
        } else if (t >= 0.4f && t < 0.8f) { // Noite
             brightness = 0.3f;
        } else if (t >= 0.8f && t < 0.9f) { // Amanhecer
             brightness = 0.3f + ((t - 0.8f) / 0.1f) * 0.7f;
        }
        scene.getRoad().setBrightness(brightness);

        scene.getClouds().updateColor(brightness);
    }
    private Color lerpColor(Color c1, Color c2, float t) {
        int r = (int) (c1.getRed() + t * (c2.getRed() - c1.getRed()));
        int g = (int) (c1.getGreen() + t * (c2.getGreen() - c1.getGreen()));
        int b = (int) (c1.getBlue() + t * (c2.getBlue() - c1.getBlue()));
        return new Color(Math.min(255, Math.max(0, r)), 
                         Math.min(255, Math.max(0, g)), 
                         Math.min(255, Math.max(0, b)));
    }
}