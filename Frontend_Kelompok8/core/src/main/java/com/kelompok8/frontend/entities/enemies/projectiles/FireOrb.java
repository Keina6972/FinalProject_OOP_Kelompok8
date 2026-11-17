package com.kelompok8.frontend.entities.enemies.projectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.entities.Entity;
import com.kelompok8.frontend.utils.WorldConfig;

// rizki
// Fire orb milik musuh. Dipakai ulang lewat pool (ProjectileManager), jadi semua state di-reset di init().
public class FireOrb extends Entity {
    private static final float SIZE = 20f;        // hitbox persegi
    private static final float DRAW_RADIUS = 12f; // ukuran gambar orb

    private float cx, cy; // titik tengah orb
    private float vx, vy;
    private float damage;
    private ProjectileMovement movement;

    public FireOrb() {
        super(0, 0, SIZE, SIZE, 0);
        this.active = false; // belum dipakai (masih di pool)
    }

    // dipanggil tiap orb diambil dari pool
    public void init(float cx, float cy, float vx, float vy, float damage, ProjectileMovement movement) {
        this.cx = cx;
        this.cy = cy;
        this.vx = vx;
        this.vy = vy;
        this.speed = (float) Math.hypot(vx, vy);
        this.damage = damage;
        this.movement = movement;
        this.active = true;
        syncHitbox();
    }

    @Override
    public void update() {
        if (!active) return;
        float dt = Gdx.graphics.getDeltaTime();
        movement.move(this, dt); // gerakan didelegasikan ke Strategy
    }

    // dipakai ProjectileMovement
    public void moveBy(float dx, float dy) {
        cx += dx;
        cy += dy;
        syncHitbox();
    }

    public void impact() {
        //ntar tambahin fire_orb_impact, ga ada animasi sementara
        deactivate();
    }

    public void deactivate() {
        active = false;
    }

    public boolean touchesGround() {
        return cy - height / 2f <= WorldConfig.GROUND_Y;
    }

    public boolean isOffScreen(float screenW, float screenH) {
        float m = 40f;
        return cx < -m || cx > screenW + m || cy > screenH + m;
    }

    private void syncHitbox() {
        x = cx - width / 2f;
        y = cy - height / 2f;
    }

    @Override
    public void render(ShapeRenderer shape) {
        if (!active) return;

        // ntar ganti  fire_orb_fly
        shape.setColor(Color.ORANGE);
        shape.circle(cx, cy, DRAW_RADIUS);
    }

    public boolean isFlying() { return active; }
    public float getVx() { return vx; }
    public float getVy() { return vy; }
    public float getDamage() { return damage; }
    public float getCenterX() { return cx; }
    public float getCenterY() { return cy; }
}
