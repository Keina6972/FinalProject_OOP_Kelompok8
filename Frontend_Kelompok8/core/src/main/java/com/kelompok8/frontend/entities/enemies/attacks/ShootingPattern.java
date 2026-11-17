package com.kelompok8.frontend.entities.enemies.attacks;

import com.kelompok8.frontend.entities.enemies.projectiles.ProjectileManager;

// Strategy: cara menembakkan projectile dari satu titik
// Serangan (BossAttack) yang ngatur kapan nembak, pattern ini yang ngatur gimana nembaknya.
public interface ShootingPattern {
    void execute(float originX, float originY, ProjectileManager manager);
}
