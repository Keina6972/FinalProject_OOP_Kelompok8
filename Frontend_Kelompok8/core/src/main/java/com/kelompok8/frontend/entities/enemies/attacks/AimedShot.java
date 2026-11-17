package com.kelompok8.frontend.entities.enemies.attacks;

import com.kelompok8.frontend.entities.Entity;
import com.kelompok8.frontend.entities.enemies.projectiles.ProjectileManager;
import com.kelompok8.frontend.entities.enemies.projectiles.ProjectileMovement;

// Rizki:
// Nembak satu orb lurus ke posisi target SAAT execute() dipanggil.
// Arahnya dikunci sekali di situ, setelah itu orb ga ngikutin target lagi
public class AimedShot implements ShootingPattern {
    private final Entity target;
    private final float speed;
    private final float damage;
    private final ProjectileMovement movement;

    public AimedShot(Entity target, float speed, float damage, ProjectileMovement movement) {
        this.target = target;
        this.speed = speed;
        this.damage = damage;
        this.movement = movement;
    }

    @Override
    public void execute(float originX, float originY, ProjectileManager manager) {
        float dx = target.getCenterX() - originX;
        float dy = target.getCenterY() - originY;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len < 0.001f) { dx = 1f; dy = 0f; len = 1f; }   //jaga2 target persis di titik tembak

        manager.spawnFireOrb(originX, originY, dx / len * speed, dy / len * speed, damage, movement);
    }
}
