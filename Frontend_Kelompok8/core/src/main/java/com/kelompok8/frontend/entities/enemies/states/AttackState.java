package com.kelompok8.frontend.entities.enemies.states;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.entities.enemies.MiniBossA;
import com.kelompok8.frontend.entities.enemies.attacks.BossAttack;

// Rizki : Menjalankan satu serangan (Strategy) sampai selesai, lalu balik ke idle
public class AttackState implements EnemyState {
    private final BossAttack attack;

    public AttackState(BossAttack attack) {
        this.attack = attack;
    }

    @Override
    public void enter(MiniBossA owner) {
        attack.start(owner, owner.getTarget());
    }

    @Override
    public void update(MiniBossA owner, float dt) {
        attack.update(dt);
        if (attack.isFinished()) owner.startIdle();
    }

    @Override
    public void render(MiniBossA owner, ShapeRenderer shape) {
        attack.render(shape);
    }
}
