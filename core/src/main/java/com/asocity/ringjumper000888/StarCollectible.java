package com.asocity.ringjumper000888;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/** A briefly-visible collectible star that co-rotates with the pod. */
public class StarCollectible {

    public int   ring;
    public float angle;      // current world angle (degrees); advances with pod speed
    public float lifetime;   // seconds remaining
    public float alpha;      // for blink (0..1)

    private float blinkTimer;

    // ── Constructor ───────────────────────────────────────────────────────────

    /** Spawns at (ring, podAngle) so it appears alongside the pod on a different ring. */
    public StarCollectible(int ring, float angle, float visibility) {
        this.ring      = ring;
        this.angle     = angle;
        this.lifetime  = visibility;
        this.alpha     = 1f;
        this.blinkTimer = 0f;
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public void update(float delta, float podAngularSpeed) {
        angle += podAngularSpeed * delta;
        if (angle > 360f) angle -= 360f;
        lifetime  -= delta;
        blinkTimer += delta;
        // 2 Hz blink
        alpha = 0.45f + 0.55f * MathUtils.sin(blinkTimer * MathUtils.PI2 * 2f);
        if (alpha < 0.05f) alpha = 0.05f;
    }

    public boolean isExpired() {
        return lifetime <= 0f;
    }

    // ── World position ────────────────────────────────────────────────────────

    public float worldX() {
        float halfW = Constants.RING_HALF_WIDTHS[ring - 1];
        return Constants.CENTRE_X + halfW * MathUtils.cosDeg(angle);
    }

    public float worldY() {
        float halfW = Constants.RING_HALF_WIDTHS[ring - 1];
        float halfH = halfW * Constants.RING_VERTICAL_SCALE;
        return Constants.CENTRE_Y + halfH * MathUtils.sinDeg(angle);
    }

    // ── Collision ─────────────────────────────────────────────────────────────

    public boolean overlaps(float podX, float podY) {
        float dx = worldX() - podX;
        float dy = worldY() - podY;
        float sumR = Constants.POD_RADIUS + Constants.STAR_RADIUS;
        return (dx * dx + dy * dy) < (sumR * sumR);
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    /** Draw using SpriteBatch with star.png texture. */
    public void draw(SpriteBatch batch, Texture starTex) {
        float s = Constants.STAR_SIZE * 1.4f; // slightly larger than collision radius
        float cx = worldX();
        float cy = worldY();
        batch.setColor(1f, 0.85f, 0.15f, alpha); // yellow
        batch.draw(starTex, cx - s * 0.5f, cy - s * 0.5f, s, s);
        batch.setColor(1f, 1f, 1f, 1f);
    }
}
