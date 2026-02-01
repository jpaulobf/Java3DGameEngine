package com.java3d.engine.scene;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    private List<GameObject> gameObjects;
    private Camera camera;
    private PointLight pointLight;
    private Starfield starfield;
    private List<Laser> lasers;
    private Road road;
    private int backgroundColor = 0x000000; // Cor de fundo padrão (Preto)
    private int groundColor = -1; // Cor do chão (-1 = desativado/transparente)

    public Scene() {
        this.gameObjects = new ArrayList<>();
        this.lasers = new ArrayList<>();
        this.camera = new Camera(0, 0, 0);
        this.pointLight = new PointLight(0, 5, 0); // Luz padrão acima da origem
        this.starfield = null;
        this.road = null;
    }

    public void addGameObject(GameObject gameObject) {
        this.gameObjects.add(gameObject);
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public Starfield getStarfield() {
        return starfield;
    }

    public void setStarfield(Starfield starfield) {
        this.starfield = starfield;
    }

    public List<Laser> getLasers() {
        return lasers;
    }

    public Road getRoad() {
        return road;
    }

    public void setRoad(Road road) {
        this.road = road;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getGroundColor() {
        return groundColor;
    }

    public void setGroundColor(int groundColor) {
        this.groundColor = groundColor;
    }
}