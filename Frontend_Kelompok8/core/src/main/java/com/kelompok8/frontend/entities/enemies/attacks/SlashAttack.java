package com.kelompok8.frontend.entities.enemies.attacks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.kelompok8.frontend.entities.enemies.Enemy;
import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.entities.enemies.attacks.BossAttack;

//i still dont know how big is the hitbox since there's no sprite yet
//i'll leave these comments to adjust it later
public class SlashAttack implements BossAttack {
    private enum Phase { WINDUP, ACTIVE, RECOVERY, DONE }

    private static final Color WARNING = new Color(0.45f, 0.4f, 0f, 1f);

    private final float windupTime, activeTime, recoveryTime;
    private final float reach, hitHeight, damage; // reach = jangkauan ke depan, hitHeight = tinggi area tebasan

    private Phase phase = Phase.DONE;
    private float timer; // Sisa waktu fase sekarang
    private boolean facingRight; // Arah tebasan (dikunci saat serangan dimulai)
    private boolean hasHit; // 1 serangan = 1 hit
    private Enemy owner;
    private Player target;
    private final Rectangle hitbox = new Rectangle(); // Area tebasan di depan boss

    public SlashAttack(float windupTime, float activeTime, float recoveryTime, float reach, float hitHeight, float damage) {
        this.windupTime = windupTime;
        this.activeTime = activeTime;
        this.recoveryTime = recoveryTime;
        this.reach = reach;
        this.hitHeight = hitHeight;
        this.damage = damage;
    }

    @Override
    public void start(Enemy owner, Player target) {
        this.owner = owner;
        this.target = target;

        // Kunci arah tebasan ke sisi tempat player berada saat ini
        Rectangle o = owner.getBounds();
        Rectangle t = target.getBounds();
        facingRight = (t.x + t.width / 2f) >= (o.x + o.width / 2f);

        phase = Phase.WINDUP;
        timer = windupTime;
        hasHit = false;
        updateHitbox();
    }

    @Override
    public void update(float dt) {
        if (phase == Phase.DONE) return;

        // Area tebasan mengikuti posisi boss
        updateHitbox();
        timer -= dt;

        switch (phase) {
            case WINDUP:
                if (timer <= 0f) { phase = Phase.ACTIVE; timer = activeTime; }
                break;
            case ACTIVE:
                if (!hasHit && hitbox.overlaps(target.getBounds())) {
                    hasHit = true;
                    target.takeDamage(damage);
                }
                if (timer <= 0f) { phase = Phase.RECOVERY; timer = recoveryTime; }
                break;
            case RECOVERY:
                if (timer <= 0f) phase = Phase.DONE;
                break;
            default:
                break;
        }
    }

    //posisi area tebasan, ngikutin sisi depan boss
    private void updateHitbox() {
        Rectangle o = owner.getBounds();
        float hx = facingRight ? o.x + o.width : o.x - reach;
        hitbox.set(hx, o.y, reach, hitHeight);
    }

    @Override
    public void render(ShapeRenderer shape) {
        if (phase == Phase.WINDUP) {
            shape.setColor(WARNING);
            shape.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        } else if (phase == Phase.ACTIVE) {
            shape.setColor(Color.ORANGE);
            shape.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        }
    }

    @Override
    public boolean isFinished() { return phase == Phase.DONE; }
}
