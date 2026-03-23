package com.asocity.ringjumper000888.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.asocity.ringjumper000888.Constants;
import com.asocity.ringjumper000888.MainGame;
import com.asocity.ringjumper000888.UiFactory;

/**
 * World selection screen — displays 4 themed worlds in a 2×2 grid.
 * Locked worlds show the required score to unlock.
 */
public class WorldsScreen implements Screen {

    private static final String BG = "ui/worlds_screen.png";

    // World metadata
    private static final String[] WORLD_NAMES = {
        "AMBER GIANT", "STORM LORD", "GLACIAL TITAN", "VOID SOVEREIGN"
    };
    // World accent colours (for card border / planet circle tint)
    private static final float[][] WORLD_COLOURS = {
        {1.00f, 0.48f, 0.10f}, // amber
        {0.70f, 0.10f, 0.40f}, // red-purple
        {0.45f, 0.82f, 1.00f}, // ice blue
        {0.55f, 0.10f, 1.00f}, // neon violet
    };

    // 2×2 grid layout
    // Card size: 200×190
    // Row 1 libgdxY = 854 - 140 - 190 = 524
    // Row 2 libgdxY = 854 - 350 - 190 = 314
    // Left card X = 30, right card X = 250
    private static final float CARD_W  = 200f;
    private static final float CARD_H  = 190f;
    private static final float[] CARD_X = {30f, 250f, 30f, 250f};
    private static final float[] CARD_Y = {524f, 524f, 314f, 314f};

    private final MainGame game;
    private Stage          stage;
    private StretchViewport viewport;
    private OrthographicCamera camera;
    private ShapeRenderer  sr;
    private int            selectedWorld = -1; // -1 = none

    // Toast state
    private String  toastText = "";
    private float   toastTimer = 0f;

    public WorldsScreen(MainGame game) {
        this.game = game;
        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }

        buildUi();
        Gdx.input.setInputProcessor(stage);
        game.playMusic("sounds/music/music_menu.ogg");
    }

    private void buildUi() {
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        TextButton.TextButtonStyle btnStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);

        // Title: top-Y=40, h=50 → libgdxY = 764
        Label title = new Label("SELECT WORLD", titleStyle);
        title.setSize(320f, 50f);
        title.setPosition((Constants.WORLD_WIDTH - 320f) / 2f, 764f);
        title.setAlignment(Align.center);
        stage.addActor(title);

        // Invisible hit-area actors for each world card
        for (int i = 0; i < Constants.WORLD_COUNT; i++) {
            final int wi = i;
            Actor hitArea = new Actor();
            hitArea.setBounds(CARD_X[i], CARD_Y[i], CARD_W, CARD_H);
            hitArea.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    onWorldTapped(wi);
                }
            });
            stage.addActor(hitArea);
        }

        // BACK button: top-Y=790, h=44 → libgdxY = 20
        TextButton back = UiFactory.makeButton("BACK", btnStyle, 120f, 44f);
        back.setPosition(20f, 20f);
        back.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                sfxClick();
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(back);
    }

    private void onWorldTapped(int wi) {
        boolean unlocked = (game.unlockedWorlds & (1 << wi)) != 0;
        if (unlocked) {
            sfxClick();
            game.currentWorld = wi;
            game.savePrefs();
            game.setScreen(new GameScreen(game));
        } else {
            toastText  = "REACH " + Constants.WORLD_UNLOCK_SCORES[wi] + " SCORE TO UNLOCK";
            toastTimer = 2.5f;
            if (game.sfxEnabled)
                game.manager.get("sounds/sfx/sfx_button_back.ogg", Sound.class).play(1f);
        }
    }

    private void sfxClick() {
        if (game.sfxEnabled)
            game.manager.get("sounds/sfx/sfx_button_click.ogg", Sound.class).play(1f);
    }

    // ── Screen lifecycle ──────────────────────────────────────────────────────

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if (toastTimer > 0f) toastTimer -= delta;

        Gdx.gl.glClearColor(0f, 0.02f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(game.manager.get(BG, Texture.class),
                0f, 0f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // Draw cards
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeType.Filled);
        for (int i = 0; i < Constants.WORLD_COUNT; i++) {
            drawCard(i, sr);
        }
        sr.end();

        // Card borders + selection highlight
        sr.begin(ShapeType.Line);
        for (int i = 0; i < Constants.WORLD_COUNT; i++) {
            float[] c = WORLD_COLOURS[i];
            boolean selected = (game.currentWorld == i);
            float alpha = selected ? 1.0f : 0.55f;
            sr.setColor(c[0], c[1], c[2], alpha);
            sr.rect(CARD_X[i], CARD_Y[i], CARD_W, CARD_H);
            if (selected) {
                sr.rect(CARD_X[i] + 2f, CARD_Y[i] + 2f, CARD_W - 4f, CARD_H - 4f);
            }
        }
        sr.end();

        // Labels drawn with batch
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        for (int i = 0; i < Constants.WORLD_COUNT; i++) {
            drawCardLabels(i);
        }
        if (toastTimer > 0f) {
            float alpha = Math.min(1f, toastTimer * 2f);
            game.fontSmall.setColor(1f, 0.85f, 0.15f, alpha);
            game.fontSmall.draw(game.batch, toastText,
                    0f, 170f, Constants.WORLD_WIDTH, Align.center, false);
            game.fontSmall.setColor(Color.WHITE);
        }
        game.batch.end();

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void drawCard(int i, ShapeRenderer sr) {
        boolean unlocked = (game.unlockedWorlds & (1 << i)) != 0;
        // Card background
        sr.setColor(0.04f, 0.07f, 0.18f, 0.90f);
        sr.rect(CARD_X[i], CARD_Y[i], CARD_W, CARD_H);
        // Planet circle
        float[] c = WORLD_COLOURS[i];
        float circAlpha = unlocked ? 0.80f : 0.35f;
        sr.setColor(c[0], c[1], c[2], circAlpha);
        sr.circle(CARD_X[i] + CARD_W / 2f, CARD_Y[i] + CARD_H * 0.58f, 60f, 32);
        // Lock overlay for locked worlds
        if (!unlocked) {
            sr.setColor(0f, 0f, 0f, 0.55f);
            sr.rect(CARD_X[i], CARD_Y[i], CARD_W, CARD_H);
        }
    }

    private void drawCardLabels(int i) {
        boolean unlocked = (game.unlockedWorlds & (1 << i)) != 0;
        float cx = CARD_X[i] + CARD_W / 2f;

        // World name
        game.fontSmall.setColor(unlocked ? Color.WHITE : new Color(0.6f, 0.6f, 0.6f, 1f));
        game.fontSmall.draw(game.batch, WORLD_NAMES[i],
                CARD_X[i], CARD_Y[i] + 36f, CARD_W, Align.center, false);

        if (unlocked) {
            game.fontSmall.setColor(0.25f, 0.77f, 1.00f, 1f);
            game.fontSmall.draw(game.batch, "UNLOCKED",
                    CARD_X[i], CARD_Y[i] + 18f, CARD_W, Align.center, false);
        } else {
            game.fontSmall.setColor(1f, 0.85f, 0.15f, 1f);
            game.fontSmall.draw(game.batch, "SCORE " + Constants.WORLD_UNLOCK_SCORES[i],
                    CARD_X[i], CARD_Y[i] + 18f, CARD_W, Align.center, false);
        }
        game.fontSmall.setColor(Color.WHITE);
    }

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        stage.dispose();
        sr.dispose();
    }
}
