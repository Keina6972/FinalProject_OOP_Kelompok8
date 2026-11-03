package com.kelompok8.frontend.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Player extends Entity {
    private float velY = 0;
    private final float GRAVITY = -1200.0f; // Kekuatan gravitasi
    private final float JUMP_VELOCITY = 550.0f; // Kecepatan lompat
    private final float GROUND_Y = 100.0f; // Garis lantai arena sementara
    private boolean isGrounded = false;

    private float shootCooldown = 0.15f;
    private float shootTimer = 0;

    public Player(float x, float y) {
        super(x, y, 32, 48, 300.0f); // Ukuran karakter 32x48
    }

    @Override
    public void update() {
        float dt = Gdx.graphics.getDeltaTime();

        // 1. Horizontal Movement (Kiri & Kanan saja)
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x -= speed * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            x += speed * dt;
        }

        // 2. Jumping Logic & Gravitasi
        if ((Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) && isGrounded) {
            velY = JUMP_VELOCITY;
            isGrounded = false;
        }

        // Aplikasikan gravitasi
        velY += GRAVITY * dt;
        y += velY * dt;

        // Ground Collision sederhana
        if (y <= GROUND_Y) {
            y = GROUND_Y;
            velY = 0;
            isGrounded = true;
        }

        // Batasi pergerakan agar tidak keluar layar kiri/kanan
        if (x < 0) x = 0;
        if (x > Gdx.graphics.getWidth() - width) x = Gdx.graphics.getWidth() - width;

        if (shootTimer > 0) shootTimer -= dt;
    }

    // 3. Logika 8-Axis Aiming berdasarkan tombol yang ditekan
    public Bullet shoot() {
        if (shootTimer <= 0 && Gdx.input.isKeyPressed(Input.Keys.J)) { // Tombol 'J' atau Klik Kiri untuk nembak
            shootTimer = shootCooldown;

            float dirX = 0;
            float dirY = 0;

            boolean up = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
            boolean down = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
            boolean left = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
            boolean right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

            // Tentukan arah berdasarkan input 8-Axis
            if (up) dirY += 1;
            if (down && !isGrounded) dirY -= 1; // Menembak ke bawah hanya saat di udara
            if (left) dirX -= 1;
            if (right) dirX += 1;

            // Jika tidak menekan tombol arah sama sekali, default nembak ke depan (misal kanan)
            if (dirX == 0 && dirY == 0) {
                dirX = 1;
            }

            Vector2 dir = new Vector2(dirX, dirY).nor(); // Normalize arah vektor
            return new Bullet(x + width / 2f - 4, y + height / 2f - 4, dir.x, dir.y, 10);
        }
        return null;
    }

    @Override
    public void render(ShapeRenderer shape) {
        shape.setColor(Color.CYAN);
        shape.rect(x, y, width, height);
    }
}
