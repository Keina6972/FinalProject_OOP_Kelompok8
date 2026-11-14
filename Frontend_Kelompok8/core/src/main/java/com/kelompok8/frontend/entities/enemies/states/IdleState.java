package com.kelompok8.frontend.entities.enemies.states;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.entities.enemies.MiniBossA;

// rizki : Diam bentar, habis itu pindah ke state menyerang
public class IdleState implements EnemyState {
    private float timer;

    public IdleState(float duration) {
        this.timer = duration;
    }

    @Override
    public void enter(MiniBossA owner) {}

    @Override
    public void update(MiniBossA owner, float dt) {
        timer -= dt;
        if (timer <= 0f) owner.startNextAttack();
    }

    @Override
    public void render(MiniBossA owner, ShapeRenderer shape) {}
}
