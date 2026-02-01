package com.java3d.engine.primitives;

import com.java3d.engine.geometry.Mesh;

public class PrimitiveFactory {

    private PrimitiveFactory() {
        // Previne instanciação de classe utilitária
    }

    public static Mesh createCube(float size) {
        return new Cube(size).getMesh();
    }

    public static Mesh createSphere(float radius, int rings, int sectors) {
        return new Sphere(radius, rings, sectors).getMesh();
    }

    public static Mesh createPlane(float width, float depth) {
        return new Plane(width, depth).getMesh();
    }

    public static Mesh createCone(float radius, float height, int segments) {
        return new Cone(radius, height, segments).getMesh();
    }

    public static Mesh createCylinder(float radius, float height, int segments) {
        return new Cylinder(radius, height, segments).getMesh();
    }

    public static Mesh createTorus(float mainRadius, float tubeRadius, int mainSegments, int tubeSegments) {
        return new Torus(mainRadius, tubeRadius, mainSegments, tubeSegments).getMesh();
    }

    public static Mesh createCapsule(float radius, float height, int rings, int sectors) {
        return new Capsule(radius, height, rings, sectors).getMesh();
    }

    public static Mesh createSpaceship() {
        return new Spaceship().getMesh();
    }
}