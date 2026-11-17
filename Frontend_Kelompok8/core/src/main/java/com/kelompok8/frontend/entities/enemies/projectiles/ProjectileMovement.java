package com.kelompok8.frontend.entities.enemies.projectiles;

// Rizki
// Strategy: cara projectile bergerak tiap frame (linear, homing, dll).
// Dibuat interface biar nanti gampang nambah gerakan baru tanpa ubah FireOrb
public interface ProjectileMovement {
    void move(FireOrb orb, float dt);
}
