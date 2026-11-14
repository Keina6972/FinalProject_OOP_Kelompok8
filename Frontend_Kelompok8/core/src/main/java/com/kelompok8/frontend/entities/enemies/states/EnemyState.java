package com.kelompok8.frontend.entities.enemies.states;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kelompok8.frontend.entities.enemies.MiniBossA;

// rizki
// State Pattern: tiap kondisi MiniBossA (diam, menyerang, dll) jadi class sendiri.
// MiniBossA = context, cuma meneruskan update/render ke state yang aktif.
// Nanti kondisi baru (RiseState, HitState, DeadState) tinggal tambah class.
public interface EnemyState {
    void enter(MiniBossA owner);              // dipanggil sekali saat masuk state
    void update(MiniBossA owner, float dt);   // dipanggil tiap frame
    void render(MiniBossA owner, ShapeRenderer shape);
}
