package com.kelompok8.frontend.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.utils.WorldConfig;

public class Bullet extends Entity {
    private final float dirX, dirY;
    private final float damage;
    private final Color color;
    private final float maxRange;
    private float distanceTraveled = 0f;

    public Bullet(float startX, float startY, float dirX, float dirY, float damage) {
        this(startX, startY, dirX, dirY, damage, Color.YELLOW, -1f);
    }

    public Bullet(float startX, float startY, float dirX, float dirY, float damage, Color color, float maxRange) {
        super(startX, startY, 8, 8, 700.0f);
        this.dirX = dirX;
        this.dirY = dirY;
        this.damage = damage;
        this.color = color;
        this.maxRange = maxRange;
    }

    @Override
    public void update() {
        float dt = Gdx.graphics.getDeltaTime();
        float moveX = dirX * speed * dt;
        float moveY = dirY * speed * dt;

        x += moveX;
        y += moveY;

        // Auto hapus jika melewati batas jarak senjata
        if (maxRange > 0f) {
            distanceTraveled += (float) Math.hypot(moveX, moveY);
            if (distanceTraveled >= maxRange) {
                active = false;
                return;
            }
        }

        // Auto hapus jika keluar dari area layar
        if (x < -20 || x > WorldConfig.WORLD_WIDTH + 20 || y < -20 || y > WorldConfig.WORLD_HEIGHT + 20) {
            active = false;
        }
    }

    @Override
    public void render(ShapeRenderer shape) {
        shape.setColor(color);
        shape.circle(x + width / 2f, y + height / 2f, width / 2f);
    }

    public float getDamage() { return damage; }
}
