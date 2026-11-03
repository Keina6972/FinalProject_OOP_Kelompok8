package com.kelompok8.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.entities.Bullet;
import com.kelompok8.frontend.entities.Entity;
import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.utils.CollisionDetector;

import java.util.ArrayList;
import java.util.Iterator;

public class PlayState {
    private Player player;
    private ArrayList<Bullet> bullets;
    private DummyBoss dummyBoss;

    public PlayState() {
        this.player = new Player(100, 100);
        this.bullets = new ArrayList<>();
        // Boss berdiri di atas lantai (Y = 100) di sebelah kanan
        this.dummyBoss = new DummyBoss(Gdx.graphics.getWidth() - 150, 100, 80, 120);
    }

    public void update() {
        player.update();

        // Tembak menggunakan tombol 'J' atau Klik Kiri Mouse
        Bullet b = player.shoot();
        if (b != null) {
            bullets.add(b);
        }

        // Update Peluru & Cek Hitbox ke Bos
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            bullet.update();

            if (dummyBoss.isActive() && CollisionDetector.checkCollision(bullet, dummyBoss)) {
                bullet.setActive(false);
                dummyBoss.takeHit();
            }

            if (!bullet.isActive()) {
                it.remove();
            }
        }

        dummyBoss.update();
    }

    public void render(ShapeRenderer shape) {
        // Gambarkan Lantai Arena (Ground)
        shape.setColor(Color.GRAY);
        shape.rect(0, 0, Gdx.graphics.getWidth(), 100);

        player.render(shape);
        for (Bullet bullet : bullets) {
            bullet.render(shape);
        }
        dummyBoss.render(shape);
    }

    private class DummyBoss extends Entity {
        private int hitCount = 0;

        public DummyBoss(float x, float y, float w, float h) {
            super(x, y, w, h, 0);
        }

        public void takeHit() { hitCount++; }

        @Override
        public void update() {}

        @Override
        public void render(ShapeRenderer shape) {
            shape.setColor(hitCount % 2 == 0 ? Color.RED : Color.WHITE);
            shape.rect(x, y, width, height);
        }
    }
}
