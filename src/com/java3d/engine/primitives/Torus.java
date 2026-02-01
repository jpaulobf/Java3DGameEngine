package com.java3d.engine.primitives;

import com.java3d.engine.geometry.Mesh;
import com.java3d.engine.geometry.Triangle;
import com.java3d.engine.geometry.Vertex;
import java.util.ArrayList;
import java.util.List;

public class Torus {

    private Mesh mesh;

    public Torus(float mainRadius, float tubeRadius, int mainSegments, int tubeSegments) {
        List<Vertex> vertices = new ArrayList<>();
        List<Triangle> triangles = new ArrayList<>();

        // Gerar vértices
        for (int i = 0; i <= mainSegments; i++) {
            double theta = 2 * Math.PI * i / mainSegments; // Ângulo do anel principal
            double cosTheta = Math.cos(theta);
            double sinTheta = Math.sin(theta);

            for (int j = 0; j <= tubeSegments; j++) {
                double phi = 2 * Math.PI * j / tubeSegments; // Ângulo do tubo
                double cosPhi = Math.cos(phi);
                double sinPhi = Math.sin(phi);

                // Fórmula paramétrica do Toro
                double x = (mainRadius + tubeRadius * cosPhi) * cosTheta;
                double y = tubeRadius * sinPhi;
                double z = (mainRadius + tubeRadius * cosPhi) * sinTheta;

                vertices.add(new Vertex((float) x, (float) y, (float) z));
            }
        }

        // Gerar triângulos
        for (int i = 0; i < mainSegments; i++) {
            for (int j = 0; j < tubeSegments; j++) {
                int current = i * (tubeSegments + 1) + j;
                int next = current + tubeSegments + 1;

                Vertex v1 = vertices.get(current);
                Vertex v2 = vertices.get(current + 1);
                Vertex v3 = vertices.get(next + 1);
                Vertex v4 = vertices.get(next);

                triangles.add(new Triangle(v1, v2, v3));
                triangles.add(new Triangle(v1, v3, v4));
            }
        }
        this.mesh = new Mesh(triangles);
    }

    public Mesh getMesh() {
        return mesh;
    }
}