package com.kelompok8.frontend.entities.enemies.attacks;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.entities.enemies.Enemy;
import com.kelompok8.frontend.entities.Player;

public interface BossAttack {
    void start(Enemy owner, Player target);   //owner = penyerang, mulai serang
    void update(float dt);
    boolean isFinished();
    void render(ShapeRenderer shape); //range serangan
}
