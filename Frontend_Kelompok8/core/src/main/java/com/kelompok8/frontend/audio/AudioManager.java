package com.kelompok8.frontend.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;

import java.util.EnumMap;
import java.util.Map;

public class AudioManager {
    private static AudioManager instance;

    private final Map<SoundType, Sound> sounds;
    private Music currentBgm;
    private String currentBgmPath;

    private float volume = 1.0f;
    private float bgmVolume = 0.5f;
    private boolean muted = false;

    private AudioManager() {
        this.sounds = new EnumMap<>(SoundType.class);
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void loadAll() {
        for (SoundType type : SoundType.values()) {
            if (sounds.containsKey(type)) continue;

            FileHandle file = Gdx.files.internal(type.getPath());
            if (file.exists()) {
                sounds.put(type, Gdx.audio.newSound(file));
            } else {
                Gdx.app.log("AudioManager", "File audio belum ditemukan: " + type.getPath());
            }
        }
    }

    public void play(SoundType type) {
        play(type, 1.0f);
    }

    public void play(SoundType type, float volumeScale) {
        if (muted || type == null) return;
        Sound sound = sounds.get(type);
        if (sound != null) {
            float finalVolume = MathUtils.clamp(volume * volumeScale, 0f, 1f);
            sound.play(finalVolume);
        }
    }

    public void playMenuBgm() {
        playFirstAvailableBgm(
            true,
            "audio/main_menu_bgm.mp3",
            "audio/main_menu_bgm.ogg",
            "audio/main_menu_bgm.wav",
            "audio/bgm_menu.mp3",
            "audio/bgm_menu.ogg"
        );
    }

    public void playGameOverBgm() {
        playFirstAvailableBgm(
            true,
            "audio/game_over.mp3",
            "audio/game_over.ogg",
            "audio/game_over.wav",
            "audio/bgm_gameover.mp3",
            "audio/bgm_gameover.ogg"
        );
    }

    private void playFirstAvailableBgm(boolean looping, String... paths) {
        for (String path : paths) {
            if (Gdx.files.internal(path).exists()) {
                playBgm(path, looping);
                return;
            }
        }
        Gdx.app.log("AudioManager", "File BGM belum ditemukan: " + paths[0]);
    }

    public void playBgm(String path, boolean looping) {
        if (currentBgm != null && path.equals(currentBgmPath) && currentBgm.isPlaying()) {
            return;
        }
        stopBgm();
        FileHandle file = Gdx.files.internal(path);
        if (!file.exists()) {
            Gdx.app.log("AudioManager", "File BGM belum ditemukan: " + path);
            return;
        }
        currentBgmPath = path;
        currentBgm = Gdx.audio.newMusic(file);
        currentBgm.setLooping(looping);
        currentBgm.setVolume(muted ? 0f : bgmVolume);
        currentBgm.play();
    }

    public void stopBgm() {
        if (currentBgm != null) {
            currentBgm.stop();
            currentBgm.dispose();
            currentBgm = null;
        }
        currentBgmPath = null;
    }

    public void setVolume(float volume) {
        this.volume = MathUtils.clamp(volume, 0f, 1f);
    }

    public float getVolume() {
        return volume;
    }

    public void setBgmVolume(float volume) {
        this.bgmVolume = MathUtils.clamp(volume, 0f, 1f);
        if (currentBgm != null && !muted) {
            currentBgm.setVolume(this.bgmVolume);
        }
    }

    public float getBgmVolume() {
        return bgmVolume;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
        if (currentBgm != null) {
            currentBgm.setVolume(muted ? 0f : bgmVolume);
        }
    }

    public boolean isMuted() {
        return muted;
    }

    public void dispose() {
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
        sounds.clear();
        stopBgm();
    }
}
