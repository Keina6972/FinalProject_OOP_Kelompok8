package com.kelompok8.frontend.entities.enemies.attacks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.entities.enemies.Enemy;
import com.kelompok8.frontend.entities.enemies.projectiles.ProjectileManager;

// rizki:
public class FireOrbAttack implements BossAttack {
    private enum Phase { CAST, FIRE, RECOVERY, DONE }

    private static final float MUZZLE_GAP = 22f; // jarak titik tembak dari sisi depan boss
    private static final float MUZZLE_HEIGHT = 0.62f; // tinggi titik tembak (0-1 dari tinggi boss)

    private final ProjectileManager manager;
    private final ShootingPattern pattern;   // cara nembak (di-inject)
    private final float castTime, shotInterval, recoveryTime;
    private final int shots;

    private Phase phase = Phase.DONE;
    private float timer;
    private float shotTimer;
    private int shotsFired;
    private boolean facingRight;
    private Enemy owner;
    private Player target;

    public FireOrbAttack(ProjectileManager manager, ShootingPattern pattern, float castTime, int shots, float shotInterval, float recoveryTime) {
        this.manager = manager;
        this.pattern = pattern;
        this.castTime = castTime;
        this.shots = Math.max(1, shots);
        this.shotInterval = shotInterval;
        this.recoveryTime = recoveryTime;
    }

    @Override
    public void start(Enemy owner, Player target) {
        this.owner = owner;
        this.target = target;
        phase = Phase.CAST;
        timer = castTime;
        shotsFired = 0;
        updateFacing();
    }

    @Override
    public void update(float dt) {
        if (phase == Phase.DONE) return;
        updateFacing(); // titik tembak ada di sisi yang menghadap player

        switch (phase) {
            case CAST:
                timer -= dt;
                if (timer <= 0f) {
                    phase = Phase.FIRE;
                    shotTimer = 0f; // tembakan pertama langsung
                }
                break;
            case FIRE:
                shotTimer -= dt;
                while (shotTimer <= 0f && shotsFired < shots) {
                    pattern.execute(muzzleX(), muzzleY(), manager); // tiap tembakan ngunci posisi player saat ini
                    shotsFired++;
                    shotTimer += shotInterval;
                }
                if (shotsFired >= shots) {
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

        //ntar ganti fire_orb_form
        shape.setColor(Color.ORANGE);
        shape.circle(muzzleX(), muzzleY(), 10f);
    }

    @Override
    public boolean isFinished() { return phase == Phase.DONE; }
}
