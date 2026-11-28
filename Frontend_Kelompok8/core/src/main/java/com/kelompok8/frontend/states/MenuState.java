package com.kelompok8.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kelompok8.frontend.audio.AudioManager;
import com.kelompok8.frontend.audio.SoundType;
import com.kelompok8.frontend.ui.FontManager;
import com.kelompok8.frontend.ui.UIAssets;
import com.kelompok8.frontend.utils.WorldConfig;

public class MenuState extends GameState {
    private final GlyphLayout layout;
    private final Rectangle playButtonBounds;
    private boolean isHovered = false;

    public MenuState(GameStateManager gsm) {
        super(gsm);
        this.layout = new GlyphLayout();

        float btnWidth = 240f;
        float btnHeight = 64f;
        float btnX = (WorldConfig.WORLD_WIDTH - btnWidth) / 2f;
        float btnY = (WorldConfig.WORLD_HEIGHT - btnHeight) / 2f - 15f;
        this.playButtonBounds = new Rectangle(btnX, btnY, btnWidth, btnHeight);

        AudioManager.getInstance().playMenuBgm();
    }

    @Override
    public void update(float dt) {
        Vector2 mouse = gsm.getMouseWorldPos();

        boolean currentlyHovered = playButtonBounds.contains(mouse.x, mouse.y);
        if (currentlyHovered && !isHovered) {
            AudioManager.getInstance().play(SoundType.UI_HOVER);
        }
        isHovered = currentlyHovered;

        boolean clickedPlay = isHovered && Gdx.input.justTouched();
        boolean pressedStart = Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.Z);

        if (clickedPlay || pressedStart) {
            AudioManager.getInstance().play(SoundType.UI_CLICK);
            AudioManager.getInstance().stopBgm();
            gsm.setState(new PlayState(gsm));
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        float screenW = WorldConfig.WORLD_WIDTH;
        float screenH = WorldConfig.WORLD_HEIGHT;
        FontManager fonts = FontManager.getInstance();
        UIAssets ui = UIAssets.getInstance();

        if (ui.btnNormalPatch == null) {
            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(isHovered ? Color.GOLD : Color.ROYAL);
            shape.rect(playButtonBounds.x, playButtonBounds.y, playButtonBounds.width, playButtonBounds.height);
            shape.end();
        }

        batch.begin();

        if (ui.mainMenuBg != null) {
            batch.draw(ui.mainMenuBg, 0, 0, screenW, screenH);
        }

        // 1. Judul Game
        BitmapFont titleFont = fonts.titleFont;
        String title = "LAST PENANCE";
        layout.setText(titleFont, title);
        titleFont.setColor(Color.WHITE);
        titleFont.draw(batch, title, (screenW - layout.width) / 2f, screenH * 0.72f);

        // 2. Tombol PLAY dengan NinePatch
        NinePatch activePatch = isHovered ? ui.btnHoverPatch : ui.btnNormalPatch;
        if (activePatch != null) {
            activePatch.draw(
                batch,
                playButtonBounds.x,
                playButtonBounds.y,
                playButtonBounds.width,
                playButtonBounds.height
            );
        }

        BitmapFont buttonFont = fonts.buttonFont;
        String playText = "PLAY";
        layout.setText(buttonFont, playText);
        buttonFont.setColor(isHovered ? Color.GOLD : Color.WHITE);
        buttonFont.draw(
            batch,
            playText,
            playButtonBounds.x + (playButtonBounds.width - layout.width) / 2f,
            playButtonBounds.y + (playButtonBounds.height + layout.height) / 2f - 2f
        );

        // 3. Panduan Kontrol dengan Ikon Keycap
        drawControlsBar(batch, fonts.smallFont, ui, screenW, screenH * 0.16f);

        batch.end();
    }

    private void drawControlsBar(SpriteBatch batch, BitmapFont font, UIAssets ui, float screenW, float centerY) {
        if (ui.keyUp == null) {
            String fallback = "Arrow Keys: Move / Aim   |   Z: Jump   |   X: Shoot   |   Tab: Switch Weapon   |   Shift: Dash";
            layout.setText(font, fallback);
            font.setColor(Color.LIGHT_GRAY);
            font.draw(batch, fallback, (screenW - layout.width) / 2f, centerY);
            return;
        }

        float iconSize = 24f;
        float keyGap = 3f;
        float labelGap = 7f;
        float groupGap = 22f;

        String[] labels = {"Move", "Jump", "Shoot", "Switch Weapon", "Dash"};
        float[] labelWidths = new float[labels.length];
        for (int i = 0; i < labels.length; i++) {
            layout.setText(font, labels[i]);
            labelWidths[i] = layout.width;
        }

        float moveGroupW = (iconSize * 4 + keyGap * 3) + labelGap + labelWidths[0];
        float zGroupW = iconSize + labelGap + labelWidths[1];
        float xGroupW = iconSize + labelGap + labelWidths[2];
        float tabGroupW = iconSize + labelGap + labelWidths[3];
        float shiftGroupW = iconSize + labelGap + labelWidths[4];

        float totalW = moveGroupW + zGroupW + xGroupW + tabGroupW + shiftGroupW + (groupGap * 4);
        float cursorX = (screenW - totalW) / 2f;
        float iconY = centerY - iconSize / 2f;

        font.setColor(Color.LIGHT_GRAY);

        TextureRegion[] arrows = {ui.keyLeft, ui.keyDown, ui.keyUp, ui.keyRight};
        for (int i = 0; i < arrows.length; i++) {
            batch.draw(arrows[i], cursorX, iconY, iconSize, iconSize);
            cursorX += iconSize + (i < arrows.length - 1 ? keyGap : labelGap);
        }
        cursorX = drawControlLabel(batch, font, labels[0], cursorX, centerY) + groupGap;

        cursorX = drawSingleKeyGroup(batch, font, ui.keyZ, labels[1], cursorX, iconY, centerY, iconSize, labelGap) + groupGap;
        cursorX = drawSingleKeyGroup(batch, font, ui.keyX, labels[2], cursorX, iconY, centerY, iconSize, labelGap) + groupGap;
        cursorX = drawSingleKeyGroup(batch, font, ui.keyTab, labels[3], cursorX, iconY, centerY, iconSize, labelGap) + groupGap;
        drawSingleKeyGroup(batch, font, ui.keyShift, labels[4], cursorX, iconY, centerY, iconSize, labelGap);
    }

    private float drawSingleKeyGroup(SpriteBatch batch, BitmapFont font, TextureRegion key, String label,
                                     float startX, float iconY, float centerY, float iconSize, float labelGap) {
        batch.draw(key, startX, iconY, iconSize, iconSize);
        return drawControlLabel(batch, font, label, startX + iconSize + labelGap, centerY);
    }

    private float drawControlLabel(SpriteBatch batch, BitmapFont font, String text, float x, float centerY) {
        layout.setText(font, text);
        font.draw(batch, text, x, centerY + layout.height / 2f);
        return x + layout.width;
    }

    @Override
    public void dispose() {
    }
}
