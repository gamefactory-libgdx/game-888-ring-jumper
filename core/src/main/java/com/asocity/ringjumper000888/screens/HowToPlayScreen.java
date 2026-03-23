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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.asocity.ringjumper000888.Constants;
import com.asocity.ringjumper000888.MainGame;
import com.asocity.ringjumper000888.UiFactory;

public class HowToPlayScreen implements Screen {

    private static final String BG = "ui/how_to_play_screen.png";

    // Panel Y positions (libgdxY = WORLD_HEIGHT - topY - height)
    // Panel 1: top-Y=110, h=130 → libgdxY = 854-110-130 = 614
    // Panel 2: top-Y=260, h=130 → libgdxY = 854-260-130 = 464
    // Panel 3: top-Y=410, h=130 → libgdxY = 854-410-130 = 314
    private static final float[] PANEL_Y = {633f, 457f, 272f};
    private static final float PANEL_W   = 340f;
    private static final float PANEL_H   = 130f;

    private final MainGame game;
    private Stage          stage;
    private StretchViewport viewport;
    private OrthographicCamera camera;
    private ShapeRenderer  sr;

    public HowToPlayScreen(MainGame game) {
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
        Label.LabelStyle headStyle  = new Label.LabelStyle(game.fontBody,  new Color(0.25f, 0.77f, 1f, 1f));
        Label.LabelStyle bodyStyle  = new Label.LabelStyle(game.fontSmall, Color.WHITE);
        TextButton.TextButtonStyle btnStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);

        // Title: top-Y=36, h=50 → libgdxY = 768
        Label title = new Label("HOW TO PLAY", titleStyle);
        title.setSize(320f, 50f);
        title.setPosition((Constants.WORLD_WIDTH - 320f) / 2f, 768f);
        title.setAlignment(Align.center);
        stage.addActor(title);

        // Three panels
        String[] headers = {"TAP ANYWHERE", "AVOID DEBRIS", "COLLECT STARS"};
        String[] bodies  = {
            "Tap the screen to jump your pod one ring outward or inward. Direction alternates with each tap.",
            "Rotating debris arcs occupy ring segments. Touch any chunk and your run ends.",
            "Golden stars appear briefly on a ring. Jump to their ring to grab them for bonus score and currency."
        };
        for (int i = 0; i < 3; i++) {
            float py = PANEL_Y[i];

            Label h = new Label((i + 1) + ".  " + headers[i], headStyle);
            h.setSize(PANEL_W - 20f, 30f);
            h.setPosition((Constants.WORLD_WIDTH - PANEL_W) / 2f + 10f, py + PANEL_H - 36f);
            h.setAlignment(Align.left);
            stage.addActor(h);

            Label b = new Label(bodies[i], bodyStyle);
            b.setSize(PANEL_W - 20f, 80f);
            b.setPosition((Constants.WORLD_WIDTH - PANEL_W) / 2f + 10f, py + 8f);
            b.setAlignment(Align.left);
            b.setWrap(true);
            stage.addActor(b);
        }

        // "Tap anywhere" hint: top-Y=620, h=44 → libgdxY = 190
        Label hint = new Label("← Tap BACK or the button below to return →", bodyStyle);
        hint.setSize(440f, 44f);
        hint.setPosition((Constants.WORLD_WIDTH - 440f) / 2f, 190f);
        hint.setAlignment(Align.center);
        stage.addActor(hint);

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
        Gdx.gl.glClearColor(0f, 0.03f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(game.manager.get(BG, Texture.class),
                0f, 0f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // Panel border outlines
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeType.Line);
        sr.setColor(0.12f, 0.81f, 0.81f, 0.75f);
        float px = (Constants.WORLD_WIDTH - PANEL_W) / 2f;
        for (float py : PANEL_Y) {
            sr.rect(px, py, PANEL_W, PANEL_H);
        }
        sr.end();

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(new MainMenuScreen(game));
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
