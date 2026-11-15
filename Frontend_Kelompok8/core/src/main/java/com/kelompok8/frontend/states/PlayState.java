package com.kelompok8.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.entities.Bullet;
import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.entities.enemies.Boss;
import com.kelompok8.frontend.entities.enemies.Enemy;
import com.kelompok8.frontend.entities.enemies.MiniBossA;
import com.kelompok8.frontend.utils.CollisionDetector;
import com.kelompok8.frontend.utils.WorldConfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayState extends GameState {
    private final Player player;
    private final List<Bullet> bullets;
    private final List<Enemy> enemies;
    private final Boss boss;

    public PlayState(GameStateManager gsm) {
        super(gsm);
        this.player = new Player(100, WorldConfig.GROUND_Y);
        this.bullets = new ArrayList<>();
        this.enemies = new ArrayList<>();

        this.boss = new Boss("Boss", Gdx.graphics.getWidth() - 150, WorldConfig.GROUND_Y, player);
        // rizki : sementara: Boss dimatikan biar MiniBossA bisa dites sendirian (apus baris ini kalo spawn order dah jadi)
        this.boss.setActive(false);

        this.enemies.add(boss);
        this.enemies.add(new MiniBossA("Miniboss A", Gdx.graphics.getWidth() - 200, WorldConfig.GROUND_Y, player));
    }

    @Override
    public void update(float dt) {
        player.update();

        // Panggil fungsi nembak (otomatis mengecek tombol X)
        Bullet b = player.shoot();
        if (b != null) {
            bullets.add(b);
        }

        // Update Peluru & Collision Check ke seluruh Enemy secara polimorfik
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            bullet.update();

            if (bullet.isActive()) {
                for (Enemy enemy : enemies) {
                    if (CollisionDetector.checkCollision(bullet, enemy)) {
                        bullet.setActive(false);
                        enemy.takeDamage(bullet.getDamage());
                        break;
                    }
                }
            }

            if (!bullet.isActive()) {
                it.remove();
            }
        }

        for (Enemy enemy : enemies) {
            enemy.update();
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        shape.begin(ShapeRenderer.ShapeType.Filled);

        // Gambarkan Lantai Arena (Ground)
        shape.setColor(Color.GRAY);
        shape.rect(0, 0, Gdx.graphics.getWidth(), WorldConfig.GROUND_Y);

        player.render(shape);
        for (Bullet bullet : bullets) {
            bullet.render(shape);
        }
        for (Enemy enemy : enemies) {
            enemy.render(shape);
        }

        // Mukhtar: Indikator Phase Boss untuk kebutuhan debugging
        if (boss != null && boss.isActive()) {
            shape.setColor(boss.getCurrentPhase() == 1 ? Color.WHITE : Color.ORANGE);
            shape.rect(10f, Gdx.graphics.getHeight() - 20f, 100f, 8f);
        }

        shape.end();
    }

    @Override
    public void dispose() {
    }
}
