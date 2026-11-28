package com.kelompok8.frontend.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.kelompok8.frontend.audio.AudioManager;
import com.kelompok8.frontend.audio.SoundType;
import com.kelompok8.frontend.cards.PlayerStats;
import com.kelompok8.frontend.ui.PlayerAssets;
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

    // HP player dan invulnerable
    private float hp;
    private final float maxHp;
    private float invulnTimer = 0f;
    private static final float INVULN_DURATION = 1.0f;

    // Konfigurasi Dash / Dodge
    private static final float DASH_SPEED = 700.0f;
    private static final float DASH_DURATION = 0.16f;
    private static final float DASH_COOLDOWN = 0.6f;

    private boolean isDashing = false;
    private float dashTimer = 0f;
    private float dashCooldownTimer = 0f;
    private int dashDir = 1;
    private boolean canAirDash = true;

    // Status Arah Menghadap (Default Kanan)
    private boolean facingRight = true;
    private boolean isMovingHorizontally = false;

    // Timer & State Animasi Sprite
    private float stateTimer = 0f;
    private float hurtAnimTimer = 0f;
    private float shootAnimTimer = 0f;
    private boolean isDying = false;
    private float deathTimer = 0f;
    private boolean dead = false;

    // Konfigurasi Senjata
    private static final float LINEAR_COOLDOWN = 0.15f;
    private static final float LINEAR_DAMAGE = 10f;

    private static final float SPREAD_COOLDOWN = 0.22f;
    private static final float SPREAD_DAMAGE = 6f;
    private static final float SPREAD_RANGE = 260f;
    private static final float SPREAD_ANGLE = 15f;

    private final PlayerStats stats;
    private WeaponType currentWeapon = WeaponType.LINEAR;
    private float shootTimer = 0;

    public Player(float x, float y) {
        this(x, y, new PlayerStats());
    }

    public Player(float x, float y, PlayerStats stats) {
        super(x, y, 32, 48, BASE_MOVE_SPEED);
        this.stats = stats;
        this.maxHp = stats.getMaxHp();
        this.hp = stats.getCurrentHp();
        syncStats();
    }

    public void syncStats() {
        this.speed = BASE_MOVE_SPEED * stats.getMoveSpeedMultiplier();
    }

    public void takeDamage(float damage) {
        if (isInvulnerable() || !active || isDying) return;
        hp -= damage;
        invulnTimer = INVULN_DURATION;
        hurtAnimTimer = 0.42f;
        stats.setCurrentHp(hp);
        System.out.println("Player HP: " + hp);
        if (hp <= 0f) {
            hp = 0f;
            active = false;
            isDying = true;
            deathTimer = 0f;
            AudioManager.getInstance().play(SoundType.PLAYER_DEATH);
        } else {
            AudioManager.getInstance().play(SoundType.PLAYER_HURT);
        }
    }

    public float getHp() { return hp; }
    public float getMaxHp() { return maxHp; }
    public boolean isInvulnerable() { return invulnTimer > 0f || isDashing; }
    public boolean isDashing() { return isDashing; }
    public boolean isDead() { return dead; }

    @Override
    public void update() {
        float dt = Gdx.graphics.getDeltaTime();
        stateTimer += dt;

        // Jalankan animasi mati dulu sebelum masuk GameOverState
        if (isDying) {
            deathTimer += dt;
            Animation<TextureRegion> deathAnim = PlayerAssets.getInstance().deathAnim;
            if (deathAnim == null || deathAnim.isAnimationFinished(deathTimer)) {
                dead = true;
            }
            return;
        }

        if (!active) return;

        if (invulnTimer > 0f) invulnTimer -= dt;
        if (hurtAnimTimer > 0f) hurtAnimTimer -= dt;
        if (shootAnimTimer > 0f) shootAnimTimer -= dt;
        if (dashCooldownTimer > 0f) dashCooldownTimer -= dt;

        // Ganti senjata pakai tombol TAB
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            currentWeapon = (currentWeapon == WeaponType.LINEAR)
                ? WeaponType.SPREAD
                : WeaponType.LINEAR;
            AudioManager.getInstance().play(SoundType.PLAYER_WEAPON_SWITCH);
        }

        // Dash / Dodge (Tombol Left Shift)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)
            && !isDashing && dashCooldownTimer <= 0f && (isGrounded || canAirDash)) {
            startDash();
        }

        isMovingHorizontally = false;

        if (isDashing) {
            x += dashDir * DASH_SPEED * dt;
            velY = 0f;
            dashTimer -= dt;
            if (dashTimer <= 0f) {
                isDashing = false;
            }
        } else {
            // Horizontal
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                x -= speed * dt;
                facingRight = false;
                isMovingHorizontally = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                x += speed * dt;
                facingRight = true;
                isMovingHorizontally = true;
            }

            // Jump
            if (Gdx.input.isKeyJustPressed(Input.Keys.Z) && isGrounded) {
                velY = JUMP_VELOCITY;
                isGrounded = false;
                stateTimer = 0f;
                AudioManager.getInstance().play(SoundType.PLAYER_JUMP);
            }

            // Gravitasi & Ground Collision
            velY += GRAVITY * dt;
            y += velY * dt;

            if (y <= GROUND_Y) {
                y = GROUND_Y;
                velY = 0;
                isGrounded = true;
                canAirDash = true;
            }
        }

        // Pembatas Layar
        if (x < 0) x = 0;
        if (x > WorldConfig.WORLD_WIDTH - width) x = WorldConfig.WORLD_WIDTH - width;

        if (shootTimer > 0) shootTimer -= dt;
    }

    private void startDash() {
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        if (left && !right) {
            dashDir = -1;
            facingRight = false;
        } else if (right && !left) {
            dashDir = 1;
            facingRight = true;
        } else {
            dashDir = facingRight ? 1 : -1;
        }

        isDashing = true;
        dashTimer = DASH_DURATION;
        dashCooldownTimer = DASH_COOLDOWN;
        stateTimer = 0f;
        velY = 0f;
        if (!isGrounded) {
            canAirDash = false;
        }
        AudioManager.getInstance().play(SoundType.PLAYER_DASH);
    }

    public void shoot(List<Bullet> bullets) {
        if (!active || isDashing || isDying) return;
        if (shootTimer <= 0 && Gdx.input.isKeyPressed(Input.Keys.X)) {
            float dirX = 0;
            float dirY = 0;

            boolean up = Gdx.input.isKeyPressed(Input.Keys.UP);
            boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN);
            boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
            boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

            if (up) dirY += 1;
            if (down && !isGrounded) dirY -= 1;
            if (left) dirX -= 1;
            if (right) dirX += 1;

            if (dirX == 0 && dirY == 0) {
                dirX = facingRight ? 1 : -1;
            }

            // Trigger animasi mengarahkan tongkat
            if (shootAnimTimer <= 0f) {
                stateTimer = 0f;
            }
            shootAnimTimer = 0.28f;

            Vector2 dir = new Vector2(dirX, dirY).nor();

            // Posisi ujung lentera tongkat
            float muzzleOffsetX = (dirX == 0) ? 0f : (facingRight ? 36f : -36f);
            float muzzleOffsetY = (dirX == 0 && dirY > 0) ? 56f : 42f;
            float bulletStartX = x + width / 2f + muzzleOffsetX - 4f;
            float bulletStartY = y + muzzleOffsetY - 4f;

            float dmgMult = stats.getDamageMultiplier();
            float aspdMult = stats.getAttackSpeedMultiplier();

            if (currentWeapon == WeaponType.LINEAR) {
                shootTimer = LINEAR_COOLDOWN / aspdMult;
                bullets.add(new Bullet(bulletStartX, bulletStartY, dir.x, dir.y, LINEAR_DAMAGE * dmgMult, Color.YELLOW, -1f));
                AudioManager.getInstance().play(SoundType.PLAYER_SHOOT_LINEAR);
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
                AudioManager.getInstance().play(SoundType.PLAYER_SHOOT_SPREAD);
            }
        }
    }

    public WeaponType getCurrentWeapon() {
        return currentWeapon;
    }

    private TextureRegion getCurrentFrame(PlayerAssets assets) {
        if (isDying && assets.deathAnim != null) {
            return assets.deathAnim.getKeyFrame(deathTimer, false);
        }
        if (hurtAnimTimer > 0f && assets.hurtAnim != null) {
            float elapsed = 0.42f - hurtAnimTimer;
            return assets.hurtAnim.getKeyFrame(elapsed, false);
        }
        if (isDashing && assets.dashAnim != null) {
            return assets.dashAnim.getKeyFrame(stateTimer, true);
        }
        if (shootAnimTimer > 0f && assets.shootAnim != null) {
            float elapsed = 0.28f - shootAnimTimer;
            return assets.shootAnim.getKeyFrame(elapsed, false);
        }
        if (!isGrounded) {
            if (velY > 0f && assets.jumpAirAnim != null) {
                return assets.jumpAirAnim.getKeyFrame(stateTimer, true);
            }
            if (assets.fallAirAnim != null) {
                return assets.fallAirAnim.getKeyFrame(stateTimer, false);
            }
        }
        if (isMovingHorizontally && assets.walkAnim != null) {
            return assets.walkAnim.getKeyFrame(stateTimer, true);
        }
        return assets.idleAnim.getKeyFrame(stateTimer, true);
    }

    public void render(SpriteBatch batch) {
        if (!active && !isDying) return;
        PlayerAssets assets = PlayerAssets.getInstance();
        if (!assets.isLoaded()) return;

        if (!isDying && invulnTimer > 0f && !isDashing && ((int) (invulnTimer * 12)) % 2 == 0) {
            return;
        }

        TextureRegion frame = getCurrentFrame(assets);
        if (frame == null) return;

        float aspect = (float) frame.getRegionWidth() / (float) frame.getRegionHeight();
        float drawH = 82f;
        float actualDrawW = drawH * aspect;
        float baseBodyW = drawH * 0.85f;

        float drawY = y - 2f;
        float drawX;
        float drawW;

        if (facingRight) {
            drawX = x + (width - baseBodyW) / 2f;
            drawW = actualDrawW;
        } else {
            drawX = x + (width + baseBodyW) / 2f;
            drawW = -actualDrawW;
        }

        if (isDashing) {
            batch.setColor(0.4f, 0.85f, 1f, 0.45f);
            batch.draw(frame, drawX - dashDir * 16f, drawY, drawW, drawH);
            batch.setColor(Color.WHITE);
        }

        batch.draw(frame, drawX, drawY, drawW, drawH);
    }

    @Override
    public void render(ShapeRenderer shape) {
        // Fallback kotak hanya muncul jika file sprite player belum ketemu
        if (PlayerAssets.getInstance().isLoaded()) return;
        if (!active) return;

        if (invulnTimer > 0f && !isDashing && ((int) (invulnTimer * 10)) % 2 == 0) return;

        if (isDashing) {
            shape.setColor(0f, 0.55f, 0.75f, 1f);
            shape.rect(x - dashDir * 12f, y, width, height);
            shape.setColor(Color.SKY);
        } else {
            shape.setColor(Color.CYAN);
        }
        shape.rect(x, y, width, height);

        shape.setColor(Color.WHITE);
        float eyeX = facingRight ? (x + width - 8) : x + 2;
        shape.rect(eyeX, y + height - 12, 6, 6);
    }
}
