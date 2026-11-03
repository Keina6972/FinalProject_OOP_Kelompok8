package com.kelompok8.frontend.utils;

import com.kelompok8.frontend.entities.Entity;

public class CollisionDetector {
    public static boolean checkCollision(Entity a, Entity b) {
        if (a == null || b == null) return false;
        return a.getBounds().overlaps(b.getBounds());
    }
}
