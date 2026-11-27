package com.kelompok8.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kelompok8.frontend.utils.WorldConfig;

public class GameOverState extends GameState {
    private final BitmapFont titleFont;
    private final BitmapFont buttonFont;
    private final GlyphLayout layout;

    private final Rectangle retryButton;
    private final Rectangle menuButton;
    private int selectedOption = 0;

    public GameOverState(GameStateManager gsm) {
        super(gsm);
        this.titleFont = new BitmapFont();
        this.titleFont.getData().setScale(2.6f);

        this.buttonFont = new BitmapFont();
        this.buttonFont.getData().setScale(1.6f);

        this.layout = new GlyphLayout();

        float btnWidth = 220f;
        float btnHeight = 52f;
        float centerX = (WorldConfig.WORLD_WIDTH - btnWidth) / 2f;

        this.retryButton = new Rectangle(centerX, WorldConfig.WORLD_HEIGHT * 0.45f, btnWidth, btnHeight);
        this.menuButton = new Rectangle(centerX, WorldConfig.WORLD_HEIGHT * 0.32f, btnWidth, btnHeight);
    }

    @Override
    public void update(float dt) {
        Vector2 mouse = gsm.getMouseWorldPos();

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOption = (selectedOption == 0) ? 1 : 0;
        }

        boolean hoveredRetry = retryButton.contains(mouse.x, mouse.y);
        boolean hoveredMenu = menuButton.contains(mouse.x, mouse.y);

        if (hoveredRetry) selectedOption = 0;
        if (hoveredMenu) selectedOption = 1;

        boolean clicked = (hoveredRetry || hoveredMenu) && Gdx.input.justTouched();
        boolean confirmed = Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.Z);

        if (clicked || confirmed) {
            if (selectedOption == 0) {
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

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(selectedOption == 0 ? Color.GOLD : Color.DARK_GRAY);
        shape.rect(retryButton.x, retryButton.y, retryButton.width, retryButton.height);

        shape.setColor(selectedOption == 1 ? Color.LIGHT_GRAY : Color.DARK_GRAY);
        shape.rect(menuButton.x, menuButton.y, menuButton.width, menuButton.height);
        shape.end();

        batch.begin();

        String title = "GAME OVER";
        layout.setText(titleFont, title);
        titleFont.setColor(Color.RED);
        titleFont.draw(batch, title, (screenW - layout.width) / 2f, screenH * 0.75f);

        drawButtonText(batch, "RETRY", retryButton, selectedOption == 0 ? Color.BLACK : Color.WHITE);
        drawButtonText(batch, "MAIN MENU", menuButton, selectedOption == 1 ? Color.BLACK : Color.WHITE);

        batch.end();
    }

    private void drawButtonText(SpriteBatch batch, String text, Rectangle rect, Color color) {
        layout.setText(buttonFont, text);
        buttonFont.setColor(color);
        float textX = rect.x + (rect.width - layout.width) / 2f;
        float textY = rect.y + (rect.height + layout.height) / 2f;
        buttonFont.draw(batch, text, textX, textY);
    }

    @Override
    public void dispose() {
        titleFont.dispose();
        buttonFont.dispose();
    }
}
