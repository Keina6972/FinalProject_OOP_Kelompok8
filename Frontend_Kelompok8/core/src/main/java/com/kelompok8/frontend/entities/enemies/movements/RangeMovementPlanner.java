package com.kelompok8.frontend.entities.enemies.movements;

import com.badlogic.gdx.math.MathUtils;
import com.kelompok8.frontend.entities.Player;
import com.kelompok8.frontend.entities.enemies.Enemy;

// Aturan kapan boss jalan / dash, berdasarkan jarak tepi-ke-tepi (gap) ke player:
// jauh   (gap > FAR_GAP)    : jalan mendekat (60%) atau dash maju (40%)
// dekat  (gap < CLOSE_GAP)  : dash mundur (55%) atau jalan menjauh (45%), kalau di belakang boss masih ada ruang
// sedang                    : jalan mendekat 35%, jalan menjauh 35%, diam 30%
// Mengembalikan null kalau boss sebaiknya diam (langsung nyerang).
public class RangeMovementPlanner {
    private static final float FAR_GAP = 220f;
    private static final float CLOSE_GAP = 110f;
    private static final float MIN_GAP = 60f;       // boss ga akan gerak sendiri sampai lebih dekat dari ini
    private static final float MIN_ROOM_BACK = 60f; // butuh ruang segini di belakang boss buat mundur

    private static final float WALK_SPEED = 90f;
    private static final float WALK_TIME_MIN = 0.6f;
    private static final float WALK_TIME_MAX = 1.2f;

    private static final float DASH_FORWARD_DISTANCE = 150f;
    private static final float DASH_BACKWARD_DISTANCE = 130f;
    private static final float DASH_WINDUP = 0.2f;
    private static final float DASH_TIME = 0.22f;
    private static final float DASH_RECOVERY = 0.2f;

    public BossMovement next(Enemy owner, Player target) {
        float gap = MovementMath.gap(owner, target);
        float awayDir = -MovementMath.dirToTarget(owner, target);
        boolean roomBack = MovementMath.room(owner, awayDir) >= MIN_ROOM_BACK;
        float roll = MathUtils.random(0f, 1f);

        if (gap > FAR_GAP) {
            return roll < 0.6f ? walk(true) : dash(DashMovement.Direction.FORWARD);
        }
        if (gap < CLOSE_GAP) {
            if (!roomBack) return null;   // kepepet dinding, ga bisa mundur
            return roll < 0.55f ? dash(DashMovement.Direction.BACKWARD) : walk(false);
        }
        if (roll < 0.35f) return walk(true);
        if (roll < 0.70f && roomBack) return walk(false);
        return null;
    }

    private BossMovement walk(boolean toward) {
        return new WalkMovement(toward, MathUtils.random(WALK_TIME_MIN, WALK_TIME_MAX), WALK_SPEED, MIN_GAP);
    }

    private BossMovement dash(DashMovement.Direction direction) {
        float distance = direction == DashMovement.Direction.FORWARD ? DASH_FORWARD_DISTANCE : DASH_BACKWARD_DISTANCE;
        return new DashMovement(direction, distance, DASH_WINDUP, DASH_TIME, DASH_RECOVERY, MIN_GAP);
    }
}
