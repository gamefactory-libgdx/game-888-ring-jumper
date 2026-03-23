package com.asocity.ringjumper000888.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.asocity.ringjumper000888.Constants;
import com.asocity.ringjumper000888.MainGame;
import com.asocity.ringjumper000888.UiFactory;

/**
 * Pause screen — overlays the game with a translucent dark sheet.
 * Resume returns to the same GameScreen instance; Restart creates a new one.
 */
public class PauseScreen implements Screen {

    private final MainGame game;
    private final Screen   previousScreen; // GameScreen instance to resume

    private Stage          stage;
    private StretchViewport viewport;
    private OrthographicCamera camera;
    private ShapeRenderer  sr;

    public PauseScreen(MainGame game, Screen previousScreen) {
        this.game           = game;
        this.previousScreen = previousScreen;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        buildUi();
        Gdx.input.setInputProcessor(stage);
    }

    private void buildUi() {
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        TextButton.TextButtonStyle btnStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);

        // "PAUSED" title centred
        Label title = new Label("PAUSED", titleStyle);
        title.setSize(260f, 60f);
        title.setPosition((Constants.WORLD_WIDTH - 260f) / 2f, 530f);
        title.setAlignment(Align.center);
        stage.addActor(title);

        // RESUME — top-Y ≈ 350 → libgdxY = 448
        TextButton resume = UiFactory.makeCentredButton("RESUME", btnStyle,
                Constants.BTN_W_MAIN, Constants.BTN_H_MAIN, 448f);
        resume.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                sfxClick();
                game.setScreen(previousScreen); // same GameScreen instance
            }
        });
        stage.addActor(resume);

        // RESTART — libgdxY = 376
        TextButton restart = UiFactory.makeCentredButton("RESTART", btnStyle,
                Constants.BTN_W_MAIN, Constants.BTN_H_MAIN, 376f);
        restart.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                sfxClick();
                game.setScreen(new GameScreen(game)); // NEW instance
            }
        });
        stage.addActor(restart);

        // MENU — libgdxY = 304
        TextButton menu = UiFactory.makeCentredButton("MENU", btnStyle,
                Constants.BTN_W_MAIN, Constants.BTN_H_MAIN, 304f);
        menu.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                sfxClick();
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(menu);
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
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dark translucent overlay (full screen)
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeType.Filled);
        sr.setColor(0f, 0.02f, 0.05f, 0.88f);
        sr.rect(0f, 0f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        // Decorative panel behind buttons
        sr.setColor(0.04f, 0.07f, 0.18f, 0.92f);
        sr.rect(60f, 280f, 360f, 360f);
        sr.end();

        // Panel border
        sr.begin(ShapeType.Line);
        sr.setColor(0.12f, 0.81f, 0.81f, 0.85f);
        sr.rect(60f, 280f, 360f, 360f);
        sr.end();

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(previousScreen);
        }
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
