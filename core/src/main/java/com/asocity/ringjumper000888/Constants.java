package com.asocity.ringjumper000888;

public final class Constants {

    private Constants() {}

    // ── Viewport ─────────────────────────────────────────────────────────────
    public static final float WORLD_WIDTH  = 480f;
    public static final float WORLD_HEIGHT = 854f;

    // Scene centre
    public static final float CENTRE_X = WORLD_WIDTH  / 2f;   // 240
    public static final float CENTRE_Y = WORLD_HEIGHT / 2f;   // 427

    // ── Planet ───────────────────────────────────────────────────────────────
    public static final float PLANET_DIAMETER = 220f;

    // ── Rings ────────────────────────────────────────────────────────────────
    public static final int   RING_COUNT          = 4;
    public static final float[] RING_HALF_WIDTHS  = {130f, 185f, 240f, 295f};
    public static final float   RING_VERTICAL_SCALE = 0.45f;
    public static final float   RING_LINE_WIDTH    = 2f;

    // ── Pod ──────────────────────────────────────────────────────────────────
    public static final float POD_SIZE        = 32f;
    public static final float POD_RADIUS      = 12f;
    public static final int   POD_START_RING  = 2;       // 1-based
    public static final float POD_START_ANGLE = 0f;

    // Pod angular speeds per world (degrees/second)
    public static final float[] WORLD_POD_SPEEDS = {45f, 60f, 80f, 105f};

    // ── Worlds ───────────────────────────────────────────────────────────────
    public static final int   WORLD_COUNT         = 4;
    public static final int[] WORLD_UNLOCK_SCORES = {0, 300, 700, 1500};

    // ── Debris ───────────────────────────────────────────────────────────────
    public static final float DEBRIS_CHUNK_RADIUS           = 9f;
    public static final float DEBRIS_CHUNK_SIZE             = 20f;
    public static final int   DEBRIS_CHUNKS_MIN             = 2;
    public static final int   DEBRIS_CHUNKS_MAX             = 5;
    public static final int   DEBRIS_FIELDS_INITIAL         = 1;
    public static final int   DEBRIS_FIELDS_MAX             = 6;
    public static final float DEBRIS_FIELDS_INTERVAL        = 20f;   // seconds
    public static final float DEBRIS_ARC_INITIAL            = 40f;   // degrees
    public static final float DEBRIS_ARC_INCREMENT          = 5f;
    public static final float DEBRIS_ARC_INCREMENT_INTERVAL = 30f;
    public static final float DEBRIS_ARC_MAX                = 90f;
    public static final float DEBRIS_SPEED_INITIAL          = 30f;   // degrees/second
    public static final float DEBRIS_SPEED_MULTIPLIER       = 1.08f;
    public static final float DEBRIS_SPEED_INTERVAL         = 15f;
    public static final float DEBRIS_SPEED_MAX              = 120f;

    // ── Star collectible ─────────────────────────────────────────────────────
    public static final float STAR_RADIUS               = 11f;
    public static final float STAR_SIZE                 = 24f;
    public static final float STAR_VISIBILITY_INITIAL   = 2.5f;   // seconds
    public static final float STAR_VISIBILITY_DECREMENT = 0.1f;
    public static final float STAR_VISIBILITY_INTERVAL  = 25f;
    public static final float STAR_VISIBILITY_FLOOR     = 1.2f;
    public static final float STAR_SPAWN_INITIAL        = 4f;     // seconds between spawns
    public static final float STAR_SPAWN_INCREMENT      = 0.2f;
    public static final float STAR_SPAWN_INTERVAL       = 30f;
    public static final float STAR_SPAWN_MAX            = 7f;

    // ── Scoring ──────────────────────────────────────────────────────────────
    public static final int SCORE_PER_SECOND      = 1;
    public static final int SCORE_PER_STAR        = 10;
    public static final int SCORE_MILESTONE_STEP  = 100;
    public static final int SCORE_MILESTONE_BONUS = 50;

    // ── Shop — skin prices (index 0 = free) ──────────────────────────────────
    public static final int[] SKIN_PRICES = {0, 50, 200, 500, 1000, 2000};
    public static final int   SKIN_COUNT  = 6;

    // ── Death animation ──────────────────────────────────────────────────────
    public static final float DEATH_ANIM_DURATION = 0.4f;   // seconds

    // ── UI transitions ───────────────────────────────────────────────────────
    public static final float TRANSITION_DURATION = 0.15f;

    // ── Leaderboard ──────────────────────────────────────────────────────────
    public static final int LEADERBOARD_MAX_ENTRIES = 10;

    // ── SharedPreferences ────────────────────────────────────────────────────
    public static final String PREFS_NAME         = "ring_jumper_prefs";
    public static final String PREF_STARS_BALANCE  = "starsBalance";
    public static final String PREF_OWNED_SKINS    = "ownedSkins";
    public static final String PREF_SKIN           = "equippedSkin";
    public static final String PREF_HIGH_SCORE     = "highScore";
    public static final String PREF_CURRENT_WORLD  = "currentWorld";
    public static final String PREF_UNLOCKED_WORLDS = "unlockedWorlds";
    public static final String PREF_MUSIC          = "musicEnabled";
    public static final String PREF_SFX            = "sfxEnabled";
    public static final String PREF_VIBRATION      = "vibrationEnabled";
    public static final String PREF_LEADERBOARD    = "leaderboard";

    // ── Button sizes ─────────────────────────────────────────────────────────
    public static final float BTN_W_MAIN   = 280f;
    public static final float BTN_H_MAIN   = 56f;
    public static final float BTN_W_SEC    = 220f;
    public static final float BTN_H_SEC    = 50f;
    public static final float BTN_W_SMALL  = 160f;
    public static final float BTN_H_SMALL  = 44f;
    public static final float BTN_ROUND    = 56f;

    // ── HUD ──────────────────────────────────────────────────────────────────
    public static final float HUD_PADDING  = 16f;
    public static final float HUD_ICON_SIZE = 28f;
}
