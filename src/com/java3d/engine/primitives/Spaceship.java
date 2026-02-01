package com.java3d.engine.primitives;

import com.java3d.engine.geometry.Mesh;
import com.java3d.engine.geometry.Triangle;
import com.java3d.engine.geometry.Vertex;
import java.util.ArrayList;
import java.util.List;

public class Spaceship {

    private Mesh mesh;

    public Spaceship() {
        List<Triangle> triangles = new ArrayList<>();

        // Geometria estilo "Caça Estelar" (Starfighter)
        // Aumentei a escala em 2x para garantir visibilidade
        float length = 1.2f;  // Comprimento do nariz
        float width = 0.8f;   // Largura das asas
        float height = 0.2f;  // Altura da fuselagem
        float engineZ = -0.5f; // Posição traseira

        Vertex nose = new Vertex(0, 0, length);
        Vertex wingL = new Vertex(-width, 0, engineZ);
        Vertex wingR = new Vertex(width, 0, engineZ);
        Vertex bodyTop = new Vertex(0, height, engineZ);
        Vertex bodyBottom = new Vertex(0, -height, engineZ);

        // Corpo / Asas (Topo)
        triangles.add(new Triangle(nose, bodyTop, wingL)); // Corrigido Winding
        triangles.add(new Triangle(nose, wingR, bodyTop)); // Corrigido Winding

        // Corpo / Asas (Baixo)
        triangles.add(new Triangle(nose, wingL, bodyBottom));
        triangles.add(new Triangle(nose, bodyBottom, wingR));

        // Traseira (Motores) - Fechando o modelo
        triangles.add(new Triangle(wingL, bodyTop, bodyBottom));
        triangles.add(new Triangle(wingR, bodyBottom, bodyTop));

        this.mesh = new Mesh(triangles);
    }

    public Mesh getMesh() {
        return mesh;
    }
}