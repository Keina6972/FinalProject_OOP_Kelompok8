package com.kelompok8.frontend.entities.enemies.projectiles;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.utils.CollisionDetector;
import com.kelompok8.frontend.utils.WorldConfig;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

//rizki
// Object Pool buat projectile.
public class ProjectileManager {
    private final Deque<FireOrb> pool = new ArrayDeque<>();
    private final List<FireOrb> active = new ArrayList<>();

    private final Deque<MagicCircle> magicPool = new ArrayDeque<>();
    private final List<MagicCircle> magicActive = new ArrayList<>();

    // ambil orb dari pool
    public FireOrb spawnFireOrb(float x, float y, float vx, float vy, float damage, ProjectileMovement movement) {
        FireOrb orb = pool.isEmpty() ? new FireOrb() : pool.pop();
        orb.init(x, y, vx, vy, damage, movement);
        active.add(orb);
        return orb;
    }

    // ambil magic circle dari pool
    public MagicCircle spawnMagicCircle(float x, float y, float vx, float vy, float damage, ProjectileMovement movement) {
        MagicCircle circle = magicPool.isEmpty() ? new MagicCircle() : magicPool.pop();
        circle.init(x, y, vx, vy, damage, movement);
        magicActive.add(circle);
        return circle;
    }

    // update semua orb + cek tabrakan dengan player
    public void update(Player target) {
        float w = WorldConfig.WORLD_WIDTH;
        float h = WorldConfig.WORLD_HEIGHT;

        Iterator<FireOrb> it = active.iterator();
        while (it.hasNext()) {
            FireOrb orb = it.next();
            orb.update();

            if (orb.isFlying()) {
                if (target != null && CollisionDetector.checkCollision(orb, target)) {
                    target.takeDamage(orb.getDamage()); // kena player = damage + impact
                    orb.impact();
                } else if (orb.touchesGround()) {
                    orb.impact(); // kena lantai = impact aja
                } else if (orb.isOffScreen(w, h)) {
                    orb.deactivate(); // keluar layar -> hilang
                }
            }

            if (!orb.isActive()) {
                it.remove();
                pool.push(orb); // balik ke pool
            }
        }

        Iterator<MagicCircle> mit = magicActive.iterator();
        while (mit.hasNext()) {
            MagicCircle circle = mit.next();
            circle.update();

            if (circle.isFlying()) {
                if (target != null && CollisionDetector.checkCollision(circle, target)) {
                    target.takeDamage(circle.getDamage()); // kena player = damage + impact
                    circle.impact();
                } else if (circle.touchesGround()) {
                    circle.impact(); // kena lantai = impact aja
                } else if (circle.isOffScreen(w, h)) {
                    circle.deactivate(); // keluar layar -> hilang
                }
            }

            if (!circle.isActive()) {
                mit.remove();
                magicPool.push(circle); // balik ke pool
            }
        }
    }

    public void render(ShapeRenderer shape) {
        for (FireOrb orb : active) orb.render(shape);
        for (MagicCircle circle : magicActive) circle.render(shape);
    }

    // buang semua orb
    public void clear() {
        for (FireOrb orb : active) {
            orb.deactivate();
            pool.push(orb);
        }
        active.clear();

        for (MagicCircle circle : magicActive) {
            circle.deactivate();
            magicPool.push(circle);
        }
        magicActive.clear();
    }

    public int getActiveCount() { return active.size(); }
    public int getPoolSize() { return pool.size(); }
}
