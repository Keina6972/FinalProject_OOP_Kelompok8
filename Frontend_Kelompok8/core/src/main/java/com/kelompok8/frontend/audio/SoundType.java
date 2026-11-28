package com.kelompok8.frontend.audio;

public enum SoundType {
    // Player Audio
    PLAYER_JUMP("audio/sfx/player_jump.wav"),
    PLAYER_DASH("audio/sfx/player_dash.wav"),
    PLAYER_SHOOT_LINEAR("audio/sfx/player_shoot_linear.wav"),
    PLAYER_SHOOT_SPREAD("audio/sfx/player_shoot_spread.wav"),
    PLAYER_WEAPON_SWITCH("audio/sfx/player_weapon_switch.wav"),
    PLAYER_HURT("audio/sfx/player_hurt.wav"),
    PLAYER_DEATH("audio/sfx/player_death.wav"),

    // UI Audio
    UI_HOVER("audio/sfx/ui_hover.mp3"),
    UI_CLICK("audio/sfx/ui_click.mp3"),
    UI_CARD_DEAL("audio/sfx/ui_card_deal.mp3"),
    UI_CARD_SELECT("audio/sfx/ui_card_select.mp3"),
    UI_CARD_SLIDE("audio/sfx/ui_card_slide.mp3");

    private final String path;

    SoundType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
