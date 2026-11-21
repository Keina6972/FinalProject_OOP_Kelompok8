package com.kelompok8.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class MenuState extends GameState {
    private final BitmapFont titleFont;
    private final BitmapFont buttonFont;
    private final BitmapFont infoFont;
    private final GlyphLayout layout;

    private final Rectangle playButtonBounds;
    private boolean isHovered = false;

    public MenuState(GameStateManager gsm) {
        super(gsm);
        this.titleFont = new BitmapFont();
        this.titleFont.getData().setScale(2.5f);

        this.buttonFont = new BitmapFont();
        this.buttonFont.getData().setScale(1.6f);

        this.infoFont = new BitmapFont();
        this.infoFont.getData().setScale(1.1f);

        this.layout = new GlyphLayout();

        float btnWidth = 220f;
        float btnHeight = 60f;
        float btnX = (Gdx.graphics.getWidth() - btnWidth) / 2f;
        float btnY = (Gdx.graphics.getHeight() - btnHeight) / 2f - 20f;
        this.playButtonBounds = new Rectangle(btnX, btnY, btnWidth, btnHeight);
    }

    @Override
    public void update(float dt) {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        isHovered = playButtonBounds.contains(mouseX, mouseY);

        // Klik tombol PLAY dengan mouse atau tekan ENTER / Z
        boolean clickedPlay = isHovered && Gdx.input.justTouched();
        boolean pressedStart = Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.Z);

        if (clickedPlay || pressedStart) {
            gsm.setState(new PlayState(gsm));
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        // 1. Render Tombol PLAY
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(isHovered ? Color.GOLD : Color.ROYAL);
        shape.rect(playButtonBounds.x, playButtonBounds.y, playButtonBounds.width, playButtonBounds.height);
        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.WHITE);
        shape.rect(playButtonBounds.x, playButtonBounds.y, playButtonBounds.width, playButtonBounds.height);
        shape.end();

        // 2. Render Teks Judul, Tombol, dan Kontrol
        batch.begin();

        String title = "LAST PENANCE";
        layout.setText(titleFont, title);
        titleFont.setColor(Color.WHITE);
        titleFont.draw(batch, title, (screenW - layout.width) / 2f, screenH * 0.72f);

        String playText = "PLAY";
        layout.setText(buttonFont, playText);
        buttonFont.setColor(isHovered ? Color.BLACK : Color.WHITE);
        buttonFont.draw(
            batch,
            playText,
            playButtonBounds.x + (playButtonBounds.width - layout.width) / 2f,
            playButtonBounds.y + (playButtonBounds.height + layout.height) / 2f
        );

        String controls = "Arrow Keys: Move / Aim   |   Z: Jump   |   X: Shoot   |   Tab: Change Attack";
        layout.setText(infoFont, controls);
        infoFont.setColor(Color.LIGHT_GRAY);
        infoFont.draw(batch, controls, (screenW - layout.width) / 2f, screenH * 0.22f);

        batch.end();
    }

    @Override
    public void dispose() {
        titleFont.dispose();
        buttonFont.dispose();
        infoFont.dispose();
    }
}
