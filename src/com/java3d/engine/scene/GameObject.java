package com.java3d.engine.scene;

import com.java3d.engine.geometry.Mesh;

import java.awt.Color;

public class GameObject {

    private Mesh mesh;
    private float x, y, z;
    private float rx, ry, rz;
    private Color color = Color.WHITE;

    public GameObject(Mesh mesh) {
        this.mesh = mesh;
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public GameObject(Mesh mesh, Color color) {
        this(mesh);
        this.color = color;
    }

    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getRx() { return rx; }
    public void setRx(float rx) { this.rx = rx; }

    public float getRy() { return ry; }
    public void setRy(float ry) { this.ry = ry; }

    public float getRz() { return rz; }
    public void setRz(float rz) { this.rz = rz; }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}