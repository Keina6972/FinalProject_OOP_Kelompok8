package com.kelompok8.frontend.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Player extends Entity {
    private float shootCooldown = 0.15f;
    private float shootTimer = 0;

    public Player(float x, float y) {
        super(x, y, 32, 32, 250.0f); 
    }

    @Override
    public void update() {
        float dt = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) y += speed * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) y -= speed * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) x -= speed * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) x += speed * dt;

        // Batasi pergerakan agar tidak keluar layar
        if (x < 0) x = 0;
        if (x > Gdx.graphics.getWidth() - width) x = Gdx.graphics.getWidth() - width;
        if (y < 0) y = 0;
        if (y > Gdx.graphics.getHeight() - height) y = Gdx.graphics.getHeight() - height;

        if (shootTimer > 0) shootTimer -= dt;
    }

    public Bullet shoot(float targetX, float targetY) {
        if (shootTimer <= 0) {
            shootTimer = shootCooldown;
            return new Bullet(x + width / 2f - 4, y + height / 2f - 4, targetX, targetY, 10);
        }
        return null;
    }

    @Override
    public void render(ShapeRenderer shape) {
        shape.setColor(Color.GREEN);
        shape.rect(x, y, width, height);
    }
}
