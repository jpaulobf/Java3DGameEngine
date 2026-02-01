package com.java3d.engine.primitives;

import com.java3d.engine.geometry.Mesh;
import com.java3d.engine.geometry.Triangle;
import com.java3d.engine.geometry.Vertex;
import java.util.ArrayList;
import java.util.List;

public class Capsule {

    private Mesh mesh;

    public Capsule(float radius, float height, int rings, int sectors) {
        List<Vertex> vertices = new ArrayList<>();
        List<Triangle> triangles = new ArrayList<>();

        // Altura do corpo cilíndrico
        float cylinderHeight = height - 2 * radius;
        if (cylinderHeight < 0) cylinderHeight = 0;
        float halfHeight = cylinderHeight / 2;

        int hemiRings = rings / 2;
        if (hemiRings < 1) hemiRings = 1;

        // Hemisfério Superior
        for (int i = 0; i <= hemiRings; i++) {
            double lat = (Math.PI / 2.0) * i / hemiRings;
            double y = radius * Math.cos(lat) + halfHeight;
            double r = radius * Math.sin(lat);

            for (int j = 0; j <= sectors; j++) {
                double lon = 2 * Math.PI * j / sectors;
                double x = r * Math.cos(lon);
                double z = r * Math.sin(lon);
                vertices.add(new Vertex((float) x, (float) y, (float) z));
            }
        }

        // Hemisfério Inferior
        for (int i = 0; i <= hemiRings; i++) {
            double lat = (Math.PI / 2.0) + (Math.PI / 2.0) * i / hemiRings;
            double y = radius * Math.cos(lat) - halfHeight;
            double r = radius * Math.sin(lat);

            for (int j = 0; j <= sectors; j++) {
                double lon = 2 * Math.PI * j / sectors;
                double x = r * Math.cos(lon);
                double z = r * Math.sin(lon);
                vertices.add(new Vertex((float) x, (float) y, (float) z));
            }
        }

        // Triangulação (conecta os anéis)
        int totalRings = 2 * hemiRings + 2; // Anéis gerados pelos dois loops
        for (int i = 0; i < totalRings - 1; i++) {
            for (int j = 0; j < sectors; j++) {
                int current = i * (sectors + 1) + j;
                int next = current + sectors + 1;

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