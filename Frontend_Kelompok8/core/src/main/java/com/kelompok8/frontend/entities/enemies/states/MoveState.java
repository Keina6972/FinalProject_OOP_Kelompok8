package com.kelompok8.frontend.entities.enemies.states;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.entities.enemies.MiniBossA;
import com.kelompok8.frontend.entities.enemies.attacks.BossAttack;
import com.kelompok8.frontend.entities.enemies.movements.BossMovement;

// Menjalankan satu gerakan (jalan/dash) sampai selesai, lalu lanjut ke serangan yang udah dipilih
public class MoveState implements EnemyState {
    private final BossMovement movement;
    private final float pause;    // total jeda (gerakan dihitung di dalamnya)
    private float elapsed = 0f;

    public MoveState(BossMovement movement, float pause) {
        this.movement = movement;
        this.pause = pause;
    }

    @Override
    public void enter(MiniBossA owner) {
        movement.start(owner, owner.getTarget());
    }

    @Override
    public void update(MiniBossA owner, float dt) {
        elapsed += dt;
        movement.update(dt);
        if (movement.isFinished()) {
            float remaining = pause - elapsed;
            if (remaining > 0f) owner.changeState(new IdleState(remaining));
            else owner.startNextAttack();
        }
    }

    @Override
    public void render(MiniBossA owner, ShapeRenderer shape) {}
}
