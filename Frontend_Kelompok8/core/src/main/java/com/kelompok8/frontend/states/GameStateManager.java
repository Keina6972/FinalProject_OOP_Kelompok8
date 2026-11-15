package com.kelompok8.frontend.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Stack;

public class GameStateManager {
    private final Stack<GameState> states = new Stack<>();

    public void pushState(GameState state) {
        states.push(state);
    }

    public void popState() {
        if (!states.isEmpty()) {
            states.pop().dispose();
        }
    }

    public void setState(GameState state) {
        while (!states.isEmpty()) {
            states.pop().dispose();
        }
        states.push(state);
    }

    public void update(float dt) {
        if (!states.isEmpty()) {
            states.peek().update(dt);
        }
    }

    public void render(SpriteBatch batch, ShapeRenderer shape) {
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
