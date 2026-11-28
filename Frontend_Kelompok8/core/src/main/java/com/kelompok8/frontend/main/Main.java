package com.kelompok8.frontend.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kelompok8.frontend.audio.AudioManager;
import com.kelompok8.frontend.states.GameStateManager;
import com.kelompok8.frontend.states.MenuState;
import com.kelompok8.frontend.ui.FontManager;
import com.kelompok8.frontend.ui.UIAssets;
import com.kelompok8.frontend.utils.WorldConfig;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private ShapeRenderer shape;
    private GameStateManager gsm;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shape = new ShapeRenderer();
        AudioManager.getInstance().loadAll();
        FontManager.getInstance().loadAll();
        UIAssets.getInstance().loadAll();
        gsm = new GameStateManager();
        gsm.push(new MenuState(gsm));
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            if (Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setWindowedMode((int) WorldConfig.WORLD_WIDTH, (int) WorldConfig.WORLD_HEIGHT);
            } else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
        }

        ScreenUtils.clear(0.08f, 0.08f, 0.1f, 1f);
        float dt = Gdx.graphics.getDeltaTime();
        gsm.update(dt);
        gsm.render(batch, shape);
    }

    @Override
    public void resize(int width, int height) {
        gsm.resize(width, height);
    }

    @Override
    public void dispose() {
        gsm.dispose();
        UIAssets.getInstance().dispose();
        FontManager.getInstance().dispose();
        AudioManager.getInstance().dispose();
        batch.dispose();
        shape.dispose();
    }
}
