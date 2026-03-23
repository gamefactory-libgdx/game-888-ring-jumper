package com.asocity.ringjumper000888.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.asocity.ringjumper000888.Constants;
import com.asocity.ringjumper000888.MainGame;
import com.asocity.ringjumper000888.UiFactory;

public class SettingsScreen implements Screen {

    private static final String BG = "ui/settings_screen.png";

    // Toggle button size (pill-style)
    private static final float TOG_W = 80f;
    private static final float TOG_H = 36f;
    // Row label size
    private static final float ROW_W = 220f;
    private static final float ROW_H = 48f;
    // Back button
    private static final float BACK_W = 120f;
    private static final float BACK_H = 44f;

    private final MainGame game;
    private Stage stage;
    private StretchViewport viewport;

    // Toggle buttons — kept as fields to update label text
    private TextButton soundToggle;
    private TextButton musicToggle;
    private TextButton vibToggle;

    public SettingsScreen(MainGame game) {
        this.game = game;

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }

        OrthographicCamera camera = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);

        buildUI();
        registerInput();
    }

    // ── UI construction ───────────────────────────────────────────────────────

    private void buildUI() {
        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);
        Label.LabelStyle bodyStyle  = new Label.LabelStyle(game.fontBody,  Color.WHITE);
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);

        // Title
        // Figma: topY=48, h=50 → libgdxY = 854 - 48 - 50 = 756
        Label title = new Label("SETTINGS", titleStyle);
        title.setSize(280f, 50f);
        title.setAlignment(Align.center);
        title.setPosition((Constants.WORLD_WIDTH - 280f) / 2f, 756f);
        stage.addActor(title);

        // ── SOUND row ──────────────────────────────────────────────────────────
        // Figma: topY=180, h=48 → libgdxY = 854 - 180 - 48 = 626
        // Toggle: topY=180, h=36 → libgdxY = 854 - 180 - 36 = 638; x=right@40 → 480-40-80=360
        float soundY = 626f;
        float soundTogY = soundY + (ROW_H - TOG_H) / 2f; // vertically center toggle in row

        Label soundLabel = new Label("SOUND", bodyStyle);
        soundLabel.setSize(ROW_W, ROW_H);
        soundLabel.setFontScale(0.82f);
        soundLabel.setPosition(100f, soundY);
        stage.addActor(soundLabel);

        soundToggle = new TextButton(game.sfxEnabled ? "ON" : "OFF", rectStyle);
        soundToggle.setSize(TOG_W, TOG_H);
        soundToggle.getLabel().setFontScale(0.75f);
        soundToggle.setPosition(Constants.WORLD_WIDTH - 130f - TOG_W, soundTogY);
        soundToggle.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.sfxEnabled = !game.sfxEnabled;
                soundToggle.setText(game.sfxEnabled ? "ON" : "OFF");
                game.prefs.putBoolean(Constants.PREF_SFX, game.sfxEnabled);
                game.prefs.flush();
                playToggle();
            }
        });
        stage.addActor(soundToggle);

        // ── MUSIC row ──────────────────────────────────────────────────────────
        // Figma: topY=268, h=48 → libgdxY = 854 - 268 - 48 = 538
        float musicY = 538f;
        float musicTogY = musicY + (ROW_H - TOG_H) / 2f;

        Label musicLabel = new Label("MUSIC", bodyStyle);
        musicLabel.setSize(ROW_W, ROW_H);
        musicLabel.setFontScale(0.82f);
        musicLabel.setPosition(100f, musicY);
        stage.addActor(musicLabel);

        musicToggle = new TextButton(game.musicEnabled ? "ON" : "OFF", rectStyle);
        musicToggle.setSize(TOG_W, TOG_H);
        musicToggle.getLabel().setFontScale(0.75f);
        musicToggle.setPosition(Constants.WORLD_WIDTH - 130f - TOG_W, musicTogY);
        musicToggle.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.musicEnabled = !game.musicEnabled;
                musicToggle.setText(game.musicEnabled ? "ON" : "OFF");
                game.prefs.putBoolean(Constants.PREF_MUSIC, game.musicEnabled);
                game.prefs.flush();
                if (game.currentMusic != null) {
                    if (game.musicEnabled) game.currentMusic.play();
                    else game.currentMusic.pause();
                }
                playToggle();
            }
        });
        stage.addActor(musicToggle);

        // ── VIBRATION row ──────────────────────────────────────────────────────
        // Figma: topY=356, h=48 → libgdxY = 854 - 356 - 48 = 450
        float vibY = 450f;
        float vibTogY = vibY + (ROW_H - TOG_H) / 2f;

        Label vibLabel = new Label("VIBRATION", bodyStyle);
        vibLabel.setSize(ROW_W, ROW_H);
        vibLabel.setFontScale(0.82f);
        vibLabel.setPosition(100f, vibY);
        stage.addActor(vibLabel);

        vibToggle = new TextButton(game.vibrationEnabled ? "ON" : "OFF", rectStyle);
        vibToggle.setSize(TOG_W, TOG_H);
        vibToggle.getLabel().setFontScale(0.75f);
        vibToggle.setPosition(Constants.WORLD_WIDTH - 130f - TOG_W, vibTogY);
        vibToggle.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.vibrationEnabled = !game.vibrationEnabled;
                vibToggle.setText(game.vibrationEnabled ? "ON" : "OFF");
                game.prefs.putBoolean(Constants.PREF_VIBRATION, game.vibrationEnabled);
                game.prefs.flush();
                playToggle();
            }
        });
        stage.addActor(vibToggle);

        // ── BACK button ────────────────────────────────────────────────────────
        // Figma: topY=790, x=left@20, h=44 → libgdxY = 854 - 790 - 44 = 20
        TextButton backBtn = UiFactory.makeButton("BACK", rectStyle, BACK_W, BACK_H);
        backBtn.setPosition(20f, 20f);
        backBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playBack();
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(backBtn);
    }

    // ── Input ─────────────────────────────────────────────────────────────────

    private void registerInput() {
        InputMultiplexer mux = new InputMultiplexer(stage, new InputAdapter() {
            @Override public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        });
        Gdx.input.setInputProcessor(mux);
    }

    // ── Sound helpers ─────────────────────────────────────────────────────────

    private void playToggle() {
        if (game.sfxEnabled && game.manager.isLoaded("sounds/sfx/sfx_toggle.ogg")) {
            game.manager.get("sounds/sfx/sfx_toggle.ogg", Sound.class).play(0.5f);
        }
    }

    private void playBack() {
        if (game.sfxEnabled && game.manager.isLoaded("sounds/sfx/sfx_button_back.ogg")) {
            game.manager.get("sounds/sfx/sfx_button_back.ogg", Sound.class).play(1.0f);
        }
    }

    // ── Screen lifecycle ──────────────────────────────────────────────────────

    @Override
    public void show() {
        game.playMusic("sounds/music/music_menu.ogg");
        registerInput();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0.031f, 0.078f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.begin();
        game.batch.draw(
            game.manager.get(BG, Texture.class),
            0f, 0f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT
        );
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
