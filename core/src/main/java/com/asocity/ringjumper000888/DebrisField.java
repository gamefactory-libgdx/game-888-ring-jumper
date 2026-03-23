package com.asocity.ringjumper000888;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/** A rotating arc of debris chunks on one of the four orbital rings. */
public class DebrisField {

    // ── Inner chunk ───────────────────────────────────────────────────────────

    public static class DebrisChunk {
        public float relativeAngle; // offset within arc (degrees)
    }

    // ── Fields ────────────────────────────────────────────────────────────────

    public  int          ring;            // 1-4 (ring index)
    public  float        arcStartAngle;   // degrees
    public  float        arcSpanDegrees;
    public  float        rotationSpeed;   // degrees/second (signed)
    public  float        currentRotation; // accumulated
    public  int          chunkCount;
    public  DebrisChunk[] chunks;

    // ── Constructor ───────────────────────────────────────────────────────────

    public DebrisField(int ring, float arcSpan, float speed) {
        this.ring          = ring;
        this.arcStartAngle = MathUtils.random(0f, 360f);
        this.arcSpanDegrees = arcSpan;
        this.rotationSpeed  = speed * (MathUtils.randomBoolean() ? 1f : -1f);
        this.currentRotation = 0f;
        this.chunkCount    = MathUtils.random(Constants.DEBRIS_CHUNKS_MIN, Constants.DEBRIS_CHUNKS_MAX);
        this.chunks        = new DebrisChunk[chunkCount];
        for (int i = 0; i < chunkCount; i++) {
            chunks[i] = new DebrisChunk();
            // Spread evenly across arc
            chunks[i].relativeAngle = chunkCount > 1
                    ? (arcSpanDegrees * i) / (chunkCount - 1)
                    : arcSpanDegrees / 2f;
        }
    }

    // ── Update ────────────────────────────────────────────────────────────────

    /** Advance rotation. When a full orbit completes the field is reassigned to a random ring. */
    public void update(float delta) {
        currentRotation += rotationSpeed * delta;
        if (currentRotation > 360f) {
            currentRotation -= 360f;
            ring = MathUtils.random(1, Constants.RING_COUNT);
        } else if (currentRotation < -360f) {
            currentRotation += 360f;
            ring = MathUtils.random(1, Constants.RING_COUNT);
        }
    }

    // ── Position helpers ──────────────────────────────────────────────────────

    public float chunkAngle(int i) {
        return arcStartAngle + currentRotation + chunks[i].relativeAngle;
    }

    public float chunkX(int i) {
        float halfW = Constants.RING_HALF_WIDTHS[ring - 1];
        return Constants.CENTRE_X + halfW * MathUtils.cosDeg(chunkAngle(i));
    }

    public float chunkY(int i) {
        float halfW = Constants.RING_HALF_WIDTHS[ring - 1];
        float halfH = halfW * Constants.RING_VERTICAL_SCALE;
        return Constants.CENTRE_Y + halfH * MathUtils.sinDeg(chunkAngle(i));
    }

    // ── Collision ─────────────────────────────────────────────────────────────

    /** True if the pod (ring, angle) overlaps any debris chunk. */
    public boolean checkCollision(int podRing, float podAngle) {
        if (podRing != ring) return false;
        float podHalfW = Constants.RING_HALF_WIDTHS[podRing - 1];
        float podHalfH = podHalfW * Constants.RING_VERTICAL_SCALE;
        float podX = Constants.CENTRE_X + podHalfW * MathUtils.cosDeg(podAngle);
        float podY = Constants.CENTRE_Y + podHalfH * MathUtils.sinDeg(podAngle);
        float sumR  = Constants.POD_RADIUS + Constants.DEBRIS_CHUNK_RADIUS;
        float sumR2 = sumR * sumR;
        for (int i = 0; i < chunkCount; i++) {
            float dx = podX - chunkX(i);
            float dy = podY - chunkY(i);
            if (dx * dx + dy * dy < sumR2) return true;
        }
        return false;
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    /** Draw chunks. ShapeRenderer must be in FILLED mode and already begun. */
    public void draw(ShapeRenderer sr) {
        float s = Constants.DEBRIS_CHUNK_SIZE;
        sr.setColor(0.85f, 0.42f, 0.10f, 1f); // orange-amber
        for (int i = 0; i < chunkCount; i++) {
            float cx = chunkX(i);
            float cy = chunkY(i);
            sr.rect(cx - s * 0.5f, cy - s * 0.5f, s, s);
        }
    }
}
