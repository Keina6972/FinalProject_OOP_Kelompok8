package com.kelompok8.frontend.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.kelompok8.frontend.ui.PlayerAssets;
import com.kelompok8.frontend.utils.WorldConfig;

public class Bullet extends Entity {
    private static final float BULLET_SPEED = 650f;

    private final float dirX;
    private final float dirY;
    private final float angleDeg;
    private final float damage;
    private final Color color;
    private final float maxRange;

    private float distanceTraveled = 0f;
    private float stateTimer = 0f;

    public Bullet(float x, float y, float dirX, float dirY, float damage, Color color, float maxRange) {
        super(x, y, 10, 10, BULLET_SPEED);
        this.dirX = dirX;
        this.dirY = dirY;
        this.angleDeg = MathUtils.atan2(dirY, dirX) * MathUtils.radiansToDegrees;
        this.damage = damage;
        this.color = color;
        this.maxRange = maxRange;
    }

    @Override
    public void update() {
        if (!active) return;
        float dt = Gdx.graphics.getDeltaTime();
        stateTimer += dt;

        float step = speed * dt;
        x += dirX * step;
        y += dirY * step;
        distanceTraveled += step;

        if (maxRange > 0f && distanceTraveled >= maxRange) {
            active = false;
            return;
        }

        if (x < -40f || x > WorldConfig.WORLD_WIDTH + 40f || y < -40f || y > WorldConfig.WORLD_HEIGHT + 40f) {
            active = false;
        }
    }

    public float getDamage() {
        return damage;
    }

    public void render(SpriteBatch batch) {
        if (!active) return;
        Animation<TextureRegion> anim = PlayerAssets.getInstance().bulletAnim;
        if (anim == null) return;

        TextureRegion frame = anim.getKeyFrame(stateTimer, true);
        float aspect = (float) frame.getRegionWidth() / (float) frame.getRegionHeight();
        float drawH = 22f;
        float drawW = drawH * aspect;

        float centerX = x + width / 2f;
        float centerY = y + height / 2f;
        float drawX = centerX - drawW / 2f;
        float drawY = centerY - drawH / 2f;

        if (color == Color.RED) {
            batch.setColor(1f, 0.42f, 0.35f, 1f);
        } else {
            batch.setColor(Color.WHITE);
        }

        batch.draw(
            frame,
            drawX, drawY,
            drawW / 2f, drawH / 2f,
            drawW, drawH,
            1f, 1f,
            angleDeg
        );
        batch.setColor(Color.WHITE);
    }

    @Override
    public void render(ShapeRenderer shape) {
        // Fallback kotak hanya muncul jika sprite bullet belum ketemu
        if (PlayerAssets.getInstance().bulletAnim != null) return;
        if (!active) return;

        shape.setColor(color);
        shape.rect(x, y, width, height);
    }
}
