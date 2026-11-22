package com.kelompok8.frontend.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.entities.enemies.ai.ActionPlan;
import com.kelompok8.frontend.entities.enemies.ai.ActionPlanner;
import com.kelompok8.frontend.entities.enemies.ai.AttackSelector;
import com.kelompok8.frontend.entities.enemies.ai.AttackType;
import com.kelompok8.frontend.entities.enemies.attacks.*;
import com.kelompok8.frontend.entities.enemies.movements.MovementMath;
import com.kelompok8.frontend.entities.enemies.movements.MovementType;
import com.kelompok8.frontend.entities.enemies.projectiles.LinearMovement;
import com.kelompok8.frontend.entities.enemies.projectiles.ProjectileManager;
import com.kelompok8.frontend.entities.enemies.states.AttackState;
import com.kelompok8.frontend.entities.enemies.states.EnemyState;
import com.kelompok8.frontend.entities.enemies.states.IdleState;
import com.kelompok8.frontend.entities.enemies.states.MoveState;


// Rizki : Miniboss A (boss api phase 2). Behavior diatur State Pattern (langsung aplikasi dari tp wkwkwk (before pre-cs btw)),
// serangannya Strategy (BossAttack) yang bisa ditambah lewat addAttack().
public class MiniBossA extends Enemy {
    private static final float FIRST_PAUSE = 1.2f;   // jeda sebelum serangan pertama
    private static final float COOLDOWN = 10f;       // cooldown semua serangan (detik), adjust aja

    private final Player target;
    private final AttackSelector selector = new AttackSelector(); // pilih serangan: peluang, jarak, kombo, cooldown
    private final ActionPlanner planner = new ActionPlanner(); // rencana gerakan & jeda setelah serangan
    private EnemyState state;   // state yang sedang aktif
    private boolean facingRight; // menghadap player (buat flip sprite nanti)

    private AttackType currentAttack;    // serangan yang lagi / barusan jalan
    private AttackType lastAttack;       // serangan sebelumnya (buat kombo)
    private MovementType lastMovement;   // gerakan di jeda sebelum serangan ini (null = diam)

    public MiniBossA(String name, float x, float y, Player target, ProjectileManager projectiles) {
        super(name, x, y, 60, 130, 600f, Color.ORANGE); // sementara
        this.target = target;

        addAttack(AttackType.BEAM, new BeamAttack(1.0f, 0.1f, 1.0f, 0.1f, 0.8f, 25f).withTracking(60f, 0.6f));
        addAttack(AttackType.FIRE_ORB, new FireOrbAttack(projectiles, new LinearMovement(), 15, 30f, 0.8f, 0.03f, 0.5f, 450f, 12f));
        addAttack(AttackType.MAGIC_CIRCLE, new MagicCircleAttack(projectiles, new MagicCircleShot(target, 360f, 15f, new LinearMovement()), 0.6f, 5, 0.5f, 0.7f));
        addAttack(AttackType.PILLAR, new PillarAttack(2, 0.8f, 0.35f, 25f));

        changeState(new IdleState(FIRST_PAUSE));
    }

    // rizki : State pattern: context
    public final void changeState(EnemyState next) {
        state = next;
        state.enter(this);
    }

    // dipanggil AttackState waktu serangan selesai, mulai cd nya, lalu rencanain jeda (gerak dulu kalau perlu)
    public final void startIdle() {
        selector.startCooldown(currentAttack);
        lastAttack = currentAttack;

        ActionPlan plan = planner.afterAttack(this, target, lastAttack);
        lastMovement = plan.movement == null ? null : plan.movement.getType();

        if (plan.movement == null) changeState(new IdleState(plan.pause));
        else changeState(new MoveState(plan.movement, plan.pause));
    }

    // dipanggil setelah jeda (dan gerakan) selesai, pilih serangan pakai jarak SEKARANG, gerakan & serangan sebelumnya, dan cooldown
    public void startNextAttack() {
        float gap = MovementMath.gap(this, target);
        currentAttack = selector.choose(gap, lastAttack, lastMovement);
        changeState(new AttackState(selector.get(currentAttack)));
    }

    // rizki : Strategy: daftar serangan
    public final void addAttack(AttackType type, BossAttack attack) {
        selector.register(type, attack, COOLDOWN);
    }


    @Override
    protected void updateBehavior() {
        selector.update(Gdx.graphics.getDeltaTime());   // cooldown jalan terus
        facingRight = target.getCenterX() >= getCenterX();
        state.update(this, Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(ShapeRenderer shape) {
        super.render(shape);   // badan + HP bar
        if (active) state.render(this, shape);
    }

    public Player getTarget() { return target; }
    public boolean isFacingRight() { return facingRight; }
}
