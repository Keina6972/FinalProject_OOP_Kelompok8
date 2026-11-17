package com.kelompok8.frontend.entities.enemies.projectiles;

// rizki : Lurus ke satu arah, ga ngejar player
public class LinearMovement implements ProjectileMovement {
    @Override
    public void move(FireOrb orb, float dt) {
        orb.moveBy(orb.getVx() * dt, orb.getVy() * dt);
    }
}
