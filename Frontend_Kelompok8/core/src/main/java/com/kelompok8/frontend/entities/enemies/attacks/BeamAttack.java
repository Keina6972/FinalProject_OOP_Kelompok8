package com.kelompok8.frontend.entities.enemies.attacks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.entities.enemies.Enemy;
import com.kelompok8.frontend.utils.GeometryUtils;
import com.kelompok8.frontend.utils.WorldConfig;


//ntar STARTUP = beam_start, ACTIVE = beam_loop, FADE = beam_end
public class BeamAttack implements BossAttack {
    private enum Phase { AIM, FIRE, FADE, RECOVERY, DONE }

    // angka random, adjust aja nanti
    private static final float MUZZLE_GAP = 22f;      // jarak ujung tangan dari sisi depan boss
    private static final float MUZZLE_HEIGHT = 0.62f; // tinggi ujung tangan (0..1 dari tinggi boss)
    private static final float BEAM_THICKNESS = 26f;  // tebel beam nya
    private static final float TRACK_CONE = 80f;      // beam cuma boleh muter di kerucut +-80 derajat dari arah hadap boss (di belakang boss = aman)
    private static final float DASH_LENGTH = 14f;     // garis putus-putus
    private static final float DASH_GAP = 10f;        // jarak antar garis
    private static final Color DASH_COLOR = new Color(0.55f, 0.45f, 0.1f, 1f); // warna garis putus-putus
    private static final Color BEAM_COLOR = Color.ORANGE; // warna beam

    private final float aimTime, startupTime, activeTime, fadeTime, recoveryTime;
    private final float damage;

    // tracking (default mati = sudut terkunci terus), diatur lewat withTracking()
    private float turnRate = 0f;  // kecepatan putar maksimal beam (derajat per detik)
    private float trackTime = 0f; // beam ngikutin player selama sekian detik sejak FIRE mulai

    private Phase phase = Phase.DONE;
    private float timer;
    private float phaseTime;
    private boolean facingRight;
    private float angle; // arah beam (derajat), hitbox, dam gambar semuanya dihitung dari sini
    private float originX, originY;
    private Enemy owner;
    private Player target;

    // aimTime = aba-aba, startupTime = beam muncul tapi belum bahaya, activeTime = beam nyala penuh (bahaya)
    public BeamAttack(float aimTime, float startupTime, float activeTime, float fadeTime, float recoveryTime, float damage) {
        this.aimTime = aimTime;
        this.startupTime = startupTime;
        this.activeTime = activeTime;
        this.fadeTime = fadeTime;
        this.recoveryTime = recoveryTime;
        this.damage = damage;
    }

    // nyalakan tracking, beam muter ngikutin player (max turnRate derajat/detik) selama trackTime detik sejak beam ditembakkan
    public BeamAttack withTracking(float turnRate, float trackTime) {
        this.turnRate = turnRate;
        this.trackTime = trackTime;
        return this;
    }

    @Override
    public void start(Enemy owner, Player target) {
        this.owner = owner;
        this.target = target;

        Rectangle o = owner.getBounds();
        Rectangle t = target.getBounds();
        facingRight = (t.x + t.width / 2f) >= (o.x + o.width / 2f);
        updateOrigin();

        // kunci sudut awal ke posisi player skrg
        angle = angleToTarget();

        enter(Phase.AIM, aimTime);
    }

    @Override
    public void update(float dt) {
        if (phase == Phase.DONE) return;
        updateOrigin(); // ujung tangan ngikutin boss
        timer -= dt;
        phaseTime += dt;

        switch (phase) {
            case AIM:
                if (timer <= 0f) enter(Phase.FIRE, startupTime + activeTime);
                break;
            case FIRE:
                if (turnRate > 0f && phaseTime < trackTime) trackTarget(dt); // beam muter ngikutin player
                if (isHitboxActive()) checkHit();
                if (timer <= 0f) enter(Phase.FADE, fadeTime);
                break;
            case FADE:
                if (timer <= 0f) enter(Phase.RECOVERY, recoveryTime);
                break;
            case RECOVERY:
                if (timer <= 0f) phase = Phase.DONE;
                break;
            default:
                break;
        }
    }

    private void enter(Phase next, float duration) {
        phase = next;
        timer = duration;
        phaseTime = 0f;
    }

    private void updateOrigin() {
        Rectangle o = owner.getBounds();
        originX = facingRight ? o.x + o.width + MUZZLE_GAP : o.x - MUZZLE_GAP;
        originY = o.y + o.height * MUZZLE_HEIGHT;
    }

    // hitbox baru aktif kalau beam udah nyala penuh (lewat masa STARTUP)
    private boolean isHitboxActive() {
        return phase == Phase.FIRE && phaseTime >= startupTime;
    }

    // sudut dari ujung tangan ke tengah badan player
    private float angleToTarget() {
        Rectangle t = target.getBounds();
        return (float) Math.toDegrees(Math.atan2(t.y + t.height / 2f - originY, t.x + t.width / 2f - originX));
    }

    // putar beam pelan2 ke arah player, maksimal turnRate * dt derajat per frame, dan ga keluar dari kerucut di depan boss
    private void trackTarget(float dt) {
        float forward = facingRight ? 0f : 180f;
        float rel = wrapAngle(angleToTarget() - forward);
        rel = Math.max(-TRACK_CONE, Math.min(TRACK_CONE, rel)); // batasi ke kerucut
        float goal = forward + rel;

        float diff = wrapAngle(goal - angle); // selisih lewat jalur terpendek
        float step = turnRate * dt;
        angle = wrapAngle(angle + Math.max(-step, Math.min(step, diff)));
    }

    // ubah sudut ke rentang -180 - 180
    private static float wrapAngle(float a) {
        return a - 360f * (float) Math.floor((a + 180f) / 360f);
    }

    // jarak dari ujung tangan ke tepi arena searah beam
    private float fullLength() {
        float dx = (float) Math.cos(Math.toRadians(angle));
        float dy = (float) Math.sin(Math.toRadians(angle));
        float t = Float.MAX_VALUE;
        if (dy < -1e-4f) t = Math.min(t, (originY - WorldConfig.GROUND_Y) / -dy);    // ground
        if (dy > 1e-4f)  t = Math.min(t, (Gdx.graphics.getHeight() - originY) / dy); // atas
        if (dx < -1e-4f) t = Math.min(t, originX / -dx);                             // dinding kiri
        if (dx > 1e-4f)  t = Math.min(t, (Gdx.graphics.getWidth() - originX) / dx);  // dinding kanan
        return Math.max(t, 0f);
    }

    private void checkHit() {
        if (target == null || !target.isActive()) return;
        float len = fullLength();
        float endX = originX + (float) Math.cos(Math.toRadians(angle)) * len;
        float endY = originY + (float) Math.sin(Math.toRadians(angle)) * len;
        if (GeometryUtils.segmentHitsRect(originX, originY, endX, endY, BEAM_THICKNESS / 2f, target.getBounds())) {
            target.takeDamage(damage);
        }
    }

    @Override
    public void render(ShapeRenderer shape) {
        if (phase == Phase.DONE || phase == Phase.RECOVERY || phase == Phase.FADE) return;

        float dx = (float) Math.cos(Math.toRadians(angle));
        float dy = (float) Math.sin(Math.toRadians(angle));

        if (isHitboxActive()) drawBeam(shape, dx, dy);
        else drawDashedLine(shape, dx, dy);
    }

    // garis putus2 sebelum beam
    private void drawDashedLine(ShapeRenderer shape, float dx, float dy) {
        shape.setColor(DASH_COLOR);
        float len = fullLength();
        float pos = 0f;
        while (pos < len) {
            float end = Math.min(pos + DASH_LENGTH, len);
            shape.rectLine(originX + dx * pos, originY + dy * pos, originX + dx * end, originY + dy * end, 3f);
            pos += DASH_LENGTH + DASH_GAP;
        }
    }

    // beam sementara, nanti ganti beam_loop
    private void drawBeam(ShapeRenderer shape, float dx, float dy) {
        float len = fullLength();
        shape.setColor(BEAM_COLOR);
        shape.rectLine(originX, originY, originX + dx * len, originY + dy * len, BEAM_THICKNESS);
    }

    @Override
    public boolean isFinished() { return phase == Phase.DONE; }
}
