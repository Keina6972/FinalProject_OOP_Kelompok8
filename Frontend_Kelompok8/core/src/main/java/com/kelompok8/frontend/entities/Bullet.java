package com.kelompok8.frontend.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Bullet extends Entity {
    private float velX, velY;
    private float damage;

    public Bullet(float startX, float startY, float targetX, float targetY, float damage) {
        super(startX, startY, 8, 8, 500.0f);
        this.damage = damage;

        float deltaX = targetX - startX;
        float deltaY = targetY - startY;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance != 0) {
            this.velX = (deltaX / distance) * speed;
            this.velY = (deltaY / distance) * speed;
        } else {
            this.velX = 0;
            this.velY = speed;
        }
    }

    @Override
    public void update() {
        float dt = Gdx.graphics.getDeltaTime();
        x += velX * dt;
        y += velY * dt;

        if (x < -20 || x > Gdx.graphics.getWidth() + 20 || y < -20 || y > Gdx.graphics.getHeight() + 20) {
            active = false;
        }
    }

    @Override
    public void render(ShapeRenderer shape) {
        shape.setColor(Color.YELLOW);
        shape.circle(x + width/2f, y + height/2f, width/2f);
    }

    public float getDamage() { return damage; }
}
