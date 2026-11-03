package com.kelompok8.frontend.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public abstract class Entity {
    protected float x, y;
    protected float width, height;
    protected float speed;
    protected boolean active = true;

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
        return new Rectangle(x, y, width, height);
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public float getX() { return x; }
    public float getY() { return y; }
}
