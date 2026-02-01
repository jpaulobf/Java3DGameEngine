package com.java3d.engine.scene;

public class Camera {

    private float x, y, z;
    private float pitch, yaw, roll;
    private float fov;

    public Camera(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.fov = 90.0f; // Campo de visão padrão em graus
    }

    public void move(float dx, float dy, float dz) {
        this.x += dx;
        this.y += dy;
        this.z += dz;
    }

    public void rotate(float dPitch, float dYaw, float dRoll) {
        this.pitch += dPitch;
        this.yaw += dYaw;
        this.roll += dRoll;
    }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public float getZ() { return z; }
    public void setZ(float z) { this.z = z; }

    public float getPitch() { return pitch; }
    public void setPitch(float pitch) { this.pitch = pitch; }

    public float getYaw() { return yaw; }
    public void setYaw(float yaw) { this.yaw = yaw; }

    public float getRoll() { return roll; }
    public void setRoll(float roll) { this.roll = roll; }

    public float getFov() { return fov; }
    public void setFov(float fov) { this.fov = fov; }

    public void setPosition(float camX, float camY, float camZ) {
        this.x = camX;
        this.y = camY;
        this.z = camZ;
    }
}