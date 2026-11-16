package com.kelompok8.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class CardSelectState extends GameState {
    private static final int CARD_COUNT = 3;

    private final BitmapFont titleFont;
    private final BitmapFont cardTitleFont;
    private final BitmapFont cardDescFont;
    private final GlyphLayout layout;

    private final Rectangle[] cards = new Rectangle[CARD_COUNT];
    private int selectedCard = 1; // Default sorot kartu tengah

    public CardSelectState(GameStateManager gsm) {
        super(gsm);
        this.titleFont = new BitmapFont();
        this.titleFont.getData().setScale(2.0f);

        this.cardTitleFont = new BitmapFont();
        this.cardTitleFont.getData().setScale(1.3f);

        this.cardDescFont = new BitmapFont();
        this.cardDescFont.getData().setScale(1.0f);

        this.layout = new GlyphLayout();

        // Hitung posisi 3 kartu agar simetris di tengah layar
        float cardWidth = 170f;
        float cardHeight = 240f;
        float gap = 35f;

        float totalWidth = (cardWidth * CARD_COUNT) + (gap * (CARD_COUNT - 1));
        float startX = (Gdx.graphics.getWidth() - totalWidth) / 2f;
        float startY = (Gdx.graphics.getHeight() - cardHeight) / 2f - 20f;

        for (int i = 0; i < CARD_COUNT; i++) {
            cards[i] = new Rectangle(startX + i * (cardWidth + gap), startY, cardWidth, cardHeight);
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
            System.out.println("Kartu dipilih: Card " + (selectedCard + 1));
            // TODO: Terapkan efek buff kartu ke Player / lanjut ke stage berikutnya
            gsm.setState(new PlayState(gsm));
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        // 1. Gambar Badan Kartu (Isi)
        shape.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < CARD_COUNT; i++) {
            Rectangle c = cards[i];
            boolean isSelected = (i == selectedCard);
            float liftY = isSelected ? 12f : 0f; // Efek kartu terangkat saat dipilih

            shape.setColor(isSelected ? new Color(0.22f, 0.25f, 0.35f, 1f) : new Color(0.14f, 0.14f, 0.18f, 1f));
            shape.rect(c.x, c.y + liftY, c.width, c.height);
        }
        shape.end();

        // 2. Gambar Bingkai / Border Kartu
        shape.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < CARD_COUNT; i++) {
            Rectangle c = cards[i];
            boolean isSelected = (i == selectedCard);
            float liftY = isSelected ? 12f : 0f;

            shape.setColor(isSelected ? Color.GOLD : Color.LIGHT_GRAY);
            shape.rect(c.x, c.y + liftY, c.width, c.height);
        }
        shape.end();

        // 3. Gambar Teks Placeholder Kartu
        batch.begin();

        String header = "BOSS DEFEATED! CHOOSE A CARD";
        layout.setText(titleFont, header);
        titleFont.setColor(Color.GOLD);
        titleFont.draw(batch, header, (screenW - layout.width) / 2f, screenH * 0.84f);

        for (int i = 0; i < CARD_COUNT; i++) {
            Rectangle c = cards[i];
            boolean isSelected = (i == selectedCard);
            float liftY = isSelected ? 12f : 0f;

            String cardTitle = "CARD " + (i + 1);
            layout.setText(cardTitleFont, cardTitle);
            cardTitleFont.setColor(isSelected ? Color.GOLD : Color.WHITE);
            cardTitleFont.draw(
                batch,
                cardTitle,
                c.x + (c.width - layout.width) / 2f,
                c.y + liftY + c.height - 25f
            );

            String placeholder = "[ Empty Slot ]";
            layout.setText(cardDescFont, placeholder);
            cardDescFont.setColor(Color.GRAY);
            cardDescFont.draw(
                batch,
                placeholder,
                c.x + (c.width - layout.width) / 2f,
                c.y + liftY + (c.height / 2f)
            );
        }

        batch.end();
    }

    @Override
    public void dispose() {
        titleFont.dispose();
        cardTitleFont.dispose();
        cardDescFont.dispose();
    }
}
