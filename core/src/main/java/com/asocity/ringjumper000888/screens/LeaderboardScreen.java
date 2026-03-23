package com.asocity.ringjumper000888.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Preferences;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.asocity.ringjumper000888.Constants;
import com.asocity.ringjumper000888.MainGame;
import com.asocity.ringjumper000888.UiFactory;

public class LeaderboardScreen implements Screen {

    private static final String BG = "ui/leaderboard_screen.png";

    // Row layout from Figma (topY starting at 150, each row 50px, spacing 54)
    // libgdxY = 854 - topY - rowH
    private static final float ROW_H = 50f;
    // topY values: 150, 204, 258, 312, 366, 420, 474, 528, 582, 636
    private static final float[] ROW_Y = {
        854f - 150f - ROW_H,   // 654
        854f - 204f - ROW_H,   // 600
        854f - 258f - ROW_H,   // 546
        854f - 312f - ROW_H,   // 492
        854f - 366f - ROW_H,   // 438
        854f - 420f - ROW_H,   // 384
        854f - 474f - ROW_H,   // 330
        854f - 528f - ROW_H,   // 276
        854f - 582f - ROW_H,   // 222
        854f - 636f - ROW_H,   // 168
    };

    private final MainGame game;
    private Stage stage;
    private StretchViewport viewport;

    public LeaderboardScreen(MainGame game) {
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

    // ── Static leaderboard persistence ───────────────────────────────────────

    /**
     * Appends {@code score} to the leaderboard and keeps only the top-10.
     * Can be called from any screen (e.g. GameOverScreen) without an instance.
     */
    public static void addScore(int score) {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        Array<Integer> scores = loadScores(prefs);
        scores.add(score);
        sortDescending(scores);
        if (scores.size > Constants.LEADERBOARD_MAX_ENTRIES) {
            scores.removeRange(Constants.LEADERBOARD_MAX_ENTRIES, scores.size - 1);
        }
        saveScores(prefs, scores);
    }

    // ── Private serialization helpers ─────────────────────────────────────────

    private static Array<Integer> loadScores(Preferences prefs) {
        Array<Integer> result = new Array<>();
        String stored = prefs.getString(Constants.PREF_LEADERBOARD, "");
        if (stored == null || stored.isEmpty()) return result;
        for (String token : stored.split(",")) {
            token = token.trim();
            if (!token.isEmpty()) {
                try {
                    result.add(Integer.parseInt(token));
                } catch (NumberFormatException e) {
                    Gdx.app.log("Leaderboard", "Skipping invalid token: " + token);
                }
            }
        }
        return result;
    }

    private static void saveScores(Preferences prefs, Array<Integer> scores) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < scores.size; i++) {
            if (i > 0) sb.append(',');
            sb.append(scores.get(i));
        }
        prefs.putString(Constants.PREF_LEADERBOARD, sb.toString());
        prefs.flush();
    }

    /** Simple insertion-sort descending — list is ≤10 entries, so O(n²) is fine. */
    private static void sortDescending(Array<Integer> arr) {
        for (int i = 1; i < arr.size; i++) {
            int key = arr.get(i);
            int j = i - 1;
            while (j >= 0 && arr.get(j) < key) {
                arr.set(j + 1, arr.get(j));
                j--;
            }
            arr.set(j + 1, key);
        }
    }

    // ── UI construction ───────────────────────────────────────────────────────

    private void buildUI() {
        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        Label.LabelStyle bodyStyle  = new Label.LabelStyle(game.fontBody,  Color.WHITE);
        Label.LabelStyle goldStyle  = new Label.LabelStyle(game.fontBody,  new Color(1f, 0.85f, 0.3f, 1f));
        Label.LabelStyle dimStyle   = new Label.LabelStyle(game.fontSmall, new Color(0.8f, 0.8f, 0.8f, 1f));

        // Title
        // Figma: topY=52, h=50 → libgdxY = 854 - 52 - 50 = 752
        Label title = new Label("LEADERBOARD", titleStyle);
        title.setSize(340f, 50f);
        title.setAlignment(Align.center);
        title.setPosition((Constants.WORLD_WIDTH - 340f) / 2f, 752f);
        stage.addActor(title);

        // Load scores
        Array<Integer> scores = loadScores(game.prefs);

        // Rank medals for top-3
        String[] medals = {"#1", "#2", "#3"};

        for (int i = 0; i < ROW_Y.length; i++) {
            float y = ROW_Y[i];

            // Rank label (left side)
            String rankText = (i < medals.length) ? medals[i] : "#" + (i + 1);
            Label.LabelStyle rankStyle = (i < 3) ? goldStyle : dimStyle;
            Label rankLabel = new Label(rankText, rankStyle);
            rankLabel.setSize(80f, ROW_H);
            rankLabel.setAlignment(Align.center);
            rankLabel.setPosition(50f, y);
            stage.addActor(rankLabel);

            // Score value (right side)
            String scoreText = (i < scores.size) ? String.valueOf(scores.get(i)) : "---";
            Label scoreLabel = new Label(scoreText, (i < 3) ? goldStyle : bodyStyle);
            scoreLabel.setSize(260f, ROW_H);
            scoreLabel.setAlignment(Align.right);
            scoreLabel.setPosition(Constants.WORLD_WIDTH - 50f - 260f, y);
            stage.addActor(scoreLabel);
        }

        // BACK button
        // Figma: topY=790, x=left@20, h=44 → libgdxY = 20
        TextButton backBtn = UiFactory.makeButton("BACK", rectStyle, 120f, 44f);
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

    // ── Sound helper ──────────────────────────────────────────────────────────

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
