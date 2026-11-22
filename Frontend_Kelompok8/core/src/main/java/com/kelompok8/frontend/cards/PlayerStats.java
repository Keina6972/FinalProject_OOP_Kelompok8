package com.kelompok8.frontend.cards;

public class PlayerStats {
    private final float maxHp = 100f;
    private float currentHp = 100f;

    private float damageMultiplier = 1.0f;
    private float attackSpeedMultiplier = 1.0f;
    private float moveSpeedMultiplier = 1.0f;

    public void applyCard(BuffCard card) {
        if (card == null) return;

        float bonus = card.getBonusMultiplier();
        switch (card.getStatType()) {
            case DAMAGE:
                damageMultiplier += bonus;
                break;
            case ATTACK_SPEED:
                attackSpeedMultiplier += bonus;
                break;
            case MOVE_SPEED:
                moveSpeedMultiplier += bonus;
                break;
        }

        System.out.println(
            "[BUFF] " + card.getRarity().label + " +" + card.getBonusPercent() + "% " + card.getStatType().displayName
                + " | DMG: x" + String.format("%.2f", damageMultiplier)
                + " | ASPD: x" + String.format("%.2f", attackSpeedMultiplier)
                + " | MSPD: x" + String.format("%.2f", moveSpeedMultiplier)
        );
    }

    // faisal : menambah getter dan setter agar hp disimpan dan di bawa ke stage berikutnya tanpa di reset kembali ke 100
    public float getMaxHp() {
        return maxHp;
    }

    public float getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(float currentHp) {
        this.currentHp = Math.max(0f, Math.min(currentHp, maxHp));
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public float getAttackSpeedMultiplier() {
        return attackSpeedMultiplier;
    }

    public float getMoveSpeedMultiplier() {
        return moveSpeedMultiplier;
    }
}
