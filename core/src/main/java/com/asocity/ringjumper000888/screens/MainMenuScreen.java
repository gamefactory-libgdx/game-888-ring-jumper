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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
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

public class MainMenuScreen implements Screen {

    private static final String BG = "ui/main_menu_screen.png";

    private final MainGame game;
    private Stage stage;
    private StretchViewport viewport;

    // ── button sizes from Figma brief ────────────────────────────────────────
    private static final float BTN_MAIN_W  = 260f;
    private static final float BTN_MAIN_H  = 56f;
    private static final float BTN_SEC_W   = 260f;
    private static final float BTN_SEC_H   = 52f;
    private static final float BTN_ICON_SZ = 56f;

    public MainMenuScreen(MainGame game) {
        this.game = game;

        // Ensure background texture loaded
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
        TextButton.TextButtonStyle rectStyle  = UiFactory.makeRectStyle(game.manager, game.fontBody);
        TextButton.TextButtonStyle roundStyle = UiFactory.makeRoundStyle(game.manager, game.fontBody);
        TextButton.TextButtonStyle roundIconStyle = UiFactory.makeRoundStyle(game.manager, game.fontIcon);

        // Title label
        // Figma: topY=80, h=60 → libgdxY = 854 - 80 - 60 = 714
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        Label titleLabel = new Label("RING JUMPER", titleStyle);
        titleLabel.setSize(360f, 60f);
        titleLabel.setAlignment(Align.center);
        titleLabel.setPosition((Constants.WORLD_WIDTH - 360f) / 2f, 714f);
        stage.addActor(titleLabel);

        // PLAY → WorldsScreen
        // Figma: topY=420, h=56 → libgdxY = 378
        TextButton playBtn = UiFactory.makeButton("PLAY", rectStyle, BTN_MAIN_W, BTN_MAIN_H);
        playBtn.setPosition((Constants.WORLD_WIDTH - BTN_MAIN_W) / 2f, 378f);
        playBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playClick();
                game.setScreen(new WorldsScreen(game));
            }
        });
        stage.addActor(playBtn);

        // SHOP → ShopScreen
        // Figma: topY=496, h=52 → libgdxY = 306
        TextButton shopBtn = UiFactory.makeButton("SHOP", rectStyle, BTN_SEC_W, BTN_SEC_H);
        shopBtn.setPosition((Constants.WORLD_WIDTH - BTN_SEC_W) / 2f, 306f);
        shopBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playClick();
                game.setScreen(new ShopScreen(game));
            }
        });
        stage.addActor(shopBtn);

        // SCORES → LeaderboardScreen
        // Figma: topY=568, h=52 → libgdxY = 234
        TextButton scoresBtn = UiFactory.makeButton("SCORES", rectStyle, BTN_SEC_W, BTN_SEC_H);
        scoresBtn.setPosition((Constants.WORLD_WIDTH - BTN_SEC_W) / 2f, 234f);
        scoresBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playClick();
                game.setScreen(new LeaderboardScreen(game));
            }
        });
        stage.addActor(scoresBtn);

        // HOW TO PLAY → HowToPlayScreen
        // Figma: topY=640, h=52 → libgdxY = 162
        TextButton howBtn = UiFactory.makeButton("HOW TO PLAY", rectStyle, BTN_SEC_W, BTN_SEC_H);
        howBtn.setPosition((Constants.WORLD_WIDTH - BTN_SEC_W) / 2f, 162f);
        howBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playClick();
                game.setScreen(new HowToPlayScreen(game));
            }
        });
        stage.addActor(howBtn);

        // SETTINGS (round icon, bottom-right)
        // Figma: topY=790, x=right@20, size=56x56 → x=404, libgdxY=8
        TextButton settingsBtn = UiFactory.makeButton("⚙", roundIconStyle, BTN_ICON_SZ, BTN_ICON_SZ);
        settingsBtn.setPosition(Constants.WORLD_WIDTH - 20f - BTN_ICON_SZ, 8f);
        settingsBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playClick();
                game.setScreen(new SettingsScreen(game));
            }
        });
        stage.addActor(settingsBtn);
    }

    // ── Input ─────────────────────────────────────────────────────────────────

    private void registerInput() {
        InputMultiplexer mux = new InputMultiplexer(stage, new InputAdapter() {
            @Override public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
                    Gdx.app.exit();
                    return true;
                }
                return false;
            }
        });
        Gdx.input.setInputProcessor(mux);
    }

    // ── Sound helper ──────────────────────────────────────────────────────────

    private void playClick() {
        if (game.sfxEnabled && game.manager.isLoaded("sounds/sfx/sfx_button_click.ogg")) {
            game.manager.get("sounds/sfx/sfx_button_click.ogg", Sound.class).play(1.0f);
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
        Gdx.gl.glClearColor(0f, 0.031f, 0.078f, 1f); // #000814
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
