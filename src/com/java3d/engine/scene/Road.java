package com.java3d.engine.scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Road {
    public static class Segment {
        public float z;
        public float width;
        public float length;
        public Color color;

        public Segment(float z, float width, float length, Color color) {
            this.z = z;
            this.width = width;
            this.length = length;
            this.color = color;
        }
    }

    private List<Segment> segments = new ArrayList<>();
    private float segmentLength = 4.0f;
    private float roadWidth = 14.0f;
    private int drawDistance = 380; // Aumentado para garantir que a estrada toque o horizonte

    public void update(float cameraZ) {
        segments.clear();
        
        // Calcular o índice do segmento onde a câmera está
        int startIndex = (int) (cameraZ / segmentLength);

        // Recuar o início para garantir que a estrada seja desenhada sob e atrás da câmera
        startIndex -= 12;
        
        // Gerar segmentos à frente da câmera
        for (int i = startIndex; i < startIndex + drawDistance; i++) {
            float z = i * segmentLength;
            
            // Alternar cores para dar sensação de movimento (Cinza Claro / Cinza Escuro)
            Color color = (i % 2 == 0) ? new Color(105, 105, 105) : new Color(115, 115, 115);
            
            // Faixas laterais (Zebras) podem ser adicionadas aqui no futuro
            segments.add(new Segment(z, roadWidth, segmentLength, color));
        }
    }

    public List<Segment> getSegments() {
        return segments;
    }
}