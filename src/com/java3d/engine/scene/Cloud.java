package com.java3d.engine.scene;

import java.awt.Color;

public class Cloud {
    private float x; // Posição horizontal (em graus, 0-360)
    private float y; // Altura acima do horizonte (pixels)
    private float width;
    private float height;
    private Color color;

    public Cloud(float x, float y, float width, float height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
}