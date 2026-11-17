package com.kelompok8.frontend.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.entities.Entity;

public abstract class Enemy extends Entity {
    protected final String name;
    protected final float maxHp;
    protected float hp;
    protected final Color baseColor;

    private static final float FLASH_DURATION = 0.08f;
    private float flashTimer = 0f;

    protected Enemy(String name, float x, float y, float width, float height, float maxHp, Color baseColor) {
        super(x, y, width, height, 0);
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.baseColor = baseColor;
    }

    //hook buat subclass
    protected void updateBehavior() {}
    protected void onDefeated() {}

    @Override
    public void update() {
        if (!active) return;
        if (flashTimer > 0f) flashTimer -= Gdx.graphics.getDeltaTime();
        //behavior khas masing musuh (idle, attack, dll)
        updateBehavior();
    }

    public boolean takeDamage(float damage) {
        if (!active) return false;

        hp -= damage;
        flashTimer = FLASH_DURATION;

        if (hp <= 0f) {
            hp = 0f;
            active = false;
            onDefeated();
            return true;
        }
        return false;
    }

    @Override
    public void render(ShapeRenderer shape) {
        if (!active) return;

        //efek kena damage (putih bntr)
        shape.setColor(flashTimer > 0f ? Color.WHITE : baseColor);
        shape.rect(x, y, width, height);

        //HP bar, nanti pindahin ke UI ajah
        float barY = y + height + 8f;
        shape.setColor(Color.DARK_GRAY);
        shape.rect(x, barY, width, 6f);
        shape.setColor(Color.RED);
        shape.rect(x, barY, width * getHpRatio(), 6f);
    }

    public boolean isAlive() { return hp > 0f; }
    public String getName() { return name; }
    public float getHp() { return hp; }
    public float getMaxHp() { return maxHp; }
    public float getHpRatio() { return hp / maxHp; }
}
