package com.kelompok8.frontend.entities.enemies.attacks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.entities.enemies.Enemy;
import com.kelompok8.frontend.entities.enemies.projectiles.ProjectileManager;
import com.kelompok8.frontend.entities.enemies.projectiles.ProjectileMovement;

// ntar ganti, CAST = fire_orb_form, orb terbang = fire_orb_fly, kena = fire_orb_impact (nanti diganti sprite)

public class FireOrbAttack implements BossAttack {
    private enum Phase { CAST, FIRE, RECOVERY, DONE }

    private static final float MUZZLE_GAP = 22f;      // jarak titik tembak dari sisi depan boss
    private static final float MUZZLE_HEIGHT = 0.62f; // tinggi titik tembak (0..1 dari tinggi boss)

    private final ProjectileManager manager;
    private final ProjectileMovement movement;   // gerakan orb (di-inject, sekarang LinearMovement)
    private final int shotCount;                 // jumlah tembakan per serangan
    private final float maxOffset;               // batas sudut random
    private final float castTime, shotInterval, recoveryTime;
    private final float speed, damage;

    private Phase phase = Phase.DONE;
    private float timer;
    private float shotTimer;
    private int shotIndex;
    private float lockedAngle;
    private boolean facingRight;
    private Enemy owner;
    private Player target;

    public FireOrbAttack(ProjectileManager manager, ProjectileMovement movement, int shotCount, float maxOffset,
                         float castTime, float shotInterval, float recoveryTime, float speed, float damage) {
        this.manager = manager;
        this.movement = movement;
        this.shotCount = Math.max(1, shotCount);
        this.maxOffset = Math.abs(maxOffset);
        this.castTime = castTime;
        this.shotInterval = shotInterval;
        this.recoveryTime = recoveryTime;
        this.speed = speed;
        this.damage = damage;
    }

    @Override
    public void start(Enemy owner, Player target) {
        this.owner = owner;
        this.target = target;
        phase = Phase.CAST;
        timer = castTime;
        shotIndex = 0;
        updateFacing();
    }

    @Override
    public void update(float dt) {
        if (phase == Phase.DONE) return;

        switch (phase) {
            case CAST:
                updateFacing(); // selama casting titik tembak ada di sisi yang menghadap player
                timer -= dt;
                if (timer <= 0f) {
                    phase = Phase.FIRE;
                    shotTimer = 0f; // tembakan pertama langsung
                }
                break;
            case FIRE:
                shotTimer -= dt;
                while (shotTimer <= 0f && shotIndex < shotCount) {
                    if (shotIndex == 0) {
                        updateFacing();                // sisi tembak terakhir dikunci di sini
                        lockedAngle = angleToTarget(); // arah dikunci ke posisi player SEKARANG
                    }
                    float randomOffset = MathUtils.random(-maxOffset, maxOffset); // arah tembakan random
                    fireShot(randomOffset);
                    shotIndex++;
                    shotTimer += shotInterval;
                }
                if (shotIndex >= shotCount) {
                    phase = Phase.RECOVERY;
                    timer = recoveryTime;
                }
                break;
            case RECOVERY:
                timer -= dt;
                if (timer <= 0f) phase = Phase.DONE;
                break;
            default:
                break;
        }
    }

    private void fireShot(float offset) {
        // offset positif selalu ke atas: kalau boss menghadap kiri, putarannya dibalik
        float angle = lockedAngle + (facingRight ? offset : -offset);
        float rad = (float) Math.toRadians(angle);
        manager.spawnFireOrb(muzzleX(), muzzleY(),
            (float) Math.cos(rad) * speed, (float) Math.sin(rad) * speed, damage, movement);
    }

    // sudut dari titik tembak ke tengah badan player
    private float angleToTarget() {
        return (float) Math.toDegrees(Math.atan2(target.getCenterY() - muzzleY(), target.getCenterX() - muzzleX()));
    }

    private void updateFacing() {
        Rectangle o = owner.getBounds();
        Rectangle t = target.getBounds();
        facingRight = (t.x + t.width / 2f) >= (o.x + o.width / 2f);
    }

    private float muzzleX() {
        Rectangle o = owner.getBounds();
        return facingRight ? o.x + o.width + MUZZLE_GAP : o.x - MUZZLE_GAP;
    }

    private float muzzleY() {
        Rectangle o = owner.getBounds();
        return o.y + o.height * MUZZLE_HEIGHT;
    }

    // placeholder sebelum sprite asli
    @Override
    public void render(ShapeRenderer shape) {
        if (phase != Phase.CAST && phase != Phase.FIRE) return;

        shape.setColor(Color.ORANGE);
        shape.circle(muzzleX(), muzzleY(), 10f);
    }

    @Override
    public boolean isFinished() { return phase == Phase.DONE; }
}
