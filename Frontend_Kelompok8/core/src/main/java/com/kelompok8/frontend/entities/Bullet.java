package com.kelompok8.frontend.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Bullet extends Entity {
    private float dirX, dirY;
    private float damage;

    public Bullet(float startX, float startY, float dirX, float dirY, float damage) {
        super(startX, startY, 8, 8, 700.0f);
        this.dirX = dirX;
        this.dirY = dirY;
        this.damage = damage;
    }

    @Override
    public void update() {
        float dt = Gdx.graphics.getDeltaTime();
        x += dirX * speed * dt;
        y += dirY * speed * dt;

        // Auto hapus jika keluar dari area layar
        if (x < -20 || x > Gdx.graphics.getWidth() + 20 || y < -20 || y > Gdx.graphics.getHeight() + 20) {
            active = false;
        }
    }

    @Override
    public void render(ShapeRenderer shape) {
        shape.setColor(Color.YELLOW);
        shape.circle(x + width / 2f, y + height / 2f, width / 2f);
    }

    public float getDamage() { return damage; }
}
