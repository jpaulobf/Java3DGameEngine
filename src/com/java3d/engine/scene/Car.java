package com.java3d.engine.scene;

import com.java3d.engine.geometry.Mesh;
import com.java3d.engine.geometry.Triangle;
import com.java3d.engine.geometry.Vertex;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Car {

    private List<GameObject> parts = new ArrayList<>();
    private List<GameObject> headlights = new ArrayList<>();

    private float posOnTrackX = 0.0f; // Posição X relativa ao centro da pista
    private float worldX = 0.0f;
    private float z = 0.0f;
    private float speed = 0.0f;
    private float maxSpeed = 1.5f;
    private float collisionShake = 0.0f;
    private boolean headlightsOn = false;

    public Car() {
        createCarParts();
    }

    public void update(boolean accel, boolean left, boolean right, Road road) {
        // Aceleração
        if (accel) {
            speed += 0.02f;
            if (speed > maxSpeed) speed = maxSpeed;
        } else {
            speed -= 0.01f;
            if (speed < 0) speed = 0;
        }

        // Movimento Lateral (relativo à pista)
        float turnSpeed = 0.2f;
        if (left && speed > 0) {
            posOnTrackX -= turnSpeed;
        } else if (right && speed > 0) {
            posOnTrackX += turnSpeed;
        }

        // Colisão com Guardrails
        float roadHalfWidth = road.getRoadWidth() / 2.0f;
        float carHalfWidth = 0.9f; // Metade da largura do carro (1.8f)
        float limit = roadHalfWidth - carHalfWidth - 0.3f; // Margem para não entrar no guardrail

        if (posOnTrackX < -limit) {
            posOnTrackX = -limit;
            triggerCollision();
        } else if (posOnTrackX > limit) {
            posOnTrackX = limit;
            triggerCollision();
        }

        // Atualizar Z
        z += speed;

        // Calcular Posição no Mundo
        float trackCenterX = road.getTrackX(z);
        worldX = trackCenterX + posOnTrackX;

        // Calcular o ângulo da pista para o CARRO (tangente imediata)
        float lookAhead = 5.0f;
        float nextTrackX = road.getTrackX(z + lookAhead);
        float dx = nextTrackX - trackCenterX;
        float trackAngle = (float) Math.toDegrees(Math.atan2(dx, lookAhead));

        // Inclinação visual (Esterçamento)
        float steeringAngle = 0;
        if (left && speed > 0) steeringAngle = -10;
        else if (right && speed > 0) steeringAngle = 10;

        // Aplicar movimento a TODAS as partes do carro
        for (GameObject part : parts) {
            part.setPosition(worldX, -1.25f, z);
            part.setRy(trackAngle + steeringAngle);
        }

        // Decaimento do Shake
        if (collisionShake > 0) {
            collisionShake -= 0.02f;
            if (collisionShake < 0) collisionShake = 0;
        }

        // Atualizar cor dos faróis
        Color headlightColor = headlightsOn ? Color.YELLOW : new Color(60, 60, 0);
        for (GameObject hl : headlights) hl.setColor(headlightColor);
    }

    private void triggerCollision() {
        if (speed > 0.1f) {
            collisionShake = 0.1f; // Define intensidade do tremor
            speed *= 0.98f; // Leve atrito ao raspar no guardrail
        }
    }

    public void toggleHeadlights() {
        headlightsOn = !headlightsOn;
    }

    public List<GameObject> getParts() { return parts; }
    public float getX() { return worldX; }
    public float getZ() { return z; }
    public float getSpeed() { return speed; }
    public float getCollisionShake() { return collisionShake; }
    public boolean isHeadlightsOn() { return headlightsOn; }

    private void createCarParts() {
        // Cores
        Color redBody = new Color(220, 20, 60);
        Color darkRed = new Color(100, 0, 0);
        Color spoilerColor = new Color(40, 40, 40);
        Color tireColor = new Color(20, 20, 20);

        // Chassi Principal
        List<Triangle> chassiTris = new ArrayList<>();
        addBox(chassiTris, 0, 0, 0, 1.8f, 0.6f, 4.0f);
        parts.add(new GameObject(new Mesh(chassiTris), redBody));

        // Cabine
        List<Triangle> cabinTris = new ArrayList<>();
        addBox(cabinTris, 0, 0.5f, -0.5f, 1.4f, 0.5f, 2.0f);
        parts.add(new GameObject(new Mesh(cabinTris), darkRed));

        // Spoiler
        List<Triangle> spoilerTris = new ArrayList<>();
        addBox(spoilerTris, -0.7f, 0.5f, -1.8f, 0.1f, 0.4f, 0.1f);
        addBox(spoilerTris, 0.7f, 0.5f, -1.8f, 0.1f, 0.4f, 0.1f);
        addBox(spoilerTris, 0, 0.8f, -1.8f, 1.8f, 0.1f, 0.4f);
        parts.add(new GameObject(new Mesh(spoilerTris), spoilerColor));

        // Rodas
        List<Triangle> wheelTris = new ArrayList<>();
        float wx = 0.8f, wy = -0.3f, wz = 1.2f, ws = 0.6f;
        addBox(wheelTris, -wx, wy,  wz, 0.3f, ws, ws);
        addBox(wheelTris,  wx, wy,  wz, 0.3f, ws, ws);
        addBox(wheelTris, -wx, wy, -wz, 0.3f, ws, ws);
        addBox(wheelTris,  wx, wy, -wz, 0.3f, ws, ws);
        parts.add(new GameObject(new Mesh(wheelTris), tireColor));

        // Faróis
        List<Triangle> headlightTris = new ArrayList<>();
        addBox(headlightTris, -0.6f, 0.1f, 2.0f, 0.3f, 0.2f, 0.1f);
        addBox(headlightTris,  0.6f, 0.1f, 2.0f, 0.3f, 0.2f, 0.1f);
        GameObject hl = new GameObject(new Mesh(headlightTris), new Color(100, 100, 0));
        parts.add(hl);
        headlights.add(hl);
    }

    private void addBox(List<Triangle> triangles, float x, float y, float z, float w, float h, float d) {
        float hw = w / 2, hh = h / 2, hd = d / 2;
        Vertex v1 = new Vertex(x - hw, y - hh, z - hd);
        Vertex v2 = new Vertex(x + hw, y - hh, z - hd);
        Vertex v3 = new Vertex(x + hw, y + hh, z - hd);
        Vertex v4 = new Vertex(x - hw, y + hh, z - hd);
        Vertex v5 = new Vertex(x - hw, y - hh, z + hd);
        Vertex v6 = new Vertex(x + hw, y - hh, z + hd);
        Vertex v7 = new Vertex(x + hw, y + hh, z + hd);
        Vertex v8 = new Vertex(x - hw, y + hh, z + hd);

        triangles.add(new Triangle(v1, v2, v3)); triangles.add(new Triangle(v1, v3, v4));
        triangles.add(new Triangle(v6, v5, v8)); triangles.add(new Triangle(v6, v8, v7));
        triangles.add(new Triangle(v4, v3, v7)); triangles.add(new Triangle(v4, v7, v8));
        triangles.add(new Triangle(v1, v5, v6)); triangles.add(new Triangle(v1, v6, v2));
        triangles.add(new Triangle(v2, v6, v7)); triangles.add(new Triangle(v2, v7, v3));
        triangles.add(new Triangle(v5, v1, v4)); triangles.add(new Triangle(v5, v4, v8));
    }
}