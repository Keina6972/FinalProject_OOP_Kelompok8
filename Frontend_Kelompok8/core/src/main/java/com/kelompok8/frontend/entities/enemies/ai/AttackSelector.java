package com.kelompok8.frontend.entities.enemies.ai;

import com.badlogic.gdx.math.MathUtils;
import com.kelompok8.frontend.entities.enemies.attacks.BossAttack;
import com.kelompok8.frontend.entities.enemies.movements.MovementType;
import com.kelompok8.frontend.entities.enemies.movements.RangeMovementPlanner;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

// Memilih serangan berikutnya pakai peluang berbobot.
// Kondisi normal: tiap serangan punya bobot sama (1.0), jadi peluangnya sama.
// Aturan di multiplier() mengalikan bobot serangan tertentu (jarak player, gerakan sebelumnya / kombo).
// Serangan yang masih cooldown ga ikut dipilih (kalau semua cooldown, ambil yang paling cepat siap).
public class AttackSelector {
    // ---- bobot aturan (kelipatan dari bobot dasar 1.0), adjust aja ----
    private static final float NEAR_FIRE_ORB = 3f;               // player dekat -> fire orb lebih sering
    private static final float FAR_BEAM = 3f;                    // player jauh -> beam lebih sering
    private static final float AFTER_DASH_BACK_MAGIC_CIRCLE = 6f; // habis dash mundur -> magic circle sangat sering

    private static class Entry {
        final BossAttack attack;
        final float cooldown;
        float remaining = 0f;   // sisa cooldown (0 = siap dipakai)

        Entry(BossAttack attack, float cooldown) {
            this.attack = attack;
            this.cooldown = cooldown;
        }
    }

    private final Map<AttackType, Entry> entries = new EnumMap<>(AttackType.class);

    public void register(AttackType type, BossAttack attack, float cooldown) {
        entries.put(type, new Entry(attack, cooldown));
    }

    public BossAttack get(AttackType type) { return entries.get(type).attack; }

    // dipanggil tiap frame: kurangi semua cooldown
    public void update(float dt) {
        for (Entry e : entries.values()) e.remaining = Math.max(0f, e.remaining - dt);
    }

    // dipanggil saat serangan selesai: mulai cooldown-nya
    public void startCooldown(AttackType type) {
        Entry e = entries.get(type);
        e.remaining = e.cooldown;
    }

    public boolean isReady(AttackType type) { return entries.get(type).remaining <= 0f; }
    public float getCooldownRemaining(AttackType type) { return entries.get(type).remaining; }

    // gap = jarak tepi-ke-tepi boss ke player. lastAttack / lastMovement = yang barusan dipakai (boleh null)
    public AttackType choose(float gap, AttackType lastAttack, MovementType lastMovement) {
        Map<AttackType, Float> w = weights(gap, lastAttack, lastMovement);

        // semua lagi cooldown: ambil yang paling cepat siap
        if (w.isEmpty()) {
            AttackType best = null;
            for (Map.Entry<AttackType, Entry> en : entries.entrySet()) {
                if (best == null || en.getValue().remaining < entries.get(best).remaining) best = en.getKey();
            }
            return best;
        }

        float total = 0f;
        for (float v : w.values()) total += v;
        float roll = MathUtils.random(0f, total);
        AttackType last = null;
        for (Map.Entry<AttackType, Float> en : w.entrySet()) {
            last = en.getKey();
            roll -= en.getValue();
            if (roll <= 0f) return en.getKey();
        }
        return last;
    }

    // bobot tiap serangan yang siap (bukan cooldown). Dipisah dari choose() biar gampang dicek / di-debug
    public Map<AttackType, Float> weights(float gap, AttackType lastAttack, MovementType lastMovement) {
        Map<AttackType, Float> w = new LinkedHashMap<>();
        for (AttackType type : AttackType.values()) {
            if (entries.containsKey(type) && isReady(type)) w.put(type, multiplier(type, gap, lastAttack, lastMovement));
        }
        return w;
    }

    // semua aturan "peluang naik" ada di sini. Mau nambah aturan kombo serangan -> serangan, tinggal pakai lastAttack
    private float multiplier(AttackType type, float gap, AttackType lastAttack, MovementType lastMovement) {
        float m = 1f;
        if (type == AttackType.FIRE_ORB && gap < RangeMovementPlanner.CLOSE_GAP) m *= NEAR_FIRE_ORB;
        if (type == AttackType.BEAM && gap > RangeMovementPlanner.FAR_GAP) m *= FAR_BEAM;
        if (type == AttackType.MAGIC_CIRCLE && lastMovement == MovementType.DASH_BACKWARD) m *= AFTER_DASH_BACK_MAGIC_CIRCLE;
        return m;
    }
}
