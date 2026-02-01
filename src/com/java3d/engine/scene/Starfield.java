package com.java3d.engine.scene;

import com.java3d.engine.geometry.Vertex;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Starfield {
    private final List<Vertex> stars;

    public Starfield(int numStars, float spread) {
        this.stars = new ArrayList<>(numStars);
        Random random = new Random();
        for (int i = 0; i < numStars; i++) {
            float x = (random.nextFloat() - 0.5f) * spread;
            float y = (random.nextFloat() - 0.5f) * spread;
            float z = (random.nextFloat() - 0.5f) * spread;
            stars.add(new Vertex(x, y, z));
        }
    }

    public List<Vertex> getStars() {
        return stars;
    }
}