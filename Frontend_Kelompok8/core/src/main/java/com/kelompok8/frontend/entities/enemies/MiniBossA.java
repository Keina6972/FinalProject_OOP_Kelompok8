package com.kelompok8.frontend.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.entities.enemies.attacks.AimedShot;
import com.kelompok8.frontend.entities.enemies.attacks.BossAttack;
import com.kelompok8.frontend.entities.enemies.attacks.FireOrbAttack;
import com.kelompok8.frontend.entities.enemies.attacks.PillarAttack;
import com.kelompok8.frontend.entities.enemies.projectiles.LinearMovement;
import com.kelompok8.frontend.entities.enemies.projectiles.ProjectileManager;
import com.kelompok8.frontend.entities.enemies.states.AttackState;
import com.kelompok8.frontend.entities.enemies.states.EnemyState;
import com.kelompok8.frontend.entities.enemies.states.IdleState;

import java.util.ArrayList;
import java.util.List;

// Rizki : Miniboss A (boss api phase 2). Behavior diatur State Pattern (langsung aplikasi dari tp wkwkwk (before pre-cs btw)),
// serangannya Strategy (BossAttack) yang bisa ditambah lewat addAttack().
public class MiniBossA extends Enemy {
    private static final float IDLE_TIME = 1.2f;

    private final Player target;
    private final List<BossAttack> attacks = new ArrayList<>();
    private int attackIndex = 0;
    private EnemyState state;   // state yang sedang aktif

    public MiniBossA(String name, float x, float y, Player target, ProjectileManager projectiles) {
        super(name, x, y, 60, 130, 600f, Color.ORANGE);   //sementara, nanti sesuaikan sprite
        this.target = target;

        addAttack(new FireOrbAttack(projectiles, new AimedShot(target, 360f, 15f, new LinearMovement()), 0.6f, 5, 0.25f, 0.7f));
        addAttack(new PillarAttack(2, 0.8f, 0.35f, 25f));   //2 gelombang, warning 0.8s, tahan 0.35s, damage 25

        startIdle();
    }

    // rizki : State pattern: context
    public final void changeState(EnemyState next) {
        state = next;
        state.enter(this);
    }

    public final void startIdle() {
        changeState(new IdleState(IDLE_TIME));
    }

    public void startNextAttack() {
        changeState(new AttackState(nextAttack()));
    }

    // rizki : Strategy: daftar serangan
    public final void addAttack(BossAttack attack) {
        attacks.add(attack);
    }

    // bergiliran
    private BossAttack nextAttack() {
        BossAttack a = attacks.get(attackIndex % attacks.size());
        attackIndex++;
        return a;
    }

    @Override
    protected void updateBehavior() {
        state.update(this, Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(ShapeRenderer shape) {
        super.render(shape);   // badan + HP bar
        if (active) state.render(this, shape);
    }

    public Player getTarget() { return target; }
}
