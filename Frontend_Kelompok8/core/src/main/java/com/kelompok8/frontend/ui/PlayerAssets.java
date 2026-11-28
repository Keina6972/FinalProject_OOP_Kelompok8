package com.kelompok8.frontend.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

public class PlayerAssets {
    private static PlayerAssets instance;
    private final Array<Texture> loadedTextures = new Array<>();

    public Animation<TextureRegion> idleAnim;
    public Animation<TextureRegion> walkAnim;
    public Animation<TextureRegion> jumpAirAnim;
    public Animation<TextureRegion> fallAirAnim;
    public Animation<TextureRegion> dashAnim;
    public Animation<TextureRegion> shootAnim;
    public Animation<TextureRegion> hurtAnim;
    public Animation<TextureRegion> deathAnim;
    public Animation<TextureRegion> bulletAnim;

    private PlayerAssets() {}

    public static PlayerAssets getInstance() {
        if (instance == null) {
            instance = new PlayerAssets();
        }
        return instance;
    }

    public void loadAll() {
        idleAnim = loadStrip("player/Idle.png", 6, 0.12f, Animation.PlayMode.LOOP);
        walkAnim = loadStrip("player/walk.png", 8, 0.08f, Animation.PlayMode.LOOP);
        dashAnim = loadStrip("player/dash.png", 8, 0.025f, Animation.PlayMode.LOOP);
        shootAnim = loadStrip("player/TongkatTembak.png", 8, 0.04f, Animation.PlayMode.NORMAL);
        hurtAnim = loadStrip("player/Hurt.png", 7, 0.06f, Animation.PlayMode.NORMAL);
        deathAnim = loadStrip("player/Death.png", 8, 0.10f, Animation.PlayMode.NORMAL);
        bulletAnim = loadStrip("player/BulletTongkatTembak.png", 8, 0.05f, Animation.PlayMode.LOOP);

        TextureRegion[] jumpFrames = loadFrames("player/Jump.png", 8);
        if (jumpFrames != null && jumpFrames.length >= 5) {
            jumpAirAnim = new Animation<>(0.09f, jumpFrames[2], jumpFrames[3], jumpFrames[4]);
            jumpAirAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        }

        TextureRegion[] fallFrames = loadFrames("player/Fall.png", 6);
        if (fallFrames != null && fallFrames.length >= 5) {
            fallAirAnim = new Animation<>(0.08f, fallFrames[0], fallFrames[1], fallFrames[2], fallFrames[3], fallFrames[4]);
            fallAirAnim.setPlayMode(Animation.PlayMode.NORMAL);
        }
    }

    private Animation<TextureRegion> loadStrip(String path, int frameCount, float frameDuration, Animation.PlayMode mode) {
        TextureRegion[] frames = loadFrames(path, frameCount);
        if (frames == null || frames.length == 0) return null;
        Animation<TextureRegion> anim = new Animation<>(frameDuration, frames);
        anim.setPlayMode(mode);
        return anim;
    }

    private TextureRegion[] loadFrames(String path, int expectedFrames) {
        if (!Gdx.files.internal(path).exists()) {
            Gdx.app.error("PlayerAssets", "Sprite tidak ditemukan: " + path);
            return null;
        }

        Texture tex = new Texture(Gdx.files.internal(path));
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        loadedTextures.add(tex);

        Pixmap pixmap = new Pixmap(Gdx.files.internal(path));
        int w = pixmap.getWidth();
        int h = pixmap.getHeight();

        boolean[] hasPixelCol = new boolean[w];
        int minY = h;
        int maxY = 0;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int alpha = pixmap.getPixel(x, y) & 0x000000ff;
                if (alpha > 15) {
                    hasPixelCol[x] = true;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }
        pixmap.dispose();

        // Kumpulkan rentang kolom X yang berisi gambar karakter
        List<int[]> intervals = new ArrayList<>();
        int start = -1;
        for (int x = 0; x < w; x++) {
            if (hasPixelCol[x]) {
                if (start == -1) start = x;
            } else if (start != -1) {
                intervals.add(new int[]{start, x - 1});
                start = -1;
            }
        }
        if (start != -1) {
            intervals.add(new int[]{start, w - 1});
        }

        // Gabungkan bagian terpisah dalam 1 frame (misal peluru yang lepas dari tongkat)
        while (intervals.size() > expectedFrames) {
            int bestIdx = 0;
            int smallestGap = Integer.MAX_VALUE;
            for (int i = 0; i < intervals.size() - 1; i++) {
                int gap = intervals.get(i + 1)[0] - intervals.get(i)[1];
                if (gap < smallestGap) {
                    smallestGap = gap;
                    bestIdx = i;
                }
            }
            int[] left = intervals.get(bestIdx);
            int[] right = intervals.get(bestIdx + 1);
            intervals.set(bestIdx, new int[]{left[0], right[1]});
            intervals.remove(bestIdx + 1);
        }

        if (intervals.isEmpty() || minY > maxY) {
            int frameW = w / expectedFrames;
            return TextureRegion.split(tex, frameW, h)[0];
        }

        int cropY = Math.max(0, minY - 2);
        int cropH = Math.min(h - cropY, (maxY - minY + 1) + 4);

        TextureRegion[] result = new TextureRegion[intervals.size()];
        for (int i = 0; i < intervals.size(); i++) {
            int[] bounds = intervals.get(i);
            int cropX = Math.max(0, bounds[0] - 2);
            int cropW = Math.min(w - cropX, (bounds[1] - bounds[0] + 1) + 4);
            result[i] = new TextureRegion(tex, cropX, cropY, cropW, cropH);
        }
        return result;
    }

    public boolean isLoaded() {
        return idleAnim != null;
    }

    public void dispose() {
        for (Texture tex : loadedTextures) {
            tex.dispose();
        }
        loadedTextures.clear();
    }
}
