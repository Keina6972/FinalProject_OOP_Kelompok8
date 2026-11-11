package com.kelompok8.frontend.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.entities.enemies.attacks.BossAttack;
import com.kelompok8.frontend.entities.enemies.attacks.SlashAttack;
import com.kelompok8.frontend.entities.enemies.attacks.BossProjectileAttack;

public class Boss extends Enemy {

    // Mukhtar: Konfigurasi HP dan waktu Boss
    private static final float BOSS_MAX_HP = 2400f;
    private static final float PHASE_TRANSITION_TIME = 1.5f;

    private static final float PHASE_1_ATTACK_DELAY = 1.2f;
    private static final float PHASE_2_ATTACK_DELAY = 0.65f;

    private final Player target;

    // Mukhtar: Variasi serangan Boss Phase 1 dan Phase 2
    private final BossAttack slashPhase1 =
        new SlashAttack(0.6f, 0.2f, 0.5f, 130f, 70f, 20f);

    private final BossAttack slashPhase2 =
        new SlashAttack(0.35f, 0.35f, 0.3f, 165f, 85f, 35f);

    private final BossAttack projectilePhase1 =
        new BossProjectileAttack(3, 220f, 12f, 0.18f, false);

    private final BossAttack projectilePhase2 =
        new BossProjectileAttack(7, 280f, 18f, 0.10f, true);

    private BossAttack current;

    private float idleTimer = PHASE_1_ATTACK_DELAY;

    // Mukhtar: Sistem Phase Boss
    private int currentPhase = 1;
    private boolean transitioning = false;
    private float transitionTimer = 0f;

    // Mukhtar: Sistem movement Boss
    private float movementTimer = 0f;
    private float dashTimer = 0f;
    private boolean dashing = false;

    private float dashStartX;
    private float dashStartY;

    private float dashTargetX;
    private float dashTargetY;

    private int movementDirection = -1;
    private int attackIndex = 0;

    private static final float PHASE_1_MOVE_SPEED = 100f;
    private static final float PHASE_2_MOVE_SPEED = 155f;
    private static final float DASH_SPEED = 520f;

    public Boss(String name, float x, float y, Player target) {
        super(name, x, y, 80, 120, BOSS_MAX_HP, Color.RED);
        this.target = target;
    }

    @Override
    public void update() {
        if (!active) return;

        float dt = Gdx.graphics.getDeltaTime();

        // Mukhtar: Mengatur transisi Phase Boss
        if (transitioning) {
            transitionTimer -= dt;

            if (transitionTimer <= 0f) {
                transitioning = false;
                idleTimer = PHASE_2_ATTACK_DELAY;

                movementTimer = 0f;
                attackIndex = 0;
            }

            return;
        }

        // Mukhtar: Movement Boss tetap berjalan selama pertarungan
        updateMovement(dt);

        super.update();
    }

    @Override
    protected void updateBehavior() {
        float dt = Gdx.graphics.getDeltaTime();

        //kalo ada serangan berjalan, update ampe selesai lalu kembali ke jeda
        if (current != null) {
            current.update(dt);

            if (current.isFinished()) {
                current = null;

                idleTimer = currentPhase == 1
                    ? PHASE_1_ATTACK_DELAY
                    : PHASE_2_ATTACK_DELAY;
            }

            return;
        }

        //kalo idle, hitung mundur jeda, lalu nyerang
        idleTimer -= dt;

        if (idleTimer <= 0f) {
            startNextAttack();
        }
    }

    // Mukhtar: Memilih pola serangan sesuai fase Boss
    private void startNextAttack() {
        if (currentPhase == 1) {

            if (attackIndex % 2 == 0) {
                current = slashPhase1;
            } else {
                current = projectilePhase1;
            }

        } else {

            if (attackIndex % 2 == 0) {
                current = projectilePhase2;
            } else {
                current = slashPhase2;
            }
        }

        attackIndex++;
        current.start(this, target);
    }

    // Mukhtar: Movement Pattern Phase 1 dan Phase 2
    private void updateMovement(float dt) {

        movementTimer += dt;

        if (currentPhase == 1) {

            // Mukhtar: Phase 1 bergerak patrol secara horizontal
            if (movementTimer >= 2.5f) {
                movementTimer = 0f;
                movementDirection *= -1;
            }

            x += movementDirection * PHASE_1_MOVE_SPEED * dt;

            y = 100f + (float) Math.sin(movementTimer * 2f) * 12f;

        } else {

            // Mukhtar: Phase 2 memiliki pergerakan lebih agresif
            updatePhase2Movement(dt);
        }

        // Mukhtar: Membatasi pergerakan Boss di dalam arena
        float minX = 20f;
        float maxX = Gdx.graphics.getWidth() - width - 20f;

        if (x < minX) {
            x = minX;
            movementDirection = 1;
        }

        if (x > maxX) {
            x = maxX;
            movementDirection = -1;
        }

        if (y < 100f) y = 100f;
        if (y > Gdx.graphics.getHeight() - height - 30f) {
            y = Gdx.graphics.getHeight() - height - 30f;
        }
    }

    // Mukhtar: Dash Movement khusus Boss Phase 2
    private void updatePhase2Movement(float dt) {

        if (!dashing) {
            dashTimer += dt;

            if (dashTimer >= 2.8f) {

                dashTimer = 0f;
                dashing = true;

                dashStartX = x;
                dashStartY = y;

                // Mukhtar: Boss melakukan dash menuju posisi Player
                float targetX = target.getX() + target.getBounds().width / 2f;

                dashTargetX = targetX - width / 2f;
                dashTargetY = 120f;
            }

            x += movementDirection * PHASE_2_MOVE_SPEED * dt;

        } else {

            // Mukhtar: Gerakan dash menggunakan interpolasi posisi
            float dx = dashTargetX - x;
            float dy = dashTargetY - y;

            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            float step = DASH_SPEED * dt;

            if (distance <= step || distance < 1f) {

                x = dashTargetX;
                y = dashTargetY;
                dashing = false;

            } else {

                x += (dx / distance) * step;
                y += (dy / distance) * step;
            }
        }
    }

    // Mukhtar: Dynamic Phase Transition berdasarkan persentase HP
    @Override
    public boolean takeDamage(float damage) {

        if (transitioning || !active) return false;

        boolean defeated = super.takeDamage(damage);

        // Mukhtar: Boss memasuki Phase 2 ketika HP tersisa 50%
        if (!defeated && currentPhase == 1 && getHpRatio() <= 0.5f) {

            startPhaseTransition();
        }

        return defeated;
    }

    // Mukhtar: Mengatur transisi Phase 1 menuju Phase 2
    private void startPhaseTransition() {

        currentPhase = 2;
        transitioning = true;

        transitionTimer = PHASE_TRANSITION_TIME;

        current = null;
        idleTimer = PHASE_2_ATTACK_DELAY;

        dashing = false;
        dashTimer = 0f;

        attackIndex = 0;

        System.out.println("[BOSS] PHASE 2 ACTIVATED!");
    }

    @Override
    public void render(ShapeRenderer shape) {

        super.render(shape);

        if (!active) return;

        // Mukhtar: Efek visual ketika Boss mengalami transisi fase
        if (transitioning) {

            shape.setColor(
                ((int) (transitionTimer * 12f) % 2 == 0)
                    ? Color.WHITE
                    : Color.RED
            );

            shape.rect(x - 5f, y - 5f, width + 10f, height + 10f);

            return;
        }

        // Mukhtar: Warna indikator sesuai Phase Boss
        if (currentPhase == 2) {
            shape.setColor(Color.ORANGE);
            shape.rect(x, y + height + 16f, width, 4f);
        }

        if (current != null) {
            current.render(shape);
        }
    }

    @Override
    protected void onDefeated() {
        current = null;
        transitioning = false;
        dashing = false;

        System.out.println("[BOSS] DEFEATED!");
    }

    // Mukhtar: Getter untuk kebutuhan integrasi gameplay
    public int getCurrentPhase() {
        return currentPhase;
    }

    public boolean isTransitioning() {
        return transitioning;
    }

    public boolean isDashing() {
        return dashing;
    }
}
