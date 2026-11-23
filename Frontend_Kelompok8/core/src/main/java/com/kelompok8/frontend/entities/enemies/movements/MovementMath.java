package com.kelompok8.frontend.entities.enemies.movements;

import com.badlogic.gdx.Gdx;
import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.entities.enemies.Enemy;

// Hitungan kecil yang dipakai bareng sama gerakan-gerakan boss (jarak ke player, batas arena)
public final class MovementMath {
    private MovementMath() {}

    // arah ke player: +1 = player di kanan boss, -1 = di kiri
    public static float dirToTarget(Enemy owner, Player target) {
        return target.getCenterX() >= owner.getCenterX() ? 1f : -1f;
    }

    // jarak horizontal tepi-ke-tepi antara boss dan player (0 kalau udah menempel)
    public static float gap(Enemy owner, Player target) {
        float centerDist = Math.abs(target.getCenterX() - owner.getCenterX());
        return Math.max(0f, centerDist - (owner.getWidth() + target.getWidth()) / 2f);
    }

    // batas kiri & kanan posisi x boss (seluruh badan harus tetap di dalam layar)
    public static float minX() { return 0f; }
    public static float maxX(Enemy owner) { return Gdx.graphics.getWidth() - owner.getWidth(); }

    // sisa ruang untuk bergerak ke arah dir (+1 kanan, -1 kiri) sampai kena dinding
    public static float room(Enemy owner, float dir) {
        return dir > 0f ? maxX(owner) - owner.getX() : owner.getX() - minX();
    }
}
