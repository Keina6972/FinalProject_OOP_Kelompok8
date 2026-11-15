package com.kelompok8.frontend.utils;

import com.badlogic.gdx.math.Rectangle;
import com.kelompok8.frontend.entities.Entity;

public class CollisionDetector {
    public static boolean checkCollision(Entity a, Entity b) {
        if (a == null || b == null || !a.isActive() || !b.isActive()) return false;
        return a.getBounds().overlaps(b.getBounds());
    }

    public static boolean checkCollision(Entity entity, Rectangle area) {
        if (entity == null || area == null || !entity.isActive()) return false;
        return entity.getBounds().overlaps(area);
    }
}
