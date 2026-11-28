package com.kelompok8.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kelompok8.frontend.audio.AudioManager;
import com.kelompok8.frontend.audio.SoundType;
import com.kelompok8.frontend.cards.BuffCard;
import com.kelompok8.frontend.cards.PlayerStats;
import com.kelompok8.frontend.ui.FontManager;
import com.kelompok8.frontend.ui.UIAssets;
import com.kelompok8.frontend.utils.WorldConfig;

public class CardSelectState extends GameState {
    private enum Phase {
        DEALING,
        CHOOSING,
        DISCARDING
    }

    private static final int CARD_COUNT = 3;
    private static final float DEAL_INTERVAL = 0.16f;
    private static final Color UNSELECTED_TINT = new Color(0.68f, 0.68f, 0.74f, 1f);

    private final PlayerStats playerStats;
    private final BuffCard[] cardOptions = new BuffCard[CARD_COUNT];
    private final GlyphLayout layout;

    private final Rectangle[] cards = new Rectangle[CARD_COUNT];
    private final float[] dealProgress = new float[CARD_COUNT];
    private final boolean[] discardStarted = new boolean[CARD_COUNT];
    private final float[] discardProgress = new float[CARD_COUNT];
    private final int[] discardOrder = new int[CARD_COUNT - 1];

    private Phase phase = Phase.DEALING;
    private float phaseTimer = 0f;
    private int revealedCount = 0;
    private int discardStep = 0;

    private int selectedCard = 1;
    private int prevHoveredIndex = -1;

    public CardSelectState(GameStateManager gsm) {
        this(gsm, new PlayerStats());
    }

    public CardSelectState(GameStateManager gsm, PlayerStats playerStats) {
        super(gsm);
        this.playerStats = playerStats;
        this.layout = new GlyphLayout();

        float cardWidth = 195f;
        float cardHeight = 260f;
        float gap = 32f;

        float totalWidth = (cardWidth * CARD_COUNT) + (gap * (CARD_COUNT - 1));
        float startX = (WorldConfig.WORLD_WIDTH - totalWidth) / 2f;
        float startY = (WorldConfig.WORLD_HEIGHT - cardHeight) / 2f - 10f;

        for (int i = 0; i < CARD_COUNT; i++) {
            cards[i] = new Rectangle(startX + i * (cardWidth + gap), startY, cardWidth, cardHeight);
            cardOptions[i] = BuffCard.generateRandom();
        }
    }

    @Override
    public void update(float dt) {
        for (int i = 0; i < CARD_COUNT; i++) {
            if (i < revealedCount && dealProgress[i] < 1f) {
                dealProgress[i] = Math.min(1f, dealProgress[i] + dt / 0.14f);
            }
            if (discardStarted[i] && discardProgress[i] < 1f) {
                discardProgress[i] = Math.min(1f, discardProgress[i] + dt / 0.22f);
            }
        }

        if (phase == Phase.DEALING) {
            phaseTimer += dt;
            while (revealedCount < CARD_COUNT && phaseTimer >= revealedCount * DEAL_INTERVAL) {
                AudioManager.getInstance().play(SoundType.UI_CARD_DEAL);
                revealedCount++;
            }
            if (revealedCount == CARD_COUNT && dealProgress[CARD_COUNT - 1] >= 1f) {
                phase = Phase.CHOOSING;
            }
            return;
        }

        if (phase == Phase.DISCARDING) {
            phaseTimer += dt;
            if (discardStep == 0 && phaseTimer >= 0.10f) {
                int idx = discardOrder[0];
                discardStarted[idx] = true;
                AudioManager.getInstance().play(SoundType.UI_CARD_SLIDE);
                discardStep = 1;
            } else if (discardStep == 1 && phaseTimer >= 0.26f) {
                int idx = discardOrder[1];
                discardStarted[idx] = true;
                AudioManager.getInstance().play(SoundType.UI_CARD_SLIDE);
                discardStep = 2;
            } else if (discardStep == 2 && phaseTimer >= 0.52f) {
                BuffCard chosen = cardOptions[selectedCard];
                playerStats.applyCard(chosen);
                gsm.setState(new PlayState(gsm, playerStats));
            }
            return;
        }

        Vector2 mouse = gsm.getMouseWorldPos();

        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            selectedCard = (selectedCard - 1 + CARD_COUNT) % CARD_COUNT;
            AudioManager.getInstance().play(SoundType.UI_HOVER);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            selectedCard = (selectedCard + 1) % CARD_COUNT;
            AudioManager.getInstance().play(SoundType.UI_HOVER);
        }

        int hoveredIndex = -1;
        for (int i = 0; i < CARD_COUNT; i++) {
            if (cards[i].contains(mouse.x, mouse.y)) {
                hoveredIndex = i;
                break;
            }
        }

        if (hoveredIndex != -1 && hoveredIndex != prevHoveredIndex) {
            selectedCard = hoveredIndex;
            AudioManager.getInstance().play(SoundType.UI_HOVER);
        }
        prevHoveredIndex = hoveredIndex;

        boolean clickedCard = (hoveredIndex != -1) && Gdx.input.justTouched();
        boolean confirmedKey = Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.Z);

        if (clickedCard || confirmedKey) {
            AudioManager.getInstance().play(SoundType.UI_CARD_SELECT);

            int d = 0;
            for (int i = 0; i < CARD_COUNT; i++) {
                if (i != selectedCard) {
                    discardOrder[d++] = i;
                }
            }

            phase = Phase.DISCARDING;
            phaseTimer = 0f;
            discardStep = 0;
        }
    }

    private float getCardRenderY(int index) {
        float baseY = cards[index].y;
        float enterOffset = -35f * (1f - dealProgress[index]);
        float liftY = (phase != Phase.DEALING && index == selectedCard) ? 16f : 0f;
        float exitOffset = -450f * discardProgress[index];
        return baseY + enterOffset + liftY + exitOffset;
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        float screenW = WorldConfig.WORLD_WIDTH;
        float screenH = WorldConfig.WORLD_HEIGHT;
        FontManager fonts = FontManager.getInstance();
        UIAssets ui = UIAssets.getInstance();

        if (ui.cardFramePatch == null) {
            shape.begin(ShapeRenderer.ShapeType.Filled);
            for (int i = 0; i < revealedCount; i++) {
                Rectangle c = cards[i];
                BuffCard.Rarity rarity = cardOptions[i].getRarity();
                boolean isSelected = (phase != Phase.DEALING && i == selectedCard);
                float cardY = getCardRenderY(i);

                shape.setColor(isSelected ? rarity.selectedColor : rarity.baseColor);
                shape.rect(c.x, cardY, c.width, c.height);
            }
            shape.end();
        }

        batch.begin();

        BitmapFont headerFont = fonts.headerFont;
        String header = "BOSS DEFEATED! CHOOSE A CARD";
        layout.setText(headerFont, header);
        headerFont.setColor(Color.GOLD);
        headerFont.draw(batch, header, (screenW - layout.width) / 2f, screenH * 0.86f);

        BitmapFont rarityFont = fonts.bodyFont;
        BitmapFont valueFont = fonts.headerFont;
        BitmapFont statFont = fonts.smallFont;

        for (int i = 0; i < revealedCount; i++) {
            Rectangle c = cards[i];
            BuffCard card = cardOptions[i];
            BuffCard.Rarity rarity = card.getRarity();
            boolean isSelected = (phase != Phase.DEALING && i == selectedCard);
            float cardY = getCardRenderY(i);

            if (ui.cardFramePatch != null) {
                batch.setColor(isSelected ? Color.WHITE : UNSELECTED_TINT);
                ui.cardFramePatch.draw(batch, c.x, cardY, c.width, c.height);
                batch.setColor(Color.WHITE);
            }

            String rarityText = rarity.label;
            layout.setText(rarityFont, rarityText);
            rarityFont.setColor(rarity.borderColor);
            rarityFont.draw(
                batch,
                rarityText,
                c.x + (c.width - layout.width) / 2f,
                cardY + c.height - 34f
            );

            String percentText = "+" + card.getBonusPercent() + "%";
            layout.setText(valueFont, percentText);
            valueFont.setColor(isSelected ? Color.GOLD : Color.WHITE);
            valueFont.draw(
                batch,
                percentText,
                c.x + (c.width - layout.width) / 2f,
                cardY + (c.height * 0.55f)
            );

            String statText = card.getStatType().displayName;
            layout.setText(statFont, statText);
            statFont.setColor(Color.LIGHT_GRAY);
            statFont.draw(
                batch,
                statText,
                c.x + (c.width - layout.width) / 2f,
                cardY + (c.height * 0.32f)
            );
        }

        if (phase == Phase.CHOOSING) {
            drawHintBar(batch, fonts.smallFont, ui, screenW, screenH * 0.12f);
        }

        batch.end();
    }

    private void drawHintBar(SpriteBatch batch, BitmapFont font, UIAssets ui, float screenW, float centerY) {
        if (ui.keyLeft == null || ui.keyRight == null || ui.keyZ == null) {
            String fallback = "Left / Right: Select   |   Z / Enter: Confirm";
            layout.setText(font, fallback);
            font.setColor(Color.LIGHT_GRAY);
            font.draw(batch, fallback, (screenW - layout.width) / 2f, centerY);
            return;
        }

        float iconSize = 22f;
        float keyGap = 3f;
        float labelGap = 7f;
        float groupGap = 24f;

        String selectLabel = "Select";
        String confirmLabel = "Confirm";

        layout.setText(font, selectLabel);
        float selectW = layout.width;
        layout.setText(font, confirmLabel);
        float confirmW = layout.width;

        float totalW = (iconSize * 2 + keyGap + labelGap + selectW)
            + groupGap
            + (iconSize + labelGap + confirmW);

        float cursorX = (screenW - totalW) / 2f;
        float iconY = centerY - iconSize / 2f;
        font.setColor(Color.LIGHT_GRAY);

        TextureRegion[] navKeys = {ui.keyLeft, ui.keyRight};
        for (int i = 0; i < navKeys.length; i++) {
            batch.draw(navKeys[i], cursorX, iconY, iconSize, iconSize);
            cursorX += iconSize + (i == 0 ? keyGap : labelGap);
        }
        layout.setText(font, selectLabel);
        font.draw(batch, selectLabel, cursorX, centerY + layout.height / 2f);
        cursorX += selectW + groupGap;

        batch.draw(ui.keyZ, cursorX, iconY, iconSize, iconSize);
        cursorX += iconSize + labelGap;
        layout.setText(font, confirmLabel);
        font.draw(batch, confirmLabel, cursorX, centerY + layout.height / 2f);
    }

    @Override
    public void dispose() {
    }
}
