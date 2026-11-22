package com.kelompok8.frontend.entities.enemies.ai;

import com.kelompok8.frontend.entities.enemies.movements.BossMovement;

// Rencana jeda setelah serangan selesai: gerakan apa (boleh null = diam) dan berapa lama jedanya
public final class ActionPlan {
    public final BossMovement movement;   // null kalau boss diam aja di jeda
    public final float pause;             // total jeda sampai serangan berikutnya (gerakan dihitung di dalam jeda ini)

    public ActionPlan(BossMovement movement, float pause) {
        this.movement = movement;
        this.pause = pause;
    }
}
