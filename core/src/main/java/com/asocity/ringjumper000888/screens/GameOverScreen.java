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

public class GameOverScreen implements Screen {

    private static final String BG = "ui/game_over_screen.png";

    private static final float BTN_W     = 260f;
    private static final float BTN_H_RET = 56f;
    private static final float BTN_H_SEC = 52f;

    private final MainGame game;
    private final int finalScore;
    private final int starsEarned;   // `extra` parameter repurposed as stars earned this run

    private Stage stage;
    private StretchViewport viewport;

    /**
     * @param game        the main game instance
     * @param score       score achieved during the run
     * @param extra       stars earned during the run
     */
    public GameOverScreen(MainGame game, int score, int extra) {
        this.game       = game;
        this.finalScore = score;
        this.starsEarned = extra;

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }

        OrthographicCamera camera = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);

        persistResults();
        buildUI();
        registerInput();
    }

    // ── Persist score / stars / leaderboard ──────────────────────────────────

    private void persistResults() {
        // Update high score
        if (finalScore > game.highScore) {
            game.highScore = finalScore;
        }
        // Award stars
        game.starBalance += starsEarned;
        game.savePrefs();

        // Save to leaderboard
        LeaderboardScreen.addScore(finalScore);
    }

    // ── UI construction ───────────────────────────────────────────────────────

    private void buildUI() {
        boolean isNewBest = (finalScore >= game.highScore);

        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        Label.LabelStyle bodyStyle  = new Label.LabelStyle(game.fontBody,  Color.WHITE);
        Label.LabelStyle scoreStyle = new Label.LabelStyle(game.fontScore, new Color(0.25f, 0.77f, 1f, 1f)); // #40C4FF
        Label.LabelStyle accentStyle = new Label.LabelStyle(game.fontBody, new Color(1f, 0.93f, 0.3f, 1f));  // gold

        // GAME OVER title
        // Figma: topY=180, h=60 → libgdxY = 854 - 180 - 60 = 614
        Label gameOverLabel = new Label(isNewBest ? "NEW BEST!" : "GAME OVER", titleStyle);
        gameOverLabel.setSize(340f, 60f);
        gameOverLabel.setAlignment(Align.center);
        gameOverLabel.setPosition((Constants.WORLD_WIDTH - 340f) / 2f, 614f);
        stage.addActor(gameOverLabel);

        // SCORE value
        // Figma: topY=290, h=44 → libgdxY = 854 - 290 - 44 = 520
        Label scoreLabel = new Label(String.valueOf(finalScore), scoreStyle);
        scoreLabel.setSize(280f, 44f);
        scoreLabel.setAlignment(Align.center);
        scoreLabel.setPosition((Constants.WORLD_WIDTH - 280f) / 2f, 520f);
        stage.addActor(scoreLabel);

        // SCORE header above it
        Label scoreHdr = new Label("SCORE", bodyStyle);
        scoreHdr.setSize(280f, 32f);
        scoreHdr.setAlignment(Align.center);
        scoreHdr.setPosition((Constants.WORLD_WIDTH - 280f) / 2f, 552f);
        stage.addActor(scoreHdr);

        // BEST value
        // Figma: topY=348, h=36 → libgdxY = 854 - 348 - 36 = 470
        Label bestLabel = new Label("BEST: " + game.highScore, bodyStyle);
        bestLabel.setSize(240f, 36f);
        bestLabel.setAlignment(Align.center);
        bestLabel.setPosition((Constants.WORLD_WIDTH - 240f) / 2f, 470f);
        stage.addActor(bestLabel);

        // STARS COLLECTED
        // Figma: topY=400, h=36 → libgdxY = 854 - 400 - 36 = 418
        Label starsLabel = new Label("STARS: " + starsEarned, accentStyle);
        starsLabel.setSize(280f, 36f);
        starsLabel.setAlignment(Align.center);
        starsLabel.setPosition((Constants.WORLD_WIDTH - 280f) / 2f, 418f);
        stage.addActor(starsLabel);

        // RETRY button → new GameScreen
        // Figma: topY=530, h=56 → libgdxY = 854 - 530 - 56 = 268
        TextButton retryBtn = UiFactory.makeButton("RETRY", rectStyle, BTN_W, BTN_H_RET);
        retryBtn.setPosition((Constants.WORLD_WIDTH - BTN_W) / 2f, 268f);
        retryBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playClick();
                game.setScreen(new GameScreen(game, game.currentWorld));
            }
        });
        stage.addActor(retryBtn);

        // MENU button → MainMenuScreen
        // Figma: topY=606, h=52 → libgdxY = 854 - 606 - 52 = 196
        TextButton menuBtn = UiFactory.makeButton("MENU", rectStyle, BTN_W, BTN_H_SEC);
        menuBtn.setPosition((Constants.WORLD_WIDTH - BTN_W) / 2f, 196f);
        menuBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playBack();
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(menuBtn);
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

    private void playClick() {
        if (game.sfxEnabled && game.manager.isLoaded("sounds/sfx/sfx_button_click.ogg")) {
            game.manager.get("sounds/sfx/sfx_button_click.ogg", Sound.class).play(1.0f);
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
        game.playMusicOnce("sounds/music/music_game_over.ogg");
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
