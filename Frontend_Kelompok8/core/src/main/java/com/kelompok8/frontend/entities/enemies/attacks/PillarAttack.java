package com.kelompok8.frontend.entities.enemies.attacks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.entities.enemies.Enemy;

import java.util.ArrayList;
import java.util.List;

// rikzi : Serangan pillar kek Pure Vessel dari hollow knight
// harusnya, selain angka random dan sprite, ga ada lagi yg perlu diubah
public class PillarAttack implements BossAttack {
    private enum Phase { WAITING, WARNING, RISING, HOLDING, RETRACTING, DONE }

    // angka random, adjust aja nanti
    private static final float FLOOR_Y = 100f;        // tinggi lantai (sama dengan GROUND_Y di Player)
    private static final float PILLAR_WIDTH = 32f;
    private static final float PILLAR_HEIGHT = 320f;
    private static final float SPACING = 104f;        // jarak antar pusat pilar
    private static final float EDGE_MARGIN = 48f;      // pilar min. segini dari dinding, biar celah di tepi arena tidak sempit
    private static final float RISE_TIME = 0.12f;     // muncul cepat
    private static final float RETRACT_TIME = 0.25f;
    private static final float WAVE_INTERVAL = 1.3f;  // jeda antar awal gelombang
    private static final float RECOVERY_TIME = 0.8f;  // boss diam setelah pilar selesai

    // satu pilar
    private static class Pillar {
        final float centerX;
        Phase phase = Phase.WAITING;
        float timer;      // sisa waktu fase sekarang
        float height = 0f;
        float progress = 0f; // 0..1 progres fase sekarang (buat animasi)

        Pillar(float centerX, float delay) {
            this.centerX = centerX;
            this.timer = delay;
        }
    }

    private final int waves;
    private final float warnTime, holdTime, damage;

    private final List<Pillar> pillars = new ArrayList<>();
    private boolean finished = true;
    private float recoveryTimer;
    private Player target;

    public PillarAttack(int waves, float warnTime, float holdTime, float damage) {
        this.waves = Math.max(1, waves);
        this.warnTime = warnTime;
        this.holdTime = holdTime;
        this.damage = damage;
    }

    @Override
    public void start(Enemy owner, Player target) {
        this.target = target;
        pillars.clear();
        finished = false;
        recoveryTimer = RECOVERY_TIME;

        // posisi awal diacak biar tiap serangan beda
        float baseOffset = MathUtils.random(0f, SPACING);
        for (int w = 0; w < waves; w++) {
            float shift = (w % 2 == 1) ? SPACING / 2f : 0f;   // gelombang ganjil digeser setengah jarak
            float delay = w * WAVE_INTERVAL;
            for (float x = baseOffset + shift - SPACING; x < Gdx.graphics.getWidth() + SPACING; x += SPACING) {
                // pilar harus cukup jauh dari kedua dinding (kalo ngga, celah di tepi terlalu sempit)
                if (x - PILLAR_WIDTH / 2f >= EDGE_MARGIN && x + PILLAR_WIDTH / 2f <= Gdx.graphics.getWidth() - EDGE_MARGIN) {
                    pillars.add(new Pillar(x, delay));
                }
            }
        }
    }

    @Override
    public void update(float dt) {
        if (finished) return;

        boolean allDone = true;
        for (Pillar p : pillars) {
            updatePillar(p, dt);
            if (p.phase != Phase.DONE) allDone = false;
        }

        // selesai setelah semua pilar hilang + masa recovery
        if (allDone) {
            recoveryTimer -= dt;
            if (recoveryTimer <= 0f) finished = true;
        }
    }

    private void updatePillar(Pillar p, float dt) {
        if (p.phase == Phase.DONE) return;
        p.timer -= dt;


        switch (p.phase) {
            case WAITING:
                if (p.timer <= 0f) enter(p, Phase.WARNING, warnTime);
                break;
            case WARNING:
                p.progress = 1f - p.timer / warnTime;
                if (p.timer <= 0f) enter(p, Phase.RISING, RISE_TIME);
                break;
            case RISING:
                p.progress = 1f - Math.max(p.timer, 0f) / RISE_TIME;
                p.height = PILLAR_HEIGHT * (1f - (1f - p.progress) * (1f - p.progress)); // melambat di ujung
                checkHit(p);
                if (p.timer <= 0f) enter(p, Phase.HOLDING, holdTime);
                break;
            case HOLDING:
                p.height = PILLAR_HEIGHT;
                checkHit(p);
                if (p.timer <= 0f) enter(p, Phase.RETRACTING, RETRACT_TIME);
                break;
            case RETRACTING:
                p.progress = 1f - Math.max(p.timer, 0f) / RETRACT_TIME;
                p.height = PILLAR_HEIGHT * (1f - p.progress);
                if (p.timer <= 0f) { p.phase = Phase.DONE; p.height = 0f; }
                break;
            default:
                break;
        }
    }

    private void enter(Pillar p, Phase next, float duration) {
        p.phase = next;
        p.timer = duration;
        p.progress = 0f;
    }

    // cek tabrakan pilar dengan player (invulnerable timer di Player mencegah kena berkali-kali)
    private void checkHit(Pillar p) {
        if (target == null || !target.isActive()) return;
        if (hitbox(p).overlaps(target.getBounds())) {
            target.takeDamage(damage);
        }
    }

    private Rectangle hitbox(Pillar p) {
        return new Rectangle(p.centerX - PILLAR_WIDTH / 2f, FLOOR_Y, PILLAR_WIDTH, p.height);
    }

    @Override
    public void render(ShapeRenderer shape) {
        for (Pillar p : pillars) {
            switch (p.phase) {
                case WARNING:    drawWarning(shape, p);    break;
                case RISING:
                case HOLDING:    drawPillar(shape, p);     break;
                case RETRACTING: drawRetracting(shape, p); break;
                default: break;
            }
        }
    }

    // NTAR TOLONG GANTI JADI SPRITE

    // penanda di lantai (nanti: animasi pillar_warning)
    private void drawWarning(ShapeRenderer shape, Pillar p) {
        float t = p.progress;
        shape.setColor(0.35f + 0.65f * t, 0.30f + 0.55f * t, 0.05f, 1f);
        shape.rect(p.centerX - PILLAR_WIDTH / 2f, FLOOR_Y, PILLAR_WIDTH, 6f);
    }

    // pilar aktif (nanti: animasi pillar_erupt)
    private void drawPillar(ShapeRenderer shape, Pillar p) {
        shape.setColor(Color.WHITE);
        shape.rect(p.centerX - PILLAR_WIDTH / 2f, FLOOR_Y, PILLAR_WIDTH, p.height);
    }

    // pilar menyusut (nanti: animasi pillar_fade)
    private void drawRetracting(ShapeRenderer shape, Pillar p) {
        shape.setColor(Color.GRAY);
        shape.rect(p.centerX - PILLAR_WIDTH / 2f, FLOOR_Y, PILLAR_WIDTH, p.height);
    }

    @Override
    public boolean isFinished() { return finished; }
}
