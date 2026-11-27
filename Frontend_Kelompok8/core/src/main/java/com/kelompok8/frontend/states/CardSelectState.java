package com.kelompok8.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kelompok8.frontend.cards.BuffCard;
import com.kelompok8.frontend.cards.PlayerStats;
import com.kelompok8.frontend.utils.WorldConfig;

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
    private int selectedCard = 1;

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

        float cardWidth = 185f;
        float cardHeight = 250f;
        float gap = 35f;

        float totalWidth = (cardWidth * CARD_COUNT) + (gap * (CARD_COUNT - 1));
        float startX = (WorldConfig.WORLD_WIDTH - totalWidth) / 2f;
        float startY = (WorldConfig.WORLD_HEIGHT - cardHeight) / 2f - 20f;

        for (int i = 0; i < CARD_COUNT; i++) {
            cards[i] = new Rectangle(startX + i * (cardWidth + gap), startY, cardWidth, cardHeight);
            cardOptions[i] = BuffCard.generateRandom();
        }
    }

    @Override
    public void update(float dt) {
        Vector2 mouse = gsm.getMouseWorldPos();

        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            selectedCard = (selectedCard - 1 + CARD_COUNT) % CARD_COUNT;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            selectedCard = (selectedCard + 1) % CARD_COUNT;
        }

        boolean hoveredAny = false;
        for (int i = 0; i < CARD_COUNT; i++) {
            if (cards[i].contains(mouse.x, mouse.y)) {
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
        float screenW = WorldConfig.WORLD_WIDTH;
        float screenH = WorldConfig.WORLD_HEIGHT;

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

            String rarityText = card.getRarity().label;
            layout.setText(rarityFont, rarityText);
            rarityFont.setColor(isSelected ? Color.GOLD : Color.WHITE);
            rarityFont.draw(
                batch,
                rarityText,
                c.x + (c.width - layout.width) / 2f,
                c.y + liftY + c.height - 28f
            );

            String percentText = "+" + card.getBonusPercent() + "%";
            layout.setText(valueFont, percentText);
            valueFont.setColor(Color.WHITE);
            valueFont.draw(
                batch,
                percentText,
                c.x + (c.width - layout.width) / 2f,
                c.y + liftY + (c.height * 0.56f)
            );

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
