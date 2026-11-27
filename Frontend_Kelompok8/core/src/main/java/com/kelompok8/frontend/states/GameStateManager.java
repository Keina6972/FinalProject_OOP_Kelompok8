package com.kelompok8.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kelompok8.frontend.utils.WorldConfig;

import java.util.Stack;

public class GameStateManager {
    private final Stack<GameState> states;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Vector2 mouseWorldPos = new Vector2();

    public GameStateManager() {
        this.states = new Stack<>();
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(WorldConfig.WORLD_WIDTH, WorldConfig.WORLD_HEIGHT, camera);
        this.viewport.apply(true);
    }

    public void push(GameState state) {
        states.push(state);
    }

    public void pop() {
        if (!states.isEmpty()) {
            states.pop().dispose();
        }
    }

    public void setState(GameState state) {
        if (!states.isEmpty()) {
            states.pop().dispose();
        }
        states.push(state);
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public Vector2 getMouseWorldPos() {
        mouseWorldPos.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouseWorldPos);
        return mouseWorldPos;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void update(float dt) {
        if (!states.isEmpty()) {
            states.peek().update(dt);
        }
    }

    public void render(SpriteBatch batch, ShapeRenderer shape) {
        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shape.setProjectionMatrix(camera.combined);

        if (!states.isEmpty()) {
            states.peek().render(batch, shape);
        }
    }

    public void dispose() {
        while (!states.isEmpty()) {
            states.pop().dispose();
        }
    }
}
