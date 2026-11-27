package com.kelompok8.frontend.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.audio.AudioManager;
import com.kelompok8.frontend.states.GameStateManager;
import com.kelompok8.frontend.states.MenuState;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private GameStateManager gsm;

    @Override
    public void create() {
        AudioManager.getInstance().loadAll();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        gsm = new GameStateManager();
        gsm.setState(new MenuState(gsm)); // Mulai dari Main Menu
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gsm.update(dt);
        gsm.render(batch, shapeRenderer);
    }

    @Override
    public void dispose() {
        AudioManager.getInstance().dispose();
        if (gsm != null) gsm.dispose();
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
