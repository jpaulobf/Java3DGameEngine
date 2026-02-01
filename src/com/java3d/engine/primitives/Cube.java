package com.java3d.engine.primitives;

import com.java3d.engine.geometry.Mesh;
import com.java3d.engine.geometry.Triangle;
import com.java3d.engine.geometry.Vertex;
import java.util.ArrayList;
import java.util.List;

public class Cube {

    private Mesh mesh;

    public Cube(float size) {
        float s = size / 2.0f;

        // 8 Vértices do cubo
        Vertex v0 = new Vertex(-s, -s, -s);
        Vertex v1 = new Vertex(s, -s, -s);
        Vertex v2 = new Vertex(s, s, -s);
        Vertex v3 = new Vertex(-s, s, -s);
        Vertex v4 = new Vertex(-s, -s, s);
        Vertex v5 = new Vertex(s, -s, s);
        Vertex v6 = new Vertex(s, s, s);
        Vertex v7 = new Vertex(-s, s, s);

        List<Triangle> triangles = new ArrayList<>();

        // Frente (Z+)
        triangles.add(new Triangle(v4, v5, v6));
        triangles.add(new Triangle(v4, v6, v7));

        // Trás (Z-)
        triangles.add(new Triangle(v1, v0, v3));
        triangles.add(new Triangle(v1, v3, v2));

        // Topo (Y+)
        triangles.add(new Triangle(v3, v0, v4)); // Correção de winding para apontar para cima/fora depende da câmera, mas mantendo padrão
        triangles.add(new Triangle(v3, v4, v7)); // Ajuste: Topo geralmente é (3, 2, 6, 7) ou similar. Vamos usar (v7, v6, v2, v3)

        // Vamos redefinir faces explicitamente para garantir winding CCW (Counter-Clockwise) externo
        triangles.clear();
        addQuad(triangles, v4, v5, v6, v7); // Frente
        addQuad(triangles, v1, v0, v3, v2); // Trás
        addQuad(triangles, v7, v6, v2, v3); // Topo
        addQuad(triangles, v0, v1, v5, v4); // Base
        addQuad(triangles, v5, v1, v2, v6); // Direita
        addQuad(triangles, v0, v4, v7, v3); // Esquerda

        this.mesh = new Mesh(triangles);
    }

    private void addQuad(List<Triangle> tris, Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
        tris.add(new Triangle(v1, v2, v3));
        tris.add(new Triangle(v1, v3, v4));
    }

    public Mesh getMesh() {
        return mesh;
    }
}