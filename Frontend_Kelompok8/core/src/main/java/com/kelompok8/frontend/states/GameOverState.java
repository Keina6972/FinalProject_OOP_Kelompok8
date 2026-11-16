package com.kelompok8.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class GameOverState extends GameState {
    private final BitmapFont titleFont;
    private final BitmapFont buttonFont;
    private final GlyphLayout layout;

    private final Rectangle retryBounds;
    private final Rectangle menuBounds;
    private int selectedIndex = 0; // 0 = RETRY, 1 = MAIN MENU

    public GameOverState(GameStateManager gsm) {
        super(gsm);
        this.titleFont = new BitmapFont();
        this.titleFont.getData().setScale(2.5f);

        this.buttonFont = new BitmapFont();
        this.buttonFont.getData().setScale(1.4f);

        this.layout = new GlyphLayout();

        float btnW = 200f;
        float btnH = 52f;
        float centerX = (Gdx.graphics.getWidth() - btnW) / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        this.retryBounds = new Rectangle(centerX, centerY - 10f, btnW, btnH);
        this.menuBounds = new Rectangle(centerX, centerY - 80f, btnW, btnH);
    }

    @Override
    public void update(float dt) {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        if (retryBounds.contains(mouseX, mouseY)) selectedIndex = 0;
        if (menuBounds.contains(mouseX, mouseY)) selectedIndex = 1;

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex = (selectedIndex == 0) ? 1 : 0;
        }

        boolean mouseClicked = Gdx.input.justTouched()
            && (retryBounds.contains(mouseX, mouseY) || menuBounds.contains(mouseX, mouseY));
        boolean keyConfirmed = Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.Z);

        if (mouseClicked || keyConfirmed) {
            if (selectedIndex == 0) {
                gsm.setState(new PlayState(gsm));
            } else {
                gsm.setState(new MenuState(gsm));
            }
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        // 1. Render Tombol
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(selectedIndex == 0 ? Color.FIREBRICK : Color.DARK_GRAY);
        shape.rect(retryBounds.x, retryBounds.y, retryBounds.width, retryBounds.height);

        shape.setColor(selectedIndex == 1 ? Color.FIREBRICK : Color.DARK_GRAY);
        shape.rect(menuBounds.x, menuBounds.y, menuBounds.width, menuBounds.height);
        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.WHITE);
        shape.rect(retryBounds.x, retryBounds.y, retryBounds.width, retryBounds.height);
        shape.rect(menuBounds.x, menuBounds.y, menuBounds.width, menuBounds.height);
        shape.end();

        // 2. Render Teks
        batch.begin();

        String title = "YOU DIED";
        layout.setText(titleFont, title);
        titleFont.setColor(Color.SCARLET);
        titleFont.draw(batch, title, (screenW - layout.width) / 2f, screenH * 0.72f);

        drawButtonLabel(batch, "RETRY", retryBounds);
        drawButtonLabel(batch, "MAIN MENU", menuBounds);

        batch.end();
    }

    private void drawButtonLabel(SpriteBatch batch, String text, Rectangle bounds) {
        layout.setText(buttonFont, text);
        buttonFont.setColor(Color.WHITE);
        buttonFont.draw(
            batch,
            text,
            bounds.x + (bounds.width - layout.width) / 2f,
            bounds.y + (bounds.height + layout.height) / 2f
        );
    }

    @Override
    public void dispose() {
        titleFont.dispose();
        buttonFont.dispose();
    }
}
