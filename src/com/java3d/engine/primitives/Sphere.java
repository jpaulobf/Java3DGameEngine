package com.java3d.engine.primitives;

import com.java3d.engine.geometry.Mesh;
import com.java3d.engine.geometry.Triangle;
import com.java3d.engine.geometry.Vertex;
import java.util.ArrayList;
import java.util.List;

public class Sphere {

    private Mesh mesh;

    public Sphere(float radius, int rings, int sectors) {
        List<Vertex> vertices = new ArrayList<>();
        List<Triangle> triangles = new ArrayList<>();

        // Gerar vértices
        for (int i = 0; i <= rings; i++) {
            double lat = Math.PI * i / rings;
            double y = radius * Math.cos(lat);
            double r = radius * Math.sin(lat);

            for (int j = 0; j <= sectors; j++) {
                double lon = 2 * Math.PI * j / sectors;
                double x = r * Math.cos(lon);
                double z = r * Math.sin(lon);

                vertices.add(new Vertex((float) x, (float) y, (float) z));
            }
        }

        // Gerar triângulos
        for (int i = 0; i < rings; i++) {
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