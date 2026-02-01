package com.java3d.engine.primitives;

import com.java3d.engine.geometry.Mesh;
import com.java3d.engine.geometry.Triangle;
import com.java3d.engine.geometry.Vertex;
import java.util.ArrayList;
import java.util.Arrays;

public class Plane {

    private Mesh mesh;

    public Plane(float width, float depth) {
        Vertex v1 = new Vertex(-width / 2, 0, -depth / 2);
        Vertex v2 = new Vertex(width / 2, 0, -depth / 2);
        Vertex v3 = new Vertex(width / 2, 0, depth / 2);
        Vertex v4 = new Vertex(-width / 2, 0, depth / 2);

        Triangle t1 = new Triangle(v1, v2, v3);
        Triangle t2 = new Triangle(v1, v3, v4);

        this.mesh = new Mesh(new ArrayList<>(Arrays.asList(t1, t2)));
    }

    public Mesh getMesh() {
        return mesh;
    }
}
