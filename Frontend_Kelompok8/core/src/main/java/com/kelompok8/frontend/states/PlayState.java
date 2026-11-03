package com.kelompok8.frontend.states;

import com.badlogic.gdx.Gdx;
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
    private DummyTarget dummyTarget;

    public PlayState() {
        this.player = new Player(Gdx.graphics.getWidth() / 2f - 16, Gdx.graphics.getHeight() / 2f - 16);
        this.bullets = new ArrayList<>();
        this.dummyTarget = new DummyTarget(Gdx.graphics.getWidth() / 2f - 25, Gdx.graphics.getHeight() - 100, 50, 50);
    }

    public void update() {
        player.update();

        // Mekanik Menembak: Klik Kiri Mouse
        if (Gdx.input.isTouched()) {
            // libGDX koordinat Y mouse terbalik (0 di atas), jadi perlu di-flip
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            Bullet b = player.shoot(mouseX, mouseY);
            if (b != null) bullets.add(b);
        }

        // Update Bullets & Hit Detection
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update();

            if (dummyTarget.isActive() && CollisionDetector.checkCollision(b, dummyTarget)) {
                b.setActive(false);
                dummyTarget.takeHit();
            }

            if (!b.isActive()) {
                it.remove();
            }
        }

        dummyTarget.update();
    }

    public void render(ShapeRenderer shape) {
        player.render(shape);
        for (Bullet b : bullets) {
            b.render(shape);
        }
        dummyTarget.render(shape);
    }

    private class DummyTarget extends Entity {
        private int hitCount = 0;

        public DummyTarget(float x, float y, float w, float h) {
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
