package com.kelompok8.frontend.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.states.PlayState;

public class Main extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private PlayState playState;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        playState = new PlayState();
    }

    @Override
    public void render() {
        // Clear background layar jadi abu-abu gelap
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update logika & render grafis
        playState.update();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        playState.render(shapeRenderer);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
