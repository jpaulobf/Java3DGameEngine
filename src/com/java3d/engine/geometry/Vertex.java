package com.java3d.engine.geometry;

/**
 * Representa um ponto no espaço 3D.
 * O sistema de coordenadas assumido geralmente é:
 * X: Esquerda/Direita
 * Y: Cima/Baixo
 * Z: Profundidade (Frente/Trás)
 */
public class Vertex {

    public float x;
    public float y;
    public float z;

    /**
     * Construtor padrão (origem)
     */
    public Vertex() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    /**
     * Construtor com coordenadas
     */
    public Vertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Método auxiliar para facilitar a depuração no console.
     */
    @Override
    public String toString() {
        return "Vertex{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    
}
