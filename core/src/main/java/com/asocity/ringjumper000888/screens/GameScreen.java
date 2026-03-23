package com.asocity.ringjumper000888.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.asocity.ringjumper000888.Constants;
import com.asocity.ringjumper000888.DebrisField;
import com.asocity.ringjumper000888.MainGame;
import com.asocity.ringjumper000888.StarCollectible;
import com.asocity.ringjumper000888.UiFactory;

/**
 * Core gameplay screen.
 * Pod orbits a gas giant on one of 4 elliptical rings.
 * Tap to jump rings; avoid debris, collect stars.
 */
public class GameScreen implements Screen {

    private static final String BG = "ui/game_screen.png";

    // Ship sprite paths (skin 0-5 → LVL 1-5, skin 5 reuses LVL 5)
    private static final String[] SKIN_TEX = {
        "sprites/vehicle/Ship_LVL_1.png",
        "sprites/vehicle/Ship_LVL_2.png",
        "sprites/vehicle/Ship_LVL_3.png",
        "sprites/vehicle/Ship_LVL_4.png",
        "sprites/vehicle/Ship_LVL_5.png",
        "sprites/vehicle/Ship_LVL_5.png",
    };

    // Ring tints per world (R, G, B)
    private static final float[][] RING_COLOUR = {
        {1.00f, 0.48f, 0.10f}, // world 0 — amber
        {0.70f, 0.15f, 0.40f}, // world 1 — storm red
        {0.45f, 0.82f, 1.00f}, // world 2 — glacial blue
        {0.55f, 0.10f, 0.90f}, // world 3 — void violet
    };

    // ── State ─────────────────────────────────────────────────────────────────

    private final MainGame game;
    private final int      worldIndex;

    // Pod
    private int   podRing;          // 1-4
    private float podAngle;         // degrees
    private float podAngularSpeed;  // degrees/second
    private int   jumpDirection;    // +1 outward, -1 inward

    // Game objects
    private final Array<DebrisField>     debrisFields = new Array<>();
    private final Array<StarCollectible> stars        = new Array<>();
    private float starSpawnTimer;
    private float currentStarVisibility;
    private float currentStarInterval;

    // Difficulty tracking (applied to NEW fields/stars)
    private float currentArcSpan;
    private float currentDebrisSpeed;

    // Scoring
    private int   score;
    private float scoreAccumulator;
    private int   starsThisRun;
    private int   lastMilestone;

    // Time
    private float elapsedTime;

    // Death animation
    private boolean dead        = false;
    private float   deathTimer  = 0f;
    private boolean transitioned = false;

    // Pod visual (rotation for directional sprite)
    private float podSpriteRotation = 0f;

    // ── Rendering ─────────────────────────────────────────────────────────────

    private Stage          stage;
    private StretchViewport viewport;
    private OrthographicCamera camera;
    private ShapeRenderer  sr;

    // HUD labels (updated each frame)
    private Label scoreLabel;
    private Label starsLabel;

    // ── Constructor ───────────────────────────────────────────────────────────

    /** Convenience 1-arg constructor — uses game.currentWorld as worldIndex. */
    public GameScreen(MainGame game) {
        this(game, game.currentWorld);
    }

    public GameScreen(MainGame game, int worldIndex) {
        this.game       = game;
        this.worldIndex = worldIndex;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        // Load game-specific textures
        if (!game.manager.isLoaded(BG)) game.manager.load(BG, Texture.class);
        for (String path : SKIN_TEX) {
            if (!game.manager.isLoaded(path)) game.manager.load(path, Texture.class);
        }
        game.manager.finishLoading();

        resetGame();
        buildHud();
        setupInput();

        game.playMusic("sounds/music/music_gameplay.ogg");
    }

    // ── Reset / init ──────────────────────────────────────────────────────────

    private void resetGame() {
        podRing         = Constants.POD_START_RING;
        podAngle        = Constants.POD_START_ANGLE;
        podAngularSpeed = Constants.WORLD_POD_SPEEDS[worldIndex];
        jumpDirection   = 1;
        score           = 0;
        scoreAccumulator = 0f;
        starsThisRun    = 0;
        lastMilestone   = 0;
        elapsedTime     = 0f;
        dead            = false;
        deathTimer      = 0f;
        transitioned    = false;
        debrisFields.clear();
        stars.clear();
        currentArcSpan    = Constants.DEBRIS_ARC_INITIAL;
        currentDebrisSpeed = Constants.DEBRIS_SPEED_INITIAL;
        currentStarVisibility = Constants.STAR_VISIBILITY_INITIAL;
        currentStarInterval   = Constants.STAR_SPAWN_INITIAL;
        starSpawnTimer = currentStarInterval;
        // Spawn initial debris fields
        for (int i = 0; i < Constants.DEBRIS_FIELDS_INITIAL; i++) {
            spawnDebrisField();
        }
    }

    // ── HUD ───────────────────────────────────────────────────────────────────

    private void buildHud() {
        Label.LabelStyle scoreStyle = new Label.LabelStyle(game.fontScore,
                new Color(1f, 0.85f, 0.15f, 1f));
        Label.LabelStyle hudStyle = new Label.LabelStyle(game.fontBody,
                Color.WHITE);
        TextButton.TextButtonStyle roundStyle = UiFactory.makeRoundStyle(game.manager, game.fontBody);

        // Score label: top-Y=24, h=40 → libgdxY = 790, centered
        scoreLabel = new Label("0", scoreStyle);
        scoreLabel.setSize(200f, 50f);
        scoreLabel.setPosition(16f, 790f);
        scoreLabel.setAlignment(Align.left);
        stage.addActor(scoreLabel);

        // Stars collected: top-Y=68, h=32 → libgdxY = 754, centered
        Label.LabelStyle iconStyle = new Label.LabelStyle(game.fontIcon, Color.WHITE);
        starsLabel = new Label("* 0", iconStyle);
        starsLabel.setSize(160f, 32f);
        starsLabel.setPosition(16f, 752f);
        starsLabel.setAlignment(Align.left);
        stage.addActor(starsLabel);

        // Pause button: top-Y=20, h=52, x=right@16 → libgdxY = 782, x = 480-52-16 = 412
        TextButton pauseBtn = UiFactory.makeButton("||", roundStyle,
                Constants.BTN_ROUND, Constants.BTN_ROUND);
        pauseBtn.setPosition(412f, 782f);
        pauseBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                sfxClick();
                game.setScreen(new PauseScreen(game, GameScreen.this));
            }
        });
        stage.addActor(pauseBtn);
    }

    // ── Input ─────────────────────────────────────────────────────────────────

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(
            stage,
            new InputAdapter() {
                @Override
                public boolean touchDown(int x, int y, int pointer, int button) {
                    if (!dead && !transitioned) onTap();
                    return true;
                }
                @Override
                public boolean keyDown(int keycode) {
                    if (keycode == Input.Keys.BACK && !dead && !transitioned) {
                        sfxClick();
                        game.setScreen(new PauseScreen(game, GameScreen.this));
                        return true;
                    }
                    return false;
                }
            }
        ));
    }

    // ── Game logic ────────────────────────────────────────────────────────────

    private void onTap() {
        int newRing = podRing + jumpDirection;
        if (newRing < 1) {
            newRing = 1;
            jumpDirection = +1;
        } else if (newRing > Constants.RING_COUNT) {
            newRing = Constants.RING_COUNT;
            jumpDirection = -1;
        }
        podRing = newRing;
        if (game.sfxEnabled)
            game.manager.get("sounds/sfx/sfx_jump.ogg", Sound.class).play(0.9f);
    }

    private void update(float delta) {
        elapsedTime += delta;

        // Pod orbit
        podAngle += podAngularSpeed * delta;
        if (podAngle >= 360f) podAngle -= 360f;

        // Update pod sprite rotation (tangent direction)
        float halfW = Constants.RING_HALF_WIDTHS[podRing - 1];
        float halfH = halfW * Constants.RING_VERTICAL_SCALE;
        float tangentX = -halfW * MathUtils.sinDeg(podAngle);
        float tangentY =  halfH * MathUtils.cosDeg(podAngle);
        podSpriteRotation = MathUtils.atan2(tangentY, tangentX) * MathUtils.radiansToDegrees;

        updateDifficulty();

        // Debris update
        for (DebrisField f : debrisFields) {
            f.update(delta);
        }

        // Star spawn
        starSpawnTimer -= delta;
        if (starSpawnTimer <= 0f) {
            spawnStar();
            starSpawnTimer = currentStarInterval;
        }

        // Star update
        for (int i = stars.size - 1; i >= 0; i--) {
            StarCollectible s = stars.get(i);
            s.update(delta, podAngularSpeed);
            if (s.isExpired()) stars.removeIndex(i);
        }

        // Scoring: +1 per second
        scoreAccumulator += delta;
        while (scoreAccumulator >= 1f) {
            scoreAccumulator -= 1f;
            int prev = score;
            score += Constants.SCORE_PER_SECOND;
            // Milestone check
            if (score / Constants.SCORE_MILESTONE_STEP > prev / Constants.SCORE_MILESTONE_STEP) {
                score += Constants.SCORE_MILESTONE_BONUS;
                lastMilestone = (score / Constants.SCORE_MILESTONE_STEP) * Constants.SCORE_MILESTONE_STEP;
            }
        }

        checkWorldUnlocks();
        checkCollisions();
    }

    private void updateDifficulty() {
        // Active debris field count
        int targetFields = Constants.DEBRIS_FIELDS_INITIAL
                + (int)(elapsedTime / Constants.DEBRIS_FIELDS_INTERVAL);
        targetFields = Math.min(targetFields, Constants.DEBRIS_FIELDS_MAX);
        while (debrisFields.size < targetFields) spawnDebrisField();

        // Arc span
        float arcSpan = Constants.DEBRIS_ARC_INITIAL
                + (int)(elapsedTime / Constants.DEBRIS_ARC_INCREMENT_INTERVAL)
                  * Constants.DEBRIS_ARC_INCREMENT;
        currentArcSpan = Math.min(arcSpan, Constants.DEBRIS_ARC_MAX);

        // Debris speed
        int speedSteps = (int)(elapsedTime / Constants.DEBRIS_SPEED_INTERVAL);
        float debrisSpeed = Constants.DEBRIS_SPEED_INITIAL
                * (float)Math.pow(Constants.DEBRIS_SPEED_MULTIPLIER, speedSteps);
        currentDebrisSpeed = Math.min(debrisSpeed, Constants.DEBRIS_SPEED_MAX);

        // Star visibility
        int visSteps = (int)(elapsedTime / Constants.STAR_VISIBILITY_INTERVAL);
        float vis = Constants.STAR_VISIBILITY_INITIAL
                - visSteps * Constants.STAR_VISIBILITY_DECREMENT;
        currentStarVisibility = Math.max(vis, Constants.STAR_VISIBILITY_FLOOR);

        // Star spawn interval
        float spawnInterval = Constants.STAR_SPAWN_INITIAL
                + (int)(elapsedTime / Constants.STAR_SPAWN_INTERVAL)
                  * Constants.STAR_SPAWN_INCREMENT;
        currentStarInterval = Math.min(spawnInterval, Constants.STAR_SPAWN_MAX);
    }

    private void spawnDebrisField() {
        int ring = MathUtils.random(1, Constants.RING_COUNT);
        debrisFields.add(new DebrisField(ring, currentArcSpan, currentDebrisSpeed));
    }

    private void spawnStar() {
        // Pick a ring different from the pod's current ring
        int starRing;
        do { starRing = MathUtils.random(1, Constants.RING_COUNT); }
        while (starRing == podRing);
        stars.add(new StarCollectible(starRing, podAngle, currentStarVisibility));
    }

    private void checkCollisions() {
        float halfW = Constants.RING_HALF_WIDTHS[podRing - 1];
        float halfH = halfW * Constants.RING_VERTICAL_SCALE;
        float podX  = Constants.CENTRE_X + halfW * MathUtils.cosDeg(podAngle);
        float podY  = Constants.CENTRE_Y + halfH * MathUtils.sinDeg(podAngle);

        // Debris
        for (DebrisField f : debrisFields) {
            if (f.checkCollision(podRing, podAngle)) {
                onPodDeath();
                return;
            }
        }

        // Stars
        for (int i = stars.size - 1; i >= 0; i--) {
            StarCollectible s = stars.get(i);
            if (s.ring == podRing && s.overlaps(podX, podY)) {
                onStarCollected(i);
            }
        }
    }

    private void onPodDeath() {
        dead = true;
        deathTimer = 0f;
        if (game.sfxEnabled)
            game.manager.get("sounds/sfx/sfx_hit.ogg", Sound.class).play(1f);
        if (game.sfxEnabled)
            game.manager.get("sounds/sfx/sfx_game_over.ogg", Sound.class).play(0.8f);
        if (game.vibrationEnabled) { try { Gdx.input.vibrate(60); } catch (Exception e) { /* ignore */ } }
    }

    private void onStarCollected(int index) {
        starsThisRun++;
        int prev = score;
        score += Constants.SCORE_PER_STAR;
        if (score / Constants.SCORE_MILESTONE_STEP > prev / Constants.SCORE_MILESTONE_STEP) {
            score += Constants.SCORE_MILESTONE_BONUS;
        }
        stars.removeIndex(index);
        if (game.sfxEnabled)
            game.manager.get("sounds/sfx/sfx_coin.ogg", Sound.class).play(1f);
        if (game.vibrationEnabled) { try { Gdx.input.vibrate(20); } catch (Exception e) { /* ignore */ } }
    }

    private void checkWorldUnlocks() {
        for (int w = 0; w < Constants.WORLD_COUNT; w++) {
            if ((game.unlockedWorlds & (1 << w)) == 0
                    && score >= Constants.WORLD_UNLOCK_SCORES[w]) {
                game.unlockedWorlds |= (1 << w);
                game.savePrefs();
            }
        }
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    private void drawRings() {
        float[] rc = RING_COLOUR[worldIndex];
        sr.setColor(rc[0], rc[1], rc[2], 0.60f);
        for (int i = 0; i < Constants.RING_COUNT; i++) {
            float hw = Constants.RING_HALF_WIDTHS[i];
            float hh = hw * Constants.RING_VERTICAL_SCALE;
            sr.ellipse(Constants.CENTRE_X - hw, Constants.CENTRE_Y - hh, hw * 2f, hh * 2f, 64);
        }
    }

    private void drawDebrisAndStars() {
        sr.begin(ShapeType.Filled);
        for (DebrisField f : debrisFields) f.draw(sr);
        for (StarCollectible s : stars)       s.draw(sr);
        sr.end();
    }

    private void drawPod(float scale) {
        int skin = MathUtils.clamp(game.equippedSkin, 0, SKIN_TEX.length - 1);
        Texture tex = game.manager.get(SKIN_TEX[skin], Texture.class);
        float hw  = Constants.RING_HALF_WIDTHS[podRing - 1];
        float hh  = hw * Constants.RING_VERTICAL_SCALE;
        float px  = Constants.CENTRE_X + hw * MathUtils.cosDeg(podAngle);
        float py  = Constants.CENTRE_Y + hh * MathUtils.sinDeg(podAngle);
        float sz  = Constants.POD_SIZE * scale;
        // Use Texture overload directly to avoid allocating TextureRegion each frame
        game.batch.draw(tex,
                px - sz * 0.5f, py - sz * 0.5f,
                sz * 0.5f, sz * 0.5f,
                sz, sz,
                1f, 1f,
                podSpriteRotation,
                0, 0, tex.getWidth(), tex.getHeight(),
                false, false
        );
    }

    // ── Screen lifecycle ──────────────────────────────────────────────────────

    @Override
    public void show() {
        // Re-register input when returning from PauseScreen
        setupInput();
        game.playMusic("sounds/music/music_gameplay.ogg");
    }

    @Override
    public void render(float delta) {
        // ── Update ────────────────────────────────────────────────────────────
        if (dead) {
            deathTimer += delta;
            if (!transitioned && deathTimer >= Constants.DEATH_ANIM_DURATION) {
                transitioned = true;
                game.setScreen(new GameOverScreen(game, score, starsThisRun));
                return;
            }
        } else {
            update(delta);
        }

        // Update HUD labels
        scoreLabel.setText(String.valueOf(score));
        starsLabel.setText("* " + starsThisRun);

        // ── Render ────────────────────────────────────────────────────────────
        Gdx.gl.glClearColor(0f, 0.02f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(game.manager.get(BG, Texture.class),
                0f, 0f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // Rings (LINE mode)
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeType.Line);
        drawRings();
        sr.end();

        // Debris + stars (FILLED mode)
        sr.setProjectionMatrix(camera.combined);
        drawDebrisAndStars();

        // Pod sprite
        float podScale = dead ? Math.max(0f, 1f - deathTimer / Constants.DEATH_ANIM_DURATION) : 1f;
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        drawPod(podScale);
        game.batch.end();

        // Death flash overlay
        if (dead) {
            float flashAlpha = Math.max(0f, 0.55f * (1f - deathTimer / Constants.DEATH_ANIM_DURATION));
            Gdx.gl.glEnable(GL20.GL_BLEND);
            sr.setProjectionMatrix(camera.combined);
            sr.begin(ShapeType.Filled);
            sr.setColor(1f, 0.08f, 0f, flashAlpha);
            sr.rect(0f, 0f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
            sr.end();
        }

        // HUD always on top
        stage.act(delta);
        stage.draw();
    }

    private void sfxClick() {
        if (game.sfxEnabled)
            game.manager.get("sounds/sfx/sfx_button_click.ogg", Sound.class).play(1f);
    }

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void pause()  {}
    @Override public void resume() { setupInput(); }
    @Override public void hide()   {}

    @Override
    public void dispose() {
        stage.dispose();
        sr.dispose();
    }
}
