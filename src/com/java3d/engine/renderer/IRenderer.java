package com.java3d.engine.renderer;

import com.java3d.engine.scene.Scene;
import java.awt.Graphics2D;

public interface IRenderer {
    void render(Graphics2D g, Scene scene, int width, int height, float speed);
}