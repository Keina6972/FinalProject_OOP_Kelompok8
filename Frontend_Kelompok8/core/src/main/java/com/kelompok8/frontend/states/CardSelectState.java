package com.kelompok8.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.kelompok8.frontend.cards.BuffCard;
import com.kelompok8.frontend.cards.PlayerStats;

public class CardSelectState extends GameState {
    private static final int CARD_COUNT = 3;

    private final PlayerStats playerStats;
    private final BuffCard[] cardOptions = new BuffCard[CARD_COUNT];

    private final BitmapFont titleFont;
    private final BitmapFont rarityFont;
    private final BitmapFont statFont;
    private final BitmapFont valueFont;
    private final GlyphLayout layout;

    private final Rectangle[] cards = new Rectangle[CARD_COUNT];
    private int selectedCard = 1; // Default sorot kartu tengah

    public CardSelectState(GameStateManager gsm) {
        this(gsm, new PlayerStats());
    }

    public CardSelectState(GameStateManager gsm, PlayerStats playerStats) {
        super(gsm);
        this.playerStats = playerStats;

        this.titleFont = new BitmapFont();
        this.titleFont.getData().setScale(2.0f);

        this.rarityFont = new BitmapFont();
        this.rarityFont.getData().setScale(1.2f);

        this.statFont = new BitmapFont();
        this.statFont.getData().setScale(1.15f);

        this.valueFont = new BitmapFont();
        this.valueFont.getData().setScale(1.8f);

        this.layout = new GlyphLayout();

        // Hitung posisi 3 kartu agar simetris di tengah layar
        float cardWidth = 185f;
        float cardHeight = 250f;
        float gap = 35f;

        float totalWidth = (cardWidth * CARD_COUNT) + (gap * (CARD_COUNT - 1));
        float startX = (Gdx.graphics.getWidth() - totalWidth) / 2f;
        float startY = (Gdx.graphics.getHeight() - cardHeight) / 2f - 20f;

        for (int i = 0; i < CARD_COUNT; i++) {
            cards[i] = new Rectangle(startX + i * (cardWidth + gap), startY, cardWidth, cardHeight);
            cardOptions[i] = BuffCard.generateRandom();
        }
    }

    @Override
    public void update(float dt) {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Navigasi keyboard Kiri / Kanan
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            selectedCard = (selectedCard - 1 + CARD_COUNT) % CARD_COUNT;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            selectedCard = (selectedCard + 1) % CARD_COUNT;
        }

        // Hover mouse
        boolean hoveredAny = false;
        for (int i = 0; i < CARD_COUNT; i++) {
            if (cards[i].contains(mouseX, mouseY)) {
                selectedCard = i;
                hoveredAny = true;
            }
        }

        boolean clickedCard = hoveredAny && Gdx.input.justTouched();
        boolean confirmedKey = Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.Z);

        if (clickedCard || confirmedKey) {
            BuffCard chosen = cardOptions[selectedCard];
            playerStats.applyCard(chosen);
            gsm.setState(new PlayState(gsm, playerStats));
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        // 1. Gambar Badan Kartu sesuai Rarity
        shape.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < CARD_COUNT; i++) {
            Rectangle c = cards[i];
            BuffCard.Rarity rarity = cardOptions[i].getRarity();
            boolean isSelected = (i == selectedCard);
            float liftY = isSelected ? 14f : 0f;

            shape.setColor(isSelected ? rarity.selectedColor : rarity.baseColor);
            shape.rect(c.x, c.y + liftY, c.width, c.height);
        }
        shape.end();

        // 2. Gambar Bingkai / Border Kartu
        shape.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < CARD_COUNT; i++) {
            Rectangle c = cards[i];
            BuffCard.Rarity rarity = cardOptions[i].getRarity();
            boolean isSelected = (i == selectedCard);
            float liftY = isSelected ? 14f : 0f;

            shape.setColor(isSelected ? Color.GOLD : rarity.borderColor);
            shape.rect(c.x, c.y + liftY, c.width, c.height);
        }
        shape.end();

        // 3. Gambar Teks Rarity, Stat, dan Persentase Buff
        batch.begin();

        String header = "BOSS DEFEATED! CHOOSE A CARD";
        layout.setText(titleFont, header);
        titleFont.setColor(Color.GOLD);
        titleFont.draw(batch, header, (screenW - layout.width) / 2f, screenH * 0.85f);

        for (int i = 0; i < CARD_COUNT; i++) {
            Rectangle c = cards[i];
            BuffCard card = cardOptions[i];
            boolean isSelected = (i == selectedCard);
            float liftY = isSelected ? 14f : 0f;

            // Label Rarity di bagian atas kartu
            String rarityText = card.getRarity().label;
            layout.setText(rarityFont, rarityText);
            rarityFont.setColor(isSelected ? Color.GOLD : Color.WHITE);
            rarityFont.draw(
                batch,
                rarityText,
                c.x + (c.width - layout.width) / 2f,
                c.y + liftY + c.height - 28f
            );

            // Nilai Persentase di tengah kartu
            String percentText = "+" + card.getBonusPercent() + "%";
            layout.setText(valueFont, percentText);
            valueFont.setColor(Color.WHITE);
            valueFont.draw(
                batch,
                percentText,
                c.x + (c.width - layout.width) / 2f,
                c.y + liftY + (c.height * 0.56f)
            );

            // Nama Stat di bagian bawah kartu
            String statText = card.getStatType().displayName;
            layout.setText(statFont, statText);
            statFont.setColor(Color.LIGHT_GRAY);
            statFont.draw(
                batch,
                statText,
                c.x + (c.width - layout.width) / 2f,
                c.y + liftY + (c.height * 0.34f)
            );
        }

        batch.end();
    }

    @Override
    public void dispose() {
        titleFont.dispose();
        rarityFont.dispose();
        statFont.dispose();
        valueFont.dispose();
    }
}
