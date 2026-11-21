package com.kelompok8.frontend.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.kelompok8.frontend.cards.PlayerStats;
import com.kelompok8.frontend.utils.WorldConfig;

import java.util.List;

public class Player extends Entity {
    public enum WeaponType {
        LINEAR,
        SPREAD
    }

    private float velY = 0;
    private static final float GRAVITY = -1200.0f;
    private static final float JUMP_VELOCITY = 550.0f;
    private static final float GROUND_Y = WorldConfig.GROUND_Y;
    private static final float BASE_MOVE_SPEED = 300.0f;
    private boolean isGrounded = false;

    // ijin nambahin  --Baygonz
    // HP player dan invulnerable
    private float hp = 100f;
    private final float maxHp = 100f;
    private float invulnTimer = 0f;
    private static final float INVULN_DURATION = 1.0f;
    // ijin nambahin  --Baygonz

    // Status Arah Menghadap (Default Kanan)
    private boolean facingRight = true;

    // Konfigurasi Senjata
    private static final float LINEAR_COOLDOWN = 0.15f;
    private static final float LINEAR_DAMAGE = 10f;

    private static final float SPREAD_COOLDOWN = 0.22f;
    private static final float SPREAD_DAMAGE = 6f;
    private static final float SPREAD_RANGE = 260f;
    private static final float SPREAD_ANGLE = 15f;
    // faisal : menambah stats untuk menyimpan buff yang aktif
    private final PlayerStats stats;
    private WeaponType currentWeapon = WeaponType.LINEAR;
    private float shootTimer = 0;

    public Player(float x, float y) {
        this(x, y, new PlayerStats());
    }

    public Player(float x, float y, PlayerStats stats) {
        super(x, y, 32, 48, BASE_MOVE_SPEED);
        this.stats = stats;
        syncStats();
    }

    public void syncStats() {
        this.speed = BASE_MOVE_SPEED * stats.getMoveSpeedMultiplier();
    }

    // ijin nambahin  --Baygonz
    // kebal saat kena damage
    public void takeDamage(float damage) {
        if (invulnTimer > 0f || !active) return;
        hp -= damage;
        invulnTimer = INVULN_DURATION;
        System.out.println("Player HP: " + hp);
        if (hp <= 0f) { hp = 0f; active = false; }
    }
    public float getHp() { return hp; }
    public float getMaxHp() { return maxHp; }
    public boolean isInvulnerable() { return invulnTimer > 0f; }
    // ijin nambahin  --Baygonz

    @Override
    public void update() {
        if (!active) return;
        float dt = Gdx.graphics.getDeltaTime();

        // ijin nambahin  --Baygonz
        if (invulnTimer > 0f) invulnTimer -= dt;
        // ijin nambahin  --Baygonz

        // Ganti senjata pakai tombol TAB
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            currentWeapon = (currentWeapon == WeaponType.LINEAR)
                ? WeaponType.SPREAD
                : WeaponType.LINEAR;
        }

        // 1. Horizontal Movement & Facing Logic (Arrow Left / Right)
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x -= speed * dt;
            facingRight = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            x += speed * dt;
            facingRight = true;
        }

        // 2. Jumping Logic (Tombol Z)
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z) && isGrounded) {
            velY = JUMP_VELOCITY;
            isGrounded = false;
        }

        // Gravitasi & Ground Collision
        velY += GRAVITY * dt;
        y += velY * dt;

        if (y <= GROUND_Y) {
            y = GROUND_Y;
            velY = 0;
            isGrounded = true;
        }

        // Pembatas Layar
        if (x < 0) x = 0;
        if (x > Gdx.graphics.getWidth() - width) x = Gdx.graphics.getWidth() - width;

        if (shootTimer > 0) shootTimer -= dt;
    }

    // 3. Logika Menembak 8-Axis dengan Keyboard (Tombol X + Arrow Keys)
    public void shoot(List<Bullet> bullets) {
        if (!active) return;
        if (shootTimer <= 0 && Gdx.input.isKeyPressed(Input.Keys.X)) {
            float dirX = 0;
            float dirY = 0;

            boolean up = Gdx.input.isKeyPressed(Input.Keys.UP);
            boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN);
            boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
            boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

            // Deteksi Arah Aiming dari Panah
            if (up) dirY += 1;
            if (down && !isGrounded) dirY -= 1; // Menembak ke bawah hanya saat di udara
            if (left) dirX -= 1;
            if (right) dirX += 1;

            // Jika tidak menekan panah sama sekali, nembak sesuai arah badan menghadap (Kiri/Kanan)
            if (dirX == 0 && dirY == 0) {
                dirX = facingRight ? 1 : -1;
            }

            // Normalisasi vektor agar kecepatan tembakan diagonal tetap konstan
            Vector2 dir = new Vector2(dirX, dirY).nor();

            float bulletStartX = x + width / 2f - 4;
            float bulletStartY = y + height / 2f - 4;

            float dmgMult = stats.getDamageMultiplier();
            float aspdMult = stats.getAttackSpeedMultiplier();

            if (currentWeapon == WeaponType.LINEAR) {
                shootTimer = LINEAR_COOLDOWN / aspdMult;
                bullets.add(new Bullet(bulletStartX, bulletStartY, dir.x, dir.y, LINEAR_DAMAGE * dmgMult, Color.YELLOW, -1f));
            } else {
                shootTimer = SPREAD_COOLDOWN / aspdMult;
                float baseAngle = dir.angleDeg();
                float[] angleOffsets = {-SPREAD_ANGLE, 0f, SPREAD_ANGLE};

                for (float offset : angleOffsets) {
                    float angle = baseAngle + offset;
                    float spreadX = MathUtils.cosDeg(angle);
                    float spreadY = MathUtils.sinDeg(angle);
                    bullets.add(new Bullet(bulletStartX, bulletStartY, spreadX, spreadY, SPREAD_DAMAGE * dmgMult, Color.RED, SPREAD_RANGE));
                }
            }
        }
    }

    public WeaponType getCurrentWeapon() {
        return currentWeapon;
    }

    @Override
    public void render(ShapeRenderer shape) {
        if (!active) return;

        // ijin nambahin  --Baygonz
        if (invulnTimer > 0f && ((int) (invulnTimer * 10)) % 2 == 0) return;  // kedapkedip
        // ijin nambahin  --Baygonz

        shape.setColor(Color.CYAN);
        shape.rect(x, y, width, height);

        // Indikator visual sederhana (Mata/Arah Menghadap)
        shape.setColor(Color.WHITE);
        float eyeX = facingRight ? (x + width - 8) : x + 2;
        shape.rect(eyeX, y + height - 12, 6, 6);
    }
}
