package com.kelompok8.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.entities.Bullet;
import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.entities.enemies.Boss;
import com.kelompok8.frontend.utils.CollisionDetector;

import java.util.ArrayList;
import java.util.Iterator;

public class PlayState {
    private Player player;
    private ArrayList<Bullet> bullets;
    private Boss boss;

    public PlayState() {
        this.player = new Player(100, 100);
        this.bullets = new ArrayList<>();
        this.boss = new Boss("Boss", Gdx.graphics.getWidth() - 150, 100, player);
    }

    public void update() {
        player.update();

        // Panggil fungsi nembak (otomatis mengecek tombol X)
        Bullet b = player.shoot();
        if (b != null) {
            bullets.add(b);
        }

        // Update Peluru & Collision Check ke Boss
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            bullet.update();

            if (boss.isActive() && CollisionDetector.checkCollision(bullet, boss)) {
                bullet.setActive(false);
                boss.takeDamage(bullet.getDamage());
            }

            if (!bullet.isActive()) {
                it.remove();
            }
        }

        boss.update();
    }

    public void render(ShapeRenderer shape) {
        // Gambarkan Lantai Arena (Ground)
        shape.setColor(Color.GRAY);
        shape.rect(0, 0, Gdx.graphics.getWidth(), 100);

        player.render(shape);
        for (Bullet bullet : bullets) {
            bullet.render(shape);
        }
        boss.render(shape);
        // Mukhtar: Indikator Phase Boss untuk kebutuhan debugging
        shape.setColor(Color.WHITE);

        if (boss.getCurrentPhase() == 1) {
            shape.rect(10f, Gdx.graphics.getHeight() - 20f, 100f, 8f);
        } else {
            shape.setColor(Color.ORANGE);
            shape.rect(10f, Gdx.graphics.getHeight() - 20f, 100f, 8f);
        }
    }
}
