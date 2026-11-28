package com.kelompok8.frontend.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class FontManager {
    private static FontManager instance;
    private static final String FONT_PATH = "fonts/main_font.ttf";

    public BitmapFont titleFont;
    public BitmapFont headerFont;
    public BitmapFont buttonFont;
    public BitmapFont bodyFont;
    public BitmapFont smallFont;

    private FontManager() {}

    public static FontManager getInstance() {
        if (instance == null) {
            instance = new FontManager();
        }
        return instance;
    }

    public void loadAll() {
        if (Gdx.files.internal(FONT_PATH).exists()) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_PATH));
            titleFont = createFont(generator, 40);
            headerFont = createFont(generator, 28);
            buttonFont = createFont(generator, 24);
            bodyFont = createFont(generator, 18);
            smallFont = createFont(generator, 15);
            generator.dispose();
        } else {
            Gdx.app.error("FontManager", "Font TTF belum ketemu di: " + FONT_PATH + ", pakai font bawaan dulu.");
            titleFont = createFallbackFont(2.6f);
            headerFont = createFallbackFont(2.0f);
            buttonFont = createFallbackFont(1.6f);
            bodyFont = createFallbackFont(1.2f);
            smallFont = createFallbackFont(1.0f);
        }
    }

    private BitmapFont createFont(FreeTypeFontGenerator generator, int size) {
        FreeTypeFontParameter param = new FreeTypeFontParameter();
        param.size = size;
        param.minFilter = Texture.TextureFilter.Nearest;
        param.magFilter = Texture.TextureFilter.Nearest;
        return generator.generateFont(param);
    }

    private BitmapFont createFallbackFont(float scale) {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(scale);
        return font;
    }

    public void dispose() {
        if (titleFont != null) titleFont.dispose();
        if (headerFont != null) headerFont.dispose();
        if (buttonFont != null) buttonFont.dispose();
        if (bodyFont != null) bodyFont.dispose();
        if (smallFont != null) smallFont.dispose();
    }
}
