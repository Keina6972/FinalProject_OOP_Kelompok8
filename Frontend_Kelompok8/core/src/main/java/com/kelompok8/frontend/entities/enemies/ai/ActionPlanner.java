package com.kelompok8.frontend.entities.enemies.ai;

import com.badlogic.gdx.math.MathUtils;
import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.entities.enemies.Enemy;
import com.kelompok8.frontend.entities.enemies.movements.BossMovement;
import com.kelompok8.frontend.entities.enemies.movements.MovementType;
import com.kelompok8.frontend.entities.enemies.movements.RangeMovementPlanner;

// Merencanakan jeda setelah serangan selesai: gerak apa di jeda itu, dan berapa lama jedanya.
// Jeda biasanya NORMAL_PAUSE dan gerakan terjadi di dalam jeda itu. Kalau lagi kombo (dash mundur), jedanya dipendekkan
// jadi serangan berikutnya langsung nyambung.
public class ActionPlanner {
    private static final float PILLAR_DASH_BACK_CHANCE = 0.7f;   // habis flame pillar -> 70% dash mundur
    private static final float NORMAL_PAUSE = 1.2f;              // jeda normal antar serangan (detik)
    private static final float COMBO_PAUSE = 0.3f;               // jeda saat kombo (detik)

    private final RangeMovementPlanner movementPlanner = new RangeMovementPlanner();

    public ActionPlan afterAttack(Enemy owner, Player target, AttackType lastAttack) {
        BossMovement movement = null;

        // kombo: habis pillar, kemungkinan tinggi dash ke belakang (kalau di belakang masih ada ruang)
        if (lastAttack == AttackType.PILLAR && MathUtils.random(0f, 1f) < PILLAR_DASH_BACK_CHANCE) {
            movement = movementPlanner.dashBackIfPossible(owner, target);
        }
        // selain itu: gerakan biasa berdasarkan jarak ke player (bisa juga null = diam)
        if (movement == null) movement = movementPlanner.next(owner, target);

        boolean combo = movement != null && movement.getType() == MovementType.DASH_BACKWARD;
        return new ActionPlan(movement, combo ? COMBO_PAUSE : NORMAL_PAUSE);
    }
}
