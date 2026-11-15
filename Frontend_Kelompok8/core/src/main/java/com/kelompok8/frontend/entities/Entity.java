package com.kelompok8.frontend.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public abstract class Entity {
    protected float x, y;
    protected float width, height;
    protected float speed;
    protected boolean active = true;
    private final Rectangle bounds = new Rectangle();

    public Entity(float x, float y, float width, float height, float speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
    }

    public abstract void update();
    public abstract void render(ShapeRenderer shape);

    public Rectangle getBounds() {
        bounds.set(x, y, width, height);
        return bounds;
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public float getCenterX() { return x + width / 2f; }
    public float getCenterY() { return y + height / 2f; }
}
