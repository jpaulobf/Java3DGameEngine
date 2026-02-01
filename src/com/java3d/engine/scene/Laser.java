package com.java3d.engine.scene;

public class Laser {
    private float x, y, z;
    private float speed;

    public Laser(float x, float y, float z, float speed) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.speed = speed;
    }

    public void update() {
        z += speed;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }
}