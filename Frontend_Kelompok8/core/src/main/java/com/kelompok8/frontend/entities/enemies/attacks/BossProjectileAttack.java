package com.kelompok8.frontend.entities.enemies.attacks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kelompok8.frontend.entities.enemies.Enemy;
import com.kelompok8.frontend.entities.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Mukhtar: Implementasi Projectile Attack untuk Boss Phase 1 dan Phase 2
public class BossProjectileAttack implements BossAttack {

    // Mukhtar: Representasi satu proyektil Boss
    private static class Projectile {

        float x;
        float y;

        float dirX;
        float dirY;

        float speed;
        float damage;

        boolean active = true;

        Projectile(float x, float y, float dirX, float dirY,
                   float speed, float damage) {

            this.x = x;
            this.y = y;

            this.dirX = dirX;
            this.dirY = dirY;

            this.speed = speed;
            this.damage = damage;
        }
    }

    private final List<Projectile> projectiles = new ArrayList<>();

    private final int projectileCount;
    private final float projectileSpeed;
    private final float damage;
    private final float spreadAngle;
    private final boolean aimed;

    private boolean finished = true;

    private Enemy owner;
    private Player target;

    // Mukhtar: Konstruktor konfigurasi pola proyektil
    public BossProjectileAttack(
        int projectileCount,
        float projectileSpeed,
        float damage,
        float spreadAngle,
        boolean aimed
    ) {

        this.projectileCount = projectileCount;
        this.projectileSpeed = projectileSpeed;
        this.damage = damage;
        this.spreadAngle = spreadAngle;
        this.aimed = aimed;
    }

    @Override
    public void start(Enemy owner, Player target) {

        this.owner = owner;
        this.target = target;

        projectiles.clear();

        finished = false;

        float originX = owner.getX() + owner.getBounds().width / 2f;
        float originY = owner.getY() + owner.getBounds().height / 2f;

        float baseAngle;

        // Mukhtar: Menentukan arah dasar serangan
        if (aimed) {

            float targetX = target.getX() + target.getBounds().width / 2f;
            float targetY = target.getY() + target.getBounds().height / 2f;

            baseAngle = (float) Math.atan2(
                targetY - originY,
                targetX - originX
            );

        } else {

            baseAngle = (float) Math.PI;
        }

        // Mukhtar: Menghasilkan pola tembakan menyebar
        for (int i = 0; i < projectileCount; i++) {

            float offset;

            if (projectileCount == 1) {
                offset = 0f;
            } else {
                offset = -spreadAngle / 2f
                    + spreadAngle * i / (projectileCount - 1f);
            }

            float angle = baseAngle + offset;

            Vector2 direction = new Vector2(
                (float) Math.cos(angle),
                (float) Math.sin(angle)
            ).nor();

            projectiles.add(
                new Projectile(
                    originX,
                    originY,
                    direction.x,
                    direction.y,
                    projectileSpeed,
                    damage
                )
            );
        }
    }

    @Override
    public void update(float dt) {

        if (finished) return;

        Iterator<Projectile> iterator = projectiles.iterator();

        while (iterator.hasNext()) {

            Projectile projectile = iterator.next();

            // Mukhtar: Memperbarui posisi proyektil
            projectile.x += projectile.dirX * projectile.speed * dt;
            projectile.y += projectile.dirY * projectile.speed * dt;

            Rectangle bulletBounds = new Rectangle(
                projectile.x - 6f,
                projectile.y - 6f,
                12f,
                12f
            );

            // Mukhtar: Collision Detection antara proyektil dan Player
            if (target.isActive()
                && bulletBounds.overlaps(target.getBounds())) {

                target.takeDamage(projectile.damage);
                iterator.remove();
                continue;
            }

            // Mukhtar: Menghapus proyektil yang keluar dari layar
            if (projectile.x < -30f
                || projectile.x > Gdx.graphics.getWidth() + 30f
                || projectile.y < -30f
                || projectile.y > Gdx.graphics.getHeight() + 30f) {

                iterator.remove();
            }
        }

        if (projectiles.isEmpty()) {
            finished = true;
        }
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void render(ShapeRenderer shape) {

        // Mukhtar: Visualisasi proyektil Boss
        shape.setColor(Color.ORANGE);

        for (Projectile projectile : projectiles) {

            shape.circle(
                projectile.x,
                projectile.y,
                6f
            );
        }
    }
}
