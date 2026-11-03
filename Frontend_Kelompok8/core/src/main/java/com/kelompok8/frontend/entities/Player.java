package com.kelompok8.frontend.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Player extends Entity {
    private float velY = 0;
    private final float GRAVITY = -1200.0f;
    private final float JUMP_VELOCITY = 550.0f;
    private final float GROUND_Y = 100.0f;
    private boolean isGrounded = false;

    // Status Arah Menghadap (Default Kanan)
    private boolean facingRight = true;

    private float shootCooldown = 0.15f;
    private float shootTimer = 0;

    public Player(float x, float y) {
        super(x, y, 32, 48, 300.0f);
    }

    @Override
    public void update() {
        float dt = Gdx.graphics.getDeltaTime();

        // 1. Horizontal Movement & Facing Logic (Arrow Left / Right)
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x -= speed * dt;
            facingRight = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            x += speed * dt;
            facingRight = true;
        }

        // 2. Jumping Logic (Tombol Z)
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z) && isGrounded) {
            velY = JUMP_VELOCITY;
            isGrounded = false;
        }

        // Gravitasi & Ground Collision
        velY += GRAVITY * dt;
        y += velY * dt;

        if (y <= GROUND_Y) {
            y = GROUND_Y;
            velY = 0;
            isGrounded = true;
        }

        // Pembatas Layar
        if (x < 0) x = 0;
        if (x > Gdx.graphics.getWidth() - width) x = Gdx.graphics.getWidth() - width;

        if (shootTimer > 0) shootTimer -= dt;
    }

    // 3. Logika Menembak 8-Axis dengan Keyboard (Tombol X + Arrow Keys)
    public Bullet shoot() {
        if (shootTimer <= 0 && Gdx.input.isKeyPressed(Input.Keys.X)) {
            shootTimer = shootCooldown;

            float dirX = 0;
            float dirY = 0;

            boolean up = Gdx.input.isKeyPressed(Input.Keys.UP);
            boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN);
            boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
            boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

            // Deteksi Arah Aiming dari Panah
            if (up) dirY += 1;
            if (down && !isGrounded) dirY -= 1; // Menembak ke bawah hanya saat di udara
            if (left) dirX -= 1;
            if (right) dirX += 1;

            // Jika tidak menekan panah sama sekali, nembak sesuai arah badan menghadap (Kiri/Kanan)
            if (dirX == 0 && dirY == 0) {
                dirX = facingRight ? 1 : -1;
            }

            // Normalisasi vektor agar kecepatan tembakan diagonal tetap konstan
            Vector2 dir = new Vector2(dirX, dirY).nor();

            float bulletStartX = x + width / 2f - 4;
            float bulletStartY = y + height / 2f - 4;

            return new Bullet(bulletStartX, bulletStartY, dir.x, dir.y, 10);
        }
        return null;
    }

    @Override
    public void render(ShapeRenderer shape) {
        shape.setColor(Color.CYAN);
        shape.rect(x, y, width, height);

        // Indikator visual sederhana (Mata/Arah Menghadap)
        shape.setColor(Color.WHITE);
        float eyeX = facingRight ? (x + width - 8) : x + 2;
        shape.rect(eyeX, y + height - 12, 6, 6);
    }
}
