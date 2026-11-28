package com.kelompok8.frontend.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class UIAssets {
    private static UIAssets instance;

    private Texture tilesheetTex;
    private Texture keyboardTex;

    // Button & Card Frame NinePatches (dari 32x32-Tilesheet.png)
    public NinePatch btnNormalPatch;
    public NinePatch btnHoverPatch;
    public NinePatch cardFramePatch;

    // Health Bar NinePatches (dari 32x32-Tilesheet.png)
    public NinePatch hpFramePatch;
    public NinePatch hpFillRedPatch;
    public NinePatch hpFillGreenPatch;
    public NinePatch hpFillBluePatch;

    // Keyboard Key Icons 16x16 (dari kb_light_all.png)
    public TextureRegion keyUp;
    public TextureRegion keyDown;
    public TextureRegion keyLeft;
    public TextureRegion keyRight;
    public TextureRegion keyZ;
    public TextureRegion keyX;
    public TextureRegion keyTab;
    public TextureRegion keyShift;

    private UIAssets() {}

    public static UIAssets getInstance() {
        if (instance == null) {
            instance = new UIAssets();
        }
        return instance;
    }

    public void loadAll() {
        tilesheetTex = loadTextureSafe("ui/32x32-Tilesheet.png");
        keyboardTex = loadTextureSafe("ui/kb_light_all.png");

        if (tilesheetTex != null) {
            // Bingkai kartu 96x96 di pojok kiri atas
            TextureRegion cardRegion = new TextureRegion(tilesheetTex, 0, 0, 96, 96);
            cardFramePatch = new NinePatch(cardRegion, 24, 24, 24, 24);

            // Tombol normal & hover (64x32)
            TextureRegion normalRegion = new TextureRegion(tilesheetTex, 0, 96, 64, 32);
            btnNormalPatch = new NinePatch(normalRegion, 14, 14, 10, 10);

            TextureRegion hoverRegion = new TextureRegion(tilesheetTex, 64, 96, 64, 32);
            btnHoverPatch = new NinePatch(hoverRegion, 14, 14, 10, 10);

            // Bingkai HP bar (96x12 tanpa padding transparan) -> scale 2x jadi tinggi 24px
            TextureRegion hpFrameRegion = new TextureRegion(tilesheetTex, 96, 138, 96, 12);
            hpFramePatch = new NinePatch(hpFrameRegion, 14, 14, 3, 3);
            hpFramePatch.scale(2f, 2f);

            // Isi bar merah, hijau, biru (86x6) -> scale 2x jadi tinggi 12px pas di dalam bingkai
            TextureRegion redRegion = new TextureRegion(tilesheetTex, 199, 173, 86, 6);
            hpFillRedPatch = new NinePatch(redRegion, 2, 2, 1, 1);
            hpFillRedPatch.scale(2f, 2f);

            TextureRegion greenRegion = new TextureRegion(tilesheetTex, 295, 173, 86, 6);
            hpFillGreenPatch = new NinePatch(greenRegion, 2, 2, 1, 1);
            hpFillGreenPatch.scale(2f, 2f);

            TextureRegion blueRegion = new TextureRegion(tilesheetTex, 295, 141, 86, 6);
            hpFillBluePatch = new NinePatch(blueRegion, 2, 2, 1, 1);
            hpFillBluePatch.scale(2f, 2f);
        }

        if (keyboardTex != null) {
            keyZ = getKeyIcon(0, 25);
            keyX = getKeyIcon(0, 23);
            keyTab = getKeyIcon(8, 2);
            keyShift = getKeyIcon(8, 5);

            keyUp = getKeyIcon(12, 12);
            keyLeft = getKeyIcon(12, 13);
            keyDown = getKeyIcon(12, 14);
            keyRight = getKeyIcon(12, 15);
        }
    }

    public void drawHealthBar(SpriteBatch batch, float x, float y, float width, float height,
                              float currentHp, float maxHp) {
        if (hpFramePatch == null || hpFillRedPatch == null) return;

        hpFramePatch.draw(batch, x, y, width, height);

        float ratio = (maxHp > 0f) ? MathUtils.clamp(currentHp / maxHp, 0f, 1f) : 0f;
        if (ratio > 0f) {
            float padX = 24f;
            float padY = 6f;
            float innerMaxW = width - (padX * 2f);
            float innerH = height - (padY * 2f);
            float fillW = Math.max(8f, innerMaxW * ratio);

            hpFillRedPatch.draw(batch, x + padX, y + padY, fillW, innerH);
        }
    }

    private TextureRegion getKeyIcon(int col, int row) {
        return new TextureRegion(keyboardTex, col * 16, row * 16, 16, 16);
    }

    private Texture loadTextureSafe(String path) {
        if (!Gdx.files.internal(path).exists()) {
            Gdx.app.error("UIAssets", "File UI tidak ditemukan: " + path);
            return null;
        }
        Texture tex = new Texture(Gdx.files.internal(path));
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return tex;
    }

    public void dispose() {
        if (tilesheetTex != null) tilesheetTex.dispose();
        if (keyboardTex != null) keyboardTex.dispose();
    }
}
