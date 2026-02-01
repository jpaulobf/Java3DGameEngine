package com.java3d.engine.scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Clouds {
    private List<Cloud> items;

    public Clouds() {
        items = new ArrayList<>();
        // Adicionar Nuvens 2D (No Horizonte)
        for (int i = 0; i < 15; i++) {
            float x = (float) (Math.random() * 360); // Posição em graus (0-360)
            float y = 50 + (float) (Math.random() * 100); // Altura acima do horizonte (pixels)
            float w = 60 + (float) (Math.random() * 80); // Largura
            float h = 20 + (float) (Math.random() * 20); // Altura
            items.add(new Cloud(x, y, w, h, new Color(255, 255, 255, 180)));
        }
    }

    public void update(float windSpeed, float camTrackAngle) {
        for (Cloud cloud : items) {
            // Movimento base do vento
            double move = windSpeed;
            
            // Movimento contrário à curva (Parallax)
            move -= camTrackAngle * 0.0125f; 
            
            cloud.setX(cloud.getX() + (float)move);
            
            // Manter dentro do ciclo 0-360 graus
            if (cloud.getX() > 360) cloud.setX(cloud.getX() - 360);
            if (cloud.getX() < 0) cloud.setX(cloud.getX() + 360);
        }
    }

    public void updateColor(float brightness) {
        int cloudAlpha = 180;
        int cloudGray = (int) (255 * brightness);
        Color cloudColor = new Color(cloudGray, cloudGray, cloudGray, cloudAlpha);
        for (Cloud cloud : items) cloud.setColor(cloudColor);
    }

    public List<Cloud> getItems() {
        return items;
    }
}