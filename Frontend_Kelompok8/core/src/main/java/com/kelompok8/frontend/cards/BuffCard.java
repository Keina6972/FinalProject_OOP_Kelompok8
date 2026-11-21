package com.kelompok8.frontend.cards;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class BuffCard {

    public enum Rarity {
        FIEND(
            "FIEND",
            3, 5,
            new Color(0.28f, 0.28f, 0.30f, 1f),
            new Color(0.36f, 0.36f, 0.39f, 1f),
            Color.LIGHT_GRAY
        ),
        WRAITH(
            "WRAITH",
            6, 14,
            new Color(0.38f, 0.24f, 0.26f, 1f),
            new Color(0.48f, 0.28f, 0.31f, 1f),
            new Color(0.85f, 0.35f, 0.40f, 1f)
        ),
        ABYSSAL(
            "ABYSSAL",
            15, 25,
            new Color(0.65f, 0.08f, 0.12f, 1f),
            new Color(0.82f, 0.12f, 0.16f, 1f),
            Color.GOLD
        );

        public final String label;
        public final int minPercent;
        public final int maxPercent;
        public final Color baseColor;
        public final Color selectedColor;
        public final Color borderColor;

        Rarity(String label, int minPercent, int maxPercent, Color baseColor, Color selectedColor, Color borderColor) {
            this.label = label;
            this.minPercent = minPercent;
            this.maxPercent = maxPercent;
            this.baseColor = baseColor;
            this.selectedColor = selectedColor;
            this.borderColor = borderColor;
        }
    }

    public enum StatType {
        DAMAGE("Damage"),
        ATTACK_SPEED("Attack Speed"),
        MOVE_SPEED("Move Speed");

        public final String displayName;

        StatType(String displayName) {
            this.displayName = displayName;
        }
    }

    private final Rarity rarity;
    private final StatType statType;
    private final int bonusPercent;

    public BuffCard(Rarity rarity, StatType statType, int bonusPercent) {
        this.rarity = rarity;
        this.statType = statType;
        this.bonusPercent = bonusPercent;
    }

    public static BuffCard generateRandom() {
        // Drop rate: Fiend 60%, Wraith 30%, Abyssal 10%
        float roll = MathUtils.random(100f);
        Rarity rarity;
        if (roll < 60f) {
            rarity = Rarity.FIEND;
        } else if (roll < 90f) {
            rarity = Rarity.WRAITH;
        } else {
            rarity = Rarity.ABYSSAL;
        }

        // Pilih 1 dari 3 stat secara acak
        StatType[] stats = StatType.values();
        StatType chosenStat = stats[MathUtils.random(stats.length - 1)];

        // Acak persentase buff sesuai range rarity
        int percent = MathUtils.random(rarity.minPercent, rarity.maxPercent);

        return new BuffCard(rarity, chosenStat, percent);
    }

    public Rarity getRarity() {
        return rarity;
    }

    public StatType getStatType() {
        return statType;
    }

    public int getBonusPercent() {
        return bonusPercent;
    }

    public float getBonusMultiplier() {
        return bonusPercent / 100f;
    }
}
