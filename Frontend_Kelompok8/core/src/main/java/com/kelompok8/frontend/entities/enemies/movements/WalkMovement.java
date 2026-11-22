package com.kelompok8.frontend.entities.enemies.movements;

import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.entities.enemies.Enemy;

// Jalan biasa ke arah player (towardPlayer = true) atau menjauh darinya (false) selama duration detik.
// Berhenti lebih awal kalau mentok dinding, atau saat mendekat sudah sedekat minGap dari player
public class WalkMovement implements BossMovement {
    private final boolean towardPlayer;
    private final float duration, speed, minGap;

    private Enemy owner;
    private Player target;
    private float timer;
    private boolean finished = true;

    public WalkMovement(boolean towardPlayer, float duration, float speed, float minGap) {
        this.towardPlayer = towardPlayer;
        this.duration = duration;
        this.speed = speed;
        this.minGap = minGap;
    }

    @Override
    public void start(Enemy owner, Player target) {
        this.owner = owner;
        this.target = target;
        timer = duration;
        finished = false;
    }

    @Override
    public void update(float dt) {
        if (finished) return;
        timer -= dt;

        // arah dihitung tiap frame, jadi jalan mendekat selalu ke posisi player yang sekarang
        float dir = MovementMath.dirToTarget(owner, target) * (towardPlayer ? 1f : -1f);
        float step = speed * dt;
        if (towardPlayer) step = Math.min(step, Math.max(0f, MovementMath.gap(owner, target) - minGap));
        step = Math.min(step, Math.max(0f, MovementMath.room(owner, dir))); // jangan tembus dinding

        owner.setX(owner.getX() + dir * step);

        // selesai kalo waktunya abis atau udah ga bisa gerak lagi (mentok)
        if (timer <= 0f || step <= 0f) finished = true;
    }

    @Override
    public boolean isFinished() { return finished; }
}
