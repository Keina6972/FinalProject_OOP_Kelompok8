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

public class GameOverState extends GameState {
    private final GlyphLayout layout;
    private final Rectangle retryButton;
    private final Rectangle menuButton;
    private int selectedOption = 0;
    private boolean wasHoveredRetry = false;
    private boolean wasHoveredMenu = false;

    public GameOverState(GameStateManager gsm) {
        super(gsm);
        this.layout = new GlyphLayout();

        float btnWidth = 240f;
        float btnHeight = 58f;
        float centerX = (WorldConfig.WORLD_WIDTH - btnWidth) / 2f;

        this.retryButton = new Rectangle(centerX, WorldConfig.WORLD_HEIGHT * 0.45f, btnWidth, btnHeight);
        this.menuButton = new Rectangle(centerX, WorldConfig.WORLD_HEIGHT * 0.31f, btnWidth, btnHeight);

        AudioManager.getInstance().playGameOverBgm();
    }

    @Override
    public void update(float dt) {
        Vector2 mouse = gsm.getMouseWorldPos();

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOption = (selectedOption == 0) ? 1 : 0;
            AudioManager.getInstance().play(SoundType.UI_HOVER);
        }

        boolean hoveredRetry = retryButton.contains(mouse.x, mouse.y);
        boolean hoveredMenu = menuButton.contains(mouse.x, mouse.y);

        if (hoveredRetry && !wasHoveredRetry) {
            selectedOption = 0;
            AudioManager.getInstance().play(SoundType.UI_HOVER);
        } else if (hoveredMenu && !wasHoveredMenu) {
            selectedOption = 1;
            AudioManager.getInstance().play(SoundType.UI_HOVER);
        }

        wasHoveredRetry = hoveredRetry;
        wasHoveredMenu = hoveredMenu;

        boolean clicked = (hoveredRetry || hoveredMenu) && Gdx.input.justTouched();
        boolean confirmed = Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.Z);

        if (clicked || confirmed) {
            AudioManager.getInstance().play(SoundType.UI_CLICK);
            if (selectedOption == 0) {
                AudioManager.getInstance().stopBgm();
                gsm.setState(new PlayState(gsm));
            } else {
                gsm.setState(new MenuState(gsm));
            }
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
            shape.setColor(selectedOption == 0 ? Color.GOLD : Color.DARK_GRAY);
            shape.rect(retryButton.x, retryButton.y, retryButton.width, retryButton.height);

            shape.setColor(selectedOption == 1 ? Color.LIGHT_GRAY : Color.DARK_GRAY);
            shape.rect(menuButton.x, menuButton.y, menuButton.width, menuButton.height);
            shape.end();
        }

        batch.begin();

        if (ui.gameOverBg != null) {
            batch.draw(ui.gameOverBg, 0, 0, screenW, screenH);
        }

        BitmapFont titleFont = fonts.titleFont;
        String title = "GAME OVER";
        layout.setText(titleFont, title);
        titleFont.setColor(Color.RED);
        titleFont.draw(batch, title, (screenW - layout.width) / 2f, screenH * 0.75f);

        drawOptionButton(batch, fonts.buttonFont, ui, "RETRY", retryButton, selectedOption == 0);
        drawOptionButton(batch, fonts.buttonFont, ui, "MAIN MENU", menuButton, selectedOption == 1);

        drawHintBar(batch, fonts.smallFont, ui, screenW, screenH * 0.14f);

        batch.end();
    }

    private void drawOptionButton(SpriteBatch batch, BitmapFont font, UIAssets ui,
                                  String text, Rectangle rect, boolean isSelected) {
        NinePatch patch = isSelected ? ui.btnHoverPatch : ui.btnNormalPatch;
        if (patch != null) {
            patch.draw(batch, rect.x, rect.y, rect.width, rect.height);
        }

        layout.setText(font, text);
        font.setColor(isSelected ? Color.GOLD : Color.WHITE);
        float textX = rect.x + (rect.width - layout.width) / 2f;
        float textY = rect.y + (rect.height + layout.height) / 2f - 2f;
        font.draw(batch, text, textX, textY);
    }

    private void drawHintBar(SpriteBatch batch, BitmapFont font, UIAssets ui, float screenW, float centerY) {
        if (ui.keyUp == null || ui.keyDown == null || ui.keyZ == null) return;

        float iconSize = 22f;
        float keyGap = 3f;
        float labelGap = 7f;
        float groupGap = 24f;

        String selectLabel = "Select";
        String confirmLabel = "Confirm";

        layout.setText(font, selectLabel);
        float selectW = layout.width;
        layout.setText(font, confirmLabel);
        float confirmW = layout.width;

        float totalW = (iconSize * 2 + keyGap + labelGap + selectW)
            + groupGap
            + (iconSize + labelGap + confirmW);

        float cursorX = (screenW - totalW) / 2f;
        float iconY = centerY - iconSize / 2f;
        font.setColor(Color.LIGHT_GRAY);

        TextureRegion[] navKeys = {ui.keyUp, ui.keyDown};
        for (int i = 0; i < navKeys.length; i++) {
            batch.draw(navKeys[i], cursorX, iconY, iconSize, iconSize);
            cursorX += iconSize + (i == 0 ? keyGap : labelGap);
        }
        layout.setText(font, selectLabel);
        font.draw(batch, selectLabel, cursorX, centerY + layout.height / 2f);
        cursorX += selectW + groupGap;

        batch.draw(ui.keyZ, cursorX, iconY, iconSize, iconSize);
        cursorX += iconSize + labelGap;
        layout.setText(font, confirmLabel);
        font.draw(batch, confirmLabel, cursorX, centerY + layout.height / 2f);
    }

    @Override
    public void dispose() {
    }
}
