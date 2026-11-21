package com.kelompok8.frontend.entities.enemies.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

// Magic circle milik musuh. Turunan FireOrb: gerak, tabrakan, impact, pool semuanya sama,
// yang beda cuma ukuran hitbox dan gambarnya.
public class MagicCircle extends FireOrb {
    private static final float WIDTH = 36f;  // hitbox (elips tegak, lebih tinggi dari lebar)
    private static final float HEIGHT = 48f;

    public MagicCircle() {
        super(WIDTH, HEIGHT);
    }

    @Override
    public void render(ShapeRenderer shape) {
        if (!isActive()) return;

        // ntar ganti circle_fly
        shape.setColor(Color.ORANGE);
        shape.ellipse(getCenterX() - width / 2f, getCenterY() - height / 2f, width, height); // elips tegak
    }

    @Override
    public void impact() {
        //ntar tambahin circle_impact, ga ada animasi sementara
        super.impact();
    }
}
