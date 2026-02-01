package com.java3d.engine.geometry;

import java.util.List;

public class Mesh {

    public List<Triangle> triangles;

    public Mesh(List<Triangle> triangles) {
        this.triangles = triangles;
    }

    public Mesh() {
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }

    // Converte a lista de triângulos em um array linear de floats (x, y, z) para o OpenGL
    public float[] getOpenGLVertices() {
        // 3 vértices por triângulo * 3 coordenadas (x, y, z) por vértice
        float[] buffer = new float[triangles.size() * 3 * 3];
        int i = 0;

        for (Triangle t : triangles) {
            // Vértice 1
            buffer[i++] = t.v1.getX();
            buffer[i++] = t.v1.getY();
            buffer[i++] = t.v1.getZ();

            // Vértice 2
            buffer[i++] = t.v2.getX();
            buffer[i++] = t.v2.getY();
            buffer[i++] = t.v2.getZ();

            // Vértice 3
            buffer[i++] = t.v3.getX();
            buffer[i++] = t.v3.getY();
            buffer[i++] = t.v3.getZ();
        }
        return buffer;
    }
}