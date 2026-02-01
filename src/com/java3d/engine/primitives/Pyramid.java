package com.java3d.engine.primitives;

import com.java3d.engine.geometry.Mesh;
import com.java3d.engine.geometry.Triangle;
import com.java3d.engine.geometry.Vertex;
import java.util.Arrays;

/**
 * Representa uma forma de pirâmide de base quadrada.
 * Esta classe contém a malha (Mesh) que representa a geometria da pirâmide.
 */
public class Pyramid {

    private final Mesh mesh;

    /**
     * Cria uma nova forma de pirâmide com as dimensões especificadas.
     * A base da pirâmide fica no plano XZ, centrada na origem, e a pirâmide se estende para cima no eixo Y.
     *
     * @param baseSize A largura e profundidade da base quadrada.
     * @param height   A altura da pirâmide a partir da base até o ápice.
     */
    public Pyramid(float baseSize, float height) {
        this.mesh = new Mesh();
        
        float s = baseSize / 2f;

        // Os 5 vértices de uma pirâmide de base quadrada
        // 4 vértices da base no plano Y=0
        Vertex v1 = new Vertex(-s, 0, -s); // Base, frente, esquerda
        Vertex v2 = new Vertex( s, 0, -s); // Base, frente, direita
        Vertex v3 = new Vertex( s, 0,  s); // Base, trás, direita
        Vertex v4 = new Vertex(-s, 0,  s); // Base, trás, esquerda
        // 1 vértice no ápice (topo)
        Vertex apex = new Vertex(0, height, 0);

        // Os 6 triângulos que formam a pirâmide
        
        // Base (2 triângulos)
        Triangle base1 = new Triangle(v1, v2, v3);
        Triangle base2 = new Triangle(v1, v3, v4);

        // Faces laterais (4 triângulos, com normais para fora)
        Triangle side1 = new Triangle(v1, apex, v2); // Face frontal
        Triangle side2 = new Triangle(v2, apex, v3); // Face direita
        Triangle side3 = new Triangle(v3, apex, v4); // Face traseira
        Triangle side4 = new Triangle(v4, apex, v1); // Face esquerda

        this.mesh.triangles.addAll(Arrays.asList(
            base1, base2,
            side1, side2,
            side3, side4
        ));
    }

    /**
     * Retorna a malha (Mesh) que representa esta pirâmide.
     * @return O objeto Mesh da pirâmide.
     */
    public Mesh getMesh() {
        return this.mesh;
    }
}
