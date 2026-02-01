package com.java3d.engine.scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Road {
    public static class Segment {
        public float z;
        public float x;     // Centro X no início do segmento
        public float endX;  // Centro X no final do segmento
        public float width;
        public float length;
        public Color color;

        public Segment(float z, float x, float endX, float width, float length, Color color) {
            this.z = z;
            this.x = x;
            this.endX = endX;
            this.width = width;
            this.length = length;
            this.color = color;
        }
    }

    private List<Segment> segments = new ArrayList<>();
    private float segmentLength = 4.0f;
    private float roadWidth = 14.0f;
    private int drawDistance = 380; // Aumentado para garantir que a estrada toque o horizonte
    
    // Estado da Geração Procedural
    private List<Float> xPositions = new ArrayList<>();
    private int maxIndexGenerated = -1;
    private float lastX = 0;
    private float lastDx = 0;

    public void update(float cameraZ) {
        segments.clear();
        
        // Calcular o índice do segmento onde a câmera está
        int startIndex = (int) (cameraZ / segmentLength);

        // Recuar o início para garantir que a estrada seja desenhada sob e atrás da câmera
        startIndex -= 12;
        
        int endIndex = startIndex + drawDistance;
        
        // Garantir que o mapa da estrada (posições X) esteja gerado até o fim da visão
        if (endIndex > 0) generateRoadMap(endIndex + 1);
        
        // Gerar segmentos à frente da câmera
        for (int i = startIndex; i < endIndex; i++) {
            float z = i * segmentLength;
            float x = (i >= 0 && i < xPositions.size()) ? xPositions.get(i) : 0;
            float nextX = (i + 1 >= 0 && i + 1 < xPositions.size()) ? xPositions.get(i+1) : 0;
            
            // Alternar cores para dar sensação de movimento (Cinza Claro / Cinza Escuro)
            Color color = (i % 2 == 0) ? new Color(105, 105, 105) : new Color(115, 115, 115);
            
            // Faixas laterais (Zebras) podem ser adicionadas aqui no futuro
            segments.add(new Segment(z, x, nextX, roadWidth, segmentLength, color));
        }
    }
    
    private void generateRoadMap(int targetIndex) {
        if (maxIndexGenerated == -1) {
            xPositions.add(0f);
            maxIndexGenerated = 0;
        }

        while (maxIndexGenerated < targetIndex) {
            maxIndexGenerated++;
            int i = maxIndexGenerated;
            
            // Lógica de Blocos: 200 segmentos por bloco
            // Bloco Par: Reta | Bloco Ímpar: Curva
            int blockSize = 200;
            int blockIndex = i / blockSize;
            
            float ddx = 0;
            if (blockIndex % 2 != 0) {
                // Curva
                // Alternar direção: Direita (1), Esquerda (-1)
                float direction = ((blockIndex / 2) % 2 == 0) ? 1.0f : -1.0f;
                ddx = direction * 0.02f; // Força da curva
            } else {
                // Reta
                // Suavizar a curva anterior até ficar reto (decair dx)
                lastDx *= 0.99f; 
            }
            
            lastDx += ddx;
            
            // Limitar a inclinação máxima para não ficar injogável
            if (lastDx > 2.0f) lastDx = 2.0f;
            if (lastDx < -2.0f) lastDx = -2.0f;
            
            lastX += lastDx;
            xPositions.add(lastX);
        }
    }

    public List<Segment> getSegments() {
        return segments;
    }

    /**
     * Calcula a posição X do centro da pista para uma determinada coordenada Z.
     * Interpola linearmente a posição X entre os segmentos da estrada.
     * @param z A coordenada Z para a qual se deseja encontrar o X da pista.
     * @return A coordenada X do centro da pista.
     */
    public float getTrackX(float z) {
        if (xPositions.isEmpty()) return 0;

        int index = (int) (z / segmentLength);
        if (index < 0) index = 0;
        
        // Garante que o mapa seja gerado o suficiente para a interpolação
        generateRoadMap(index + 2);

        if (index >= xPositions.size() - 1) return xPositions.get(xPositions.size() - 1);

        float startX = xPositions.get(index);
        float endX = xPositions.get(index + 1);
        float segmentProgress = (z % segmentLength) / segmentLength;
        return startX + (endX - startX) * segmentProgress;
    }
}