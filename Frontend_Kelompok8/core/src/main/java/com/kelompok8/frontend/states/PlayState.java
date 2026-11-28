package com.kelompok8.frontend.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.cards.PlayerStats;
import com.kelompok8.frontend.entities.Bullet;
import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.entities.enemies.Boss;
import com.kelompok8.frontend.entities.enemies.Enemy;
import com.kelompok8.frontend.entities.enemies.MiniBossA;
import com.kelompok8.frontend.entities.enemies.projectiles.ProjectileManager;
import com.kelompok8.frontend.ui.FontManager;
import com.kelompok8.frontend.ui.UIAssets;
import com.kelompok8.frontend.utils.CollisionDetector;
import com.kelompok8.frontend.utils.WorldConfig;

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
    private final GlyphLayout layout;

    public PlayState(GameStateManager gsm) {
        this(gsm, new PlayerStats());
    }

    public PlayState(GameStateManager gsm, PlayerStats playerStats) {
        super(gsm);
        this.playerStats = playerStats;
        this.player = new Player(100, WorldConfig.GROUND_Y, playerStats);
        this.bullets = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.projectiles = new ProjectileManager();
        this.layout = new GlyphLayout();

        this.boss = new Boss("Boss", WorldConfig.WORLD_WIDTH - 150, WorldConfig.GROUND_Y, player);
        // rizki : sementara: Boss dimatikan biar MiniBossA bisa dites sendirian (apus baris ini kalo spawn order dah jadi)
        this.boss.setActive(false);

        this.enemies.add(boss);
        this.enemies.add(new MiniBossA("Miniboss A", WorldConfig.WORLD_WIDTH - 200, WorldConfig.GROUND_Y, player, projectiles));
    }

    @Override
    public void update(float dt) {
        // 1. Cek State Mati Player (HP <= 0)
        if (player.isDead()) {
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

                if (CollisionDetector.checkCollision(player, enemy)) {
                    player.takeDamage(BOSS_CONTACT_DAMAGE);
                }
            }
        }

        // Rizki : biar projectile musuh bergerak dan mengecek collision ke player
        projectiles.update(player);

        // 2. Cek Jika Boss / Semua Enemy Mati -> Pindah ke Tampilan Pilih 3 Kartu
        if (allEnemiesDefeated) {
            gsm.setState(new CardSelectState(gsm, playerStats));
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        UIAssets ui = UIAssets.getInstance();

        shape.begin(ShapeRenderer.ShapeType.Filled);

        // Gambarkan Lantai Arena (Ground)
        shape.setColor(Color.GRAY);
        shape.rect(0, 0, WorldConfig.WORLD_WIDTH, WorldConfig.GROUND_Y);

        player.render(shape);
        for (Bullet bullet : bullets) {
            bullet.render(shape);
        }
        for (Enemy enemy : enemies) {
            enemy.render(shape);
        }

        projectiles.render(shape);

        // Fallback HUD kalau tekstur HP bar belum ke-load
        if (ui.hpFramePatch == null) {
            float hpRatio = Math.max(0f, player.getHp() / player.getMaxHp());
            shape.setColor(Color.DARK_GRAY);
            shape.rect(14f, WorldConfig.WORLD_HEIGHT - 36f, 180f, 16f);
            shape.setColor(Color.RED);
            shape.rect(14f, WorldConfig.WORLD_HEIGHT - 36f, 180f * hpRatio, 16f);
        }

        // Mukhtar: Indikator Phase Boss untuk kebutuhan debugging
        if (boss != null && boss.isActive()) {
            shape.setColor(boss.getCurrentPhase() == 1 ? Color.WHITE : Color.ORANGE);
            shape.rect(14f, WorldConfig.WORLD_HEIGHT - 54f, 100f, 8f);
        }

        shape.end();

        // Render HUD, Assets & Font
        batch.begin();
        player.render(batch);
        for (Bullet bullet : bullets) {
            bullet.render(batch);
        }
        drawHud(batch, ui);
        batch.end();
    }

    private void drawHud(SpriteBatch batch, UIAssets ui) {
        BitmapFont font = FontManager.getInstance().smallFont;

        float startX = 18f;
        float topY = WorldConfig.WORLD_HEIGHT - 14f;

        // 1. Kolom HP Bar Player
        float hpW = 220f;
        float hpH = 24f;
        float hpY = topY - 18f - hpH;

        font.setColor(Color.GOLD);
        font.draw(batch, "HP", startX + 4f, topY);

        int currentHpInt = Math.max(0, (int) Math.ceil(player.getHp()));
        int maxHpInt = (int) Math.ceil(player.getMaxHp());
        String hpText = currentHpInt + "/" + maxHpInt;
        layout.setText(font, hpText);
        font.setColor(Color.WHITE);
        font.draw(batch, hpText, startX + hpW - layout.width - 4f, topY);

        ui.drawHealthBar(batch, startX, hpY, hpW, hpH, player.getHp(), player.getMaxHp());

        // 2. Kolom Senjata Aktif + Ikon TAB di atasnya
        boolean isLinear = player.getCurrentWeapon() == Player.WeaponType.LINEAR;
        float badgeX = startX + hpW + 16f;
        float badgeW = 136f;
        float badgeH = 28f;
        float badgeY = hpY - 2f;

        if (ui.keyTab != null) {
            batch.draw(ui.keyTab, badgeX + 4f, topY - 13f, 16f, 16f);
        }
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "SWITCH", badgeX + 24f, topY);

        NinePatch badgePatch = isLinear ? ui.btnNormalPatch : ui.btnHoverPatch;
        if (badgePatch != null) {
            badgePatch.draw(batch, badgeX, badgeY, badgeW, badgeH);
        }

        String weaponText = isLinear ? "LINEAR" : "SPREAD";
        layout.setText(font, weaponText);
        font.setColor(isLinear ? Color.GOLD : Color.ORANGE);
        font.draw(
            batch,
            weaponText,
            badgeX + (badgeW - layout.width) / 2f,
            badgeY + (badgeH + layout.height) / 2f - 1f
        );
    }

    @Override
    public void dispose() {
        projectiles.clear();
    }
}
