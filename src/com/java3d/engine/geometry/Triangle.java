package com.java3d.engine.geometry;

/**
 * Representa um triângulo no espaço 3D, composto por três vértices.
 */
public class Triangle {

    public Vertex v1;
    public Vertex v2;
    public Vertex v3;

    /**
     * Construtor que inicializa o triângulo com três vértices.
     * @param v1 O primeiro vértice do triângulo.
     * @param v2 O segundo vértice do triângulo.
     * @param v3 O terceiro vértice do triângulo.
     */
    public Triangle(Vertex v1, Vertex v2, Vertex v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    /**
     * Método auxiliar para facilitar a depuração no console.
     */
    @Override
    public String toString() {
        return "Triangle{" + "v1=" + v1 + ", v2=" + v2 + ", v3=" + v3 + '}';
    }

    public Vertex getV1() {
        return v1;
    }

    public void setV1(Vertex v1) {
        this.v1 = v1;
    }

    public Vertex getV2() {
        return v2;
    }

    public void setV2(Vertex v2) {
        this.v2 = v2;
    }

    public Vertex getV3() {
        return v3;
    }

    public void setV3(Vertex v3) {
        this.v3 = v3;
    }

    
}
