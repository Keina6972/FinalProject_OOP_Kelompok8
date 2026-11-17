package com.kelompok8.frontend.entities.enemies.projectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.utils.CollisionDetector;

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

    // ambil orb dari pool (kalau kosong baru bikin baru)
    public FireOrb spawnFireOrb(float x, float y, float vx, float vy, float damage, ProjectileMovement movement) {
        FireOrb orb = pool.isEmpty() ? new FireOrb() : pool.pop();
        orb.init(x, y, vx, vy, damage, movement);
        active.add(orb);
        return orb;
    }

    // update semua orb + cek tabrakan dengan player
    public void update(Player target) {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

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
    }

    public void render(ShapeRenderer shape) {
        for (FireOrb orb : active) orb.render(shape);
    }

    // buang semua orb (misal ganti state)
    public void clear() {
        for (FireOrb orb : active) {
            orb.deactivate();
            pool.push(orb);
        }
        active.clear();
    }

    public int getActiveCount() { return active.size(); }
    public int getPoolSize() { return pool.size(); }
}
