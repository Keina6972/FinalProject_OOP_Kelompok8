package com.kelompok8.frontend.entities.enemies.movements;

import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.entities.enemies.Enemy;

// Strategy: cara boss bergerak (jalan, dash, dll), sama konsepnya dengan BossAttack tapi buat gerakan.
// Dibuat interface biar gerakan baru gampang ditambah tanpa ubah state/bossnya.
public interface BossMovement {
    void start(Enemy owner, Player target);
    void update(float dt);
    boolean isFinished();
    MovementType getType();
}
