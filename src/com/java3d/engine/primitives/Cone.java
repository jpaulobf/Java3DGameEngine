package com.java3d.engine.primitives;

import com.java3d.engine.geometry.Mesh;
import com.java3d.engine.geometry.Triangle;
import com.java3d.engine.geometry.Vertex;
import java.util.ArrayList;
import java.util.List;

public class Cone {

    private Mesh mesh;

    public Cone(float radius, float height, int segments) {
        List<Triangle> triangles = new ArrayList<>();

        float halfHeight = height / 2;
        Vertex tip = new Vertex(0, halfHeight, 0);
        Vertex baseCenter = new Vertex(0, -halfHeight, 0);

        List<Vertex> baseVertices = new ArrayList<>();
        for (int i = 0; i < segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            float x = (float) (radius * Math.cos(angle));
            float z = (float) (radius * Math.sin(angle));
            baseVertices.add(new Vertex(x, -halfHeight, z));
        }

        for (int i = 0; i < segments; i++) {
            Vertex current = baseVertices.get(i);
            Vertex next = baseVertices.get((i + 1) % segments);

            // Triângulo lateral (Base -> Próximo -> Topo)
            triangles.add(new Triangle(current, next, tip));

            // Triângulo da base (Centro -> Próximo -> Atual) - ordem para normal apontar para baixo
            triangles.add(new Triangle(baseCenter, next, current));
        }

        this.mesh = new Mesh(triangles);
    }

    public Mesh getMesh() {
        return mesh;
    }
}