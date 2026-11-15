package com.kelompok8.frontend.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class GameState {
    protected final GameStateManager gsm;

    protected GameState(GameStateManager gsm) {
        this.gsm = gsm;
    }

    public abstract void update(float dt);
    public abstract void render(SpriteBatch batch, ShapeRenderer shape);
    public abstract void dispose();
}
