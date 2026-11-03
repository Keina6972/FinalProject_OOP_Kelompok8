package com.kelompok8.frontend.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.entities.enemies.attacks.BossAttack;
import com.kelompok8.frontend.entities.enemies.attacks.SlashAttack;

public class Boss extends Enemy {
    private static final float IDLE_TIME = 1.5f;

    private final Player target;
    private final BossAttack slash = new SlashAttack(0.6f, 0.2f, 0.5f, 130f, 70f, 20f);
    private BossAttack current;
    private float idleTimer = IDLE_TIME;

    public Boss(String name, float x, float y, Player target) {
        super(name, x, y, 80, 120, 300f, Color.RED);
        this.target = target;
    }

    @Override
    protected void updateBehavior() {
        float dt = Gdx.graphics.getDeltaTime();

        //kalo ada serangan berjalan, update ampe selesai lalu kembali ke jeda
        if (current != null) {
            current.update(dt);
            if (current.isFinished()) {
                current = null;
                idleTimer = IDLE_TIME;
            }
            return;
        }

        //kalo idle, hitung mundur jeda, lalu nyerang
        idleTimer -= dt;
        if (idleTimer <= 0f) {
            current = slash;
            current.start(this, target);
        }
    }

    @Override
    public void render(ShapeRenderer shape) {
        super.render(shape); //gambar badan + HP bar
        if (active && current != null) current.render(shape); //gambar efek serangan
    }

    @Override
    protected void onDefeated() { current = null; }
}
