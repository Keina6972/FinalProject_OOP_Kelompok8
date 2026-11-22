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
import com.kelompok8.frontend.entities.enemies.projectiles.ProjectileManager;
import com.kelompok8.frontend.utils.CollisionDetector;
import com.kelompok8.frontend.utils.WorldConfig;
import com.kelompok8.frontend.cards.PlayerStats;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayState extends GameState {
    private static final float BOSS_CONTACT_DAMAGE = 10f;
    // Faisal : menambahkan stat baru pada player
    private final PlayerStats playerStats;
    private final Player player;
    private final List<Bullet> bullets;
    private final List<Enemy> enemies;
    private final Boss boss;
    private final ProjectileManager projectiles;

    public PlayState(GameStateManager gsm) {
        this(gsm, new PlayerStats());
    }

    public PlayState(GameStateManager gsm, PlayerStats playerStats) {
        // setup semua yang akan digunakan pada saat main
        super(gsm);
        this.playerStats = playerStats;
        this.player = new Player(100, WorldConfig.GROUND_Y, playerStats);
        this.bullets = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.projectiles = new ProjectileManager();

        this.boss = new Boss("Boss", Gdx.graphics.getWidth() - 150, WorldConfig.GROUND_Y, player);
        // rizki : sementara: Boss dimatikan biar MiniBossA bisa dites sendirian (apus baris ini kalo spawn order dah jadi)
        this.boss.setActive(false);

        this.enemies.add(boss);
        this.enemies.add(new MiniBossA("Miniboss A", Gdx.graphics.getWidth() - 200, WorldConfig.GROUND_Y, player, projectiles));
    }

    @Override
    public void update(float dt) {
        // 1. Cek State Mati Player (HP <= 0)
        if (!player.isActive()) {
            gsm.setState(new GameOverState(gsm));
            return;
        }

        player.update();

        // Panggil fungsi nembak (otomatis mengecek tombol X dan senjata aktif)
        player.shoot(bullets);

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

        boolean allEnemiesDefeated = true;
        for (Enemy enemy : enemies) {
            enemy.update();
            if (enemy.isActive()) {
                allEnemiesDefeated = false;
                // kalau kolisi dengan boss maka kena damage 10hp
                if (CollisionDetector.checkCollision(player, enemy)) {
                    player.takeDamage(BOSS_CONTACT_DAMAGE);
                }
            }
        }

        // Rizki : biar projectile musuh bergerak dan mengecek collision ke player
        projectiles.update(player);

        // 2. Cek Jika Boss agar Pindah ke Tampilan Pilih 3 Kartu
        if (allEnemiesDefeated) {
            gsm.setState(new CardSelectState(gsm, playerStats));
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        shape.begin(ShapeRenderer.ShapeType.Filled);

        // ground arena
        shape.setColor(Color.GRAY);
        shape.rect(0, 0, Gdx.graphics.getWidth(), WorldConfig.GROUND_Y);

        player.render(shape);
        for (Bullet bullet : bullets) {
            bullet.render(shape);
        }
        for (Enemy enemy : enemies) {
            enemy.render(shape);
        }

        projectiles.render(shape);

        // HUD HP Bar Player di pojok kiri atas
        float hpRatio = Math.max(0f, player.getHp() / player.getMaxHp());
        shape.setColor(Color.DARK_GRAY);
        shape.rect(10f, Gdx.graphics.getHeight() - 25f, 160f, 12f);
        shape.setColor(Color.CYAN);
        shape.rect(10f, Gdx.graphics.getHeight() - 25f, 160f * hpRatio, 12f);

        // Active weapon (kuning = linear, merah = spread)
        shape.setColor(player.getCurrentWeapon() == Player.WeaponType.LINEAR ? Color.YELLOW : Color.RED);
        shape.rect(178f, Gdx.graphics.getHeight() - 25f, 22f, 12f);

        // Mukhtar: Indikator Phase Boss untuk kebutuhan debugging
        if (boss != null && boss.isActive()) {
            shape.setColor(boss.getCurrentPhase() == 1 ? Color.WHITE : Color.ORANGE);
            shape.rect(10f, Gdx.graphics.getHeight() - 42f, 100f, 8f);
        }

        shape.end();
    }

    @Override
    public void dispose() {
        projectiles.clear();
    }
}
