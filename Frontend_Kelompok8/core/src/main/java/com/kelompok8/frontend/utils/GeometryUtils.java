package com.kelompok8.frontend.utils;

import com.badlogic.gdx.math.Rectangle;

// Hitungan geometri buat hitbox yang miring untuk beam
public final class GeometryUtils {
    private GeometryUtils() {}

    // true kalau segmen (ax,ay)->(bx,by) yang ditebalkan sebesar radius menyentuh kotak r
    public static boolean segmentHitsRect(float ax, float ay, float bx, float by, float radius, Rectangle r) {
        return distSegmentRect(ax, ay, bx, by, r) <= radius;
    }

    // jarak terdekat segmen ke kotak (0 kalau berpotongan/di dalam)
    public static float distSegmentRect(float ax, float ay, float bx, float by, Rectangle r) {
        if (segmentIntersectsRect(ax, ay, bx, by, r)) return 0f;

        // kalau tidak berpotongan, jarak terdekat pasti terjadi di titik sudut:
        // 4 sudut kotak ke segmen, atau ujung segmen ke kotak
        float d = Math.min(distPointRect(ax, ay, r), distPointRect(bx, by, r));
        d = Math.min(d, distPointSegment(r.x, r.y, ax, ay, bx, by));
        d = Math.min(d, distPointSegment(r.x + r.width, r.y, ax, ay, bx, by));
        d = Math.min(d, distPointSegment(r.x, r.y + r.height, ax, ay, bx, by));
        d = Math.min(d, distPointSegment(r.x + r.width, r.y + r.height, ax, ay, bx, by));
        return d;
    }

    // line clipping (shoutout to Liang Brasky)
    public static boolean segmentIntersectsRect(float ax, float ay, float bx, float by, Rectangle r) {
        float dx = bx - ax, dy = by - ay;
        float[] p = {-dx, dx, -dy, dy};
        float[] q = {ax - r.x, r.x + r.width - ax, ay - r.y, r.y + r.height - ay};
        float t0 = 0f, t1 = 1f;
        for (int i = 0; i < 4; i++) {
            if (p[i] == 0f) {
                if (q[i] < 0f) return false;
            } else {
                float t = q[i] / p[i];
                if (p[i] < 0f) { if (t > t1) return false; if (t > t0) t0 = t; }
                else           { if (t < t0) return false; if (t < t1) t1 = t; }
            }
        }
        return true;
    }

    public static float distPointRect(float px, float py, Rectangle r) {
        float dx = Math.max(Math.max(r.x - px, 0f), px - (r.x + r.width));
        float dy = Math.max(Math.max(r.y - py, 0f), py - (r.y + r.height));
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static float distPointSegment(float px, float py, float ax, float ay, float bx, float by) {
        float dx = bx - ax, dy = by - ay;
        float len2 = dx * dx + dy * dy;
        float t = (len2 == 0f) ? 0f : ((px - ax) * dx + (py - ay) * dy) / len2;
        t = Math.max(0f, Math.min(1f, t));
        float cx = ax + t * dx, cy = ay + t * dy;
        return (float) Math.sqrt((px - cx) * (px - cx) + (py - cy) * (py - cy));
    }
}
