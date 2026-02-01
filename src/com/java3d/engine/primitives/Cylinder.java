package com.java3d.engine.primitives;

import com.java3d.engine.geometry.Mesh;
import com.java3d.engine.geometry.Triangle;
import com.java3d.engine.geometry.Vertex;
import java.util.ArrayList;
import java.util.List;

public class Cylinder {

    private Mesh mesh;

    public Cylinder(float radius, float height, int segments) {
        List<Triangle> triangles = new ArrayList<>();
        List<Vertex> topVertices = new ArrayList<>();
        List<Vertex> bottomVertices = new ArrayList<>();

        float halfHeight = height / 2;
        Vertex topCenter = new Vertex(0, halfHeight, 0);
        Vertex bottomCenter = new Vertex(0, -halfHeight, 0);

        // Gerar anéis superior e inferior
        for (int i = 0; i < segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            float x = (float) (radius * Math.cos(angle));
            float z = (float) (radius * Math.sin(angle));

            topVertices.add(new Vertex(x, halfHeight, z));
            bottomVertices.add(new Vertex(x, -halfHeight, z));
        }

        for (int i = 0; i < segments; i++) {
            int nextI = (i + 1) % segments;

            Vertex topCurrent = topVertices.get(i);
            Vertex topNext = topVertices.get(nextI);
            Vertex bottomCurrent = bottomVertices.get(i);
            Vertex bottomNext = bottomVertices.get(nextI);

            // Tampa Superior (Topo -> Próximo -> Atual)
            triangles.add(new Triangle(topCenter, topNext, topCurrent));

            // Tampa Inferior (Centro -> Atual -> Próximo) - Invertido para normal apontar para baixo
            triangles.add(new Triangle(bottomCenter, bottomCurrent, bottomNext));

            // Lateral (formada por 2 triângulos/quad)
            // Triângulo 1: BottomCurrent -> TopCurrent -> TopNext
            triangles.add(new Triangle(bottomCurrent, topCurrent, topNext));
            // Triângulo 2: BottomCurrent -> TopNext -> BottomNext
            triangles.add(new Triangle(bottomCurrent, topNext, bottomNext));
        }

        this.mesh = new Mesh(triangles);
    }

    public Mesh getMesh() {
        return mesh;
    }
}