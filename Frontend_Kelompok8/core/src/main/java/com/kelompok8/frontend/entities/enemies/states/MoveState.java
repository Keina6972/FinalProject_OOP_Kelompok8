package com.kelompok8.frontend.entities.enemies.states;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.entities.enemies.MiniBossA;
import com.kelompok8.frontend.entities.enemies.attacks.BossAttack;
import com.kelompok8.frontend.entities.enemies.movements.BossMovement;

// Menjalankan satu gerakan (jalan/dash) sampai selesai, lalu lanjut ke serangan yang udah dipilih
public class MoveState implements EnemyState {
    private final BossMovement movement;
    private final BossAttack nextAttack;

    public MoveState(BossMovement movement, BossAttack nextAttack) {
        this.movement = movement;
        this.nextAttack = nextAttack;
    }

    @Override
    public void enter(MiniBossA owner) {
        movement.start(owner, owner.getTarget());
    }

    @Override
    public void update(MiniBossA owner, float dt) {
        movement.update(dt);
        if (movement.isFinished()) owner.changeState(new AttackState(nextAttack));
    }

    @Override
    public void render(MiniBossA owner, ShapeRenderer shape) {}
}
