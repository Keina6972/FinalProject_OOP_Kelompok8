package com.kelompok8.frontend.entities.enemies.movements;

import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.entities.enemies.Enemy;

// Arah dikunci saat dash dimulai, jadi player bisa baca. Jarak dihitung saat dash benar-benar mulai:
// dipotong kalau mentok dinding, dan untuk FORWARD dipotong biar berhenti minGap sebelum player (ga nabrak)
// jangan lupa ntar ganti sprite aslinya yah
public class DashMovement implements BossMovement {
    public enum Direction { FORWARD, BACKWARD }
    private enum Phase { WINDUP, DASH, RECOVERY, DONE }

    private final Direction direction;
    private final float distance, windupTime, dashTime, recoveryTime, minGap;

    private Enemy owner;
    private Player target;
    private Phase phase = Phase.DONE;
    private float timer;
    private float dir;          // +1 kanan, -1 kiri (dikunci di start)
    private float startX;       // posisi awal dash
    private float dashDistance; // jarak dash sebenarnya (sudah dipotong)
    private float elapsed;      // waktu berjalan di fase DASH

    public DashMovement(Direction direction, float distance, float windupTime, float dashTime, float recoveryTime, float minGap) {
        this.direction = direction;
        this.distance = distance;
        this.windupTime = windupTime;
        this.dashTime = Math.max(dashTime, 0.0001f);
        this.recoveryTime = recoveryTime;
        this.minGap = minGap;
    }

    @Override
    public void start(Enemy owner, Player target) {
        this.owner = owner;
        this.target = target;
        // kunci arah sekarang: FORWARD = ke player, BACKWARD = menjauh
        dir = MovementMath.dirToTarget(owner, target) * (direction == Direction.FORWARD ? 1f : -1f);
        phase = Phase.WINDUP;
        timer = windupTime;
    }

    @Override
    public void update(float dt) {
        switch (phase) {
            case WINDUP:
                timer -= dt;
                if (timer <= 0f) beginDash();
                break;
            case DASH:
                elapsed += dt;
                float u = Math.min(elapsed / dashTime, 1f);
                float eased = 1f - (1f - u) * (1f - u);   // cepat di awal, melambat di akhir
                owner.setX(startX + dir * dashDistance * eased);
                if (u >= 1f) { phase = Phase.RECOVERY; timer = recoveryTime; }
                break;
            case RECOVERY:
                timer -= dt;
                if (timer <= 0f) phase = Phase.DONE;
                break;
            default:
                break;
        }
    }

    private void beginDash() {
        float allowed = distance;
        // FORWARD: berhenti minGap sebelum player (ga nabrak). Cuma berlaku kalau player masih di arah dash
        // kalau player lompat ke sisi lain boss saat aba-aba, dash tetap jalan ke arah yang dikunci
        boolean playerAhead = MovementMath.dirToTarget(owner, target) == dir;
        if (direction == Direction.FORWARD && playerAhead) {
            allowed = Math.min(allowed, Math.max(0f, MovementMath.gap(owner, target) - minGap));
        }
        allowed = Math.min(allowed, Math.max(0f, MovementMath.room(owner, dir))); // jangan tembus dinding
        dashDistance = allowed;
        startX = owner.getX();
        elapsed = 0f;
        phase = Phase.DASH;
    }

    @Override
    public boolean isFinished() { return phase == Phase.DONE; }
}
