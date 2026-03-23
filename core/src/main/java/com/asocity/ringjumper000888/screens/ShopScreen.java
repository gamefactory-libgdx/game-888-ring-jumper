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
 * Shop screen — browse and purchase the 6 pod skins.
 * Pod sprites come from sprites/vehicle/Ship_LVL_1..5.png.
 */
public class ShopScreen implements Screen {

    private static final String BG = "ui/shop_screen.png";

    // Skin sprites: skins 0-4 map to LVL 1-5; skin 5 reuses LVL 5
    private static final String[] SKIN_TEX = {
        "sprites/vehicle/Ship_LVL_1.png",
        "sprites/vehicle/Ship_LVL_2.png",
        "sprites/vehicle/Ship_LVL_3.png",
        "sprites/vehicle/Ship_LVL_4.png",
        "sprites/vehicle/Ship_LVL_5.png",
        "sprites/vehicle/Ship_LVL_5.png",
    };

    private static final String[] SKIN_NAMES = {
        "MICRO PROBE", "NEON ORB", "CRYSTAL SPHERE",
        "SOLAR FLARE", "DARK MATTER", "COSMIC EYE"
    };

    // Card grid layout (6 cards, 2 rows × 3 columns)
    // Card size: 130×160
    // Row 1: top-Y=160, h=160 → libgdxY = 854-160-160 = 534
    // Row 2: top-Y=340, h=160 → libgdxY = 854-340-160 = 354
    // Col X: left@24, center=(480-130)/2=175, right=480-130-24=326
    private static final float CARD_W = 130f;
    private static final float CARD_H = 160f;
    private static final float[] CARD_X = {24f, 175f, 326f, 24f, 175f, 326f};
    private static final float[] CARD_Y = {494f, 494f, 494f, 314f, 314f, 314f};

    private final MainGame game;
    private final boolean  fromGameOver;

    private Stage          stage;
    private StretchViewport viewport;
    private OrthographicCamera camera;
    private ShapeRenderer  sr;

    private int    selectedSkin;  // card currently highlighted
    private String toastText  = "";
    private float  toastTimer = 0f;

    // Action button (bottom centre) reference so we can re-label it
    private TextButton actionBtn;
    private Label      actionBtnLabel;

    public ShopScreen(MainGame game) {
        this(game, false);
    }

    public ShopScreen(MainGame game, boolean fromGameOver) {
        this.game         = game;
        this.fromGameOver = fromGameOver;
        this.selectedSkin = game.equippedSkin;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        // Load all skin textures
        for (String path : SKIN_TEX) {
            if (!game.manager.isLoaded(path)) game.manager.load(path, Texture.class);
        }
        if (!game.manager.isLoaded(BG)) game.manager.load(BG, Texture.class);
        game.manager.finishLoading();

        buildUi();
        Gdx.input.setInputProcessor(stage);
        game.playMusic("sounds/music/music_menu.ogg");
    }

    private void buildUi() {
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        Label.LabelStyle starStyle  = new Label.LabelStyle(game.fontIcon,
                new Color(1f, 0.85f, 0.15f, 1f));
        TextButton.TextButtonStyle btnStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);

        // SHOP title: top-Y=36, h=50 → libgdxY = 768
        Label title = new Label("POD SKINS", titleStyle);
        title.setSize(240f, 50f);
        title.setPosition((Constants.WORLD_WIDTH - 240f) / 2f, 768f);
        title.setAlignment(Align.center);
        stage.addActor(title);

        // Star balance: top-Y=36, h=40, x=right@20 → libgdxY = 778, x = 480-140-20 = 320
        final Label starLabel = new Label("* " + game.starBalance, starStyle);
        starLabel.setSize(140f, 40f);
        starLabel.setPosition(Constants.WORLD_WIDTH - 140f - 20f, 778f);
        starLabel.setName("starLabel");
        starLabel.setAlignment(Align.right);
        stage.addActor(starLabel);

        // Card hit areas
        for (int i = 0; i < Constants.SKIN_COUNT; i++) {
            final int si = i;
            Actor hit = new Actor();
            hit.setBounds(CARD_X[i], CARD_Y[i], CARD_W, CARD_H);
            hit.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    selectedSkin = si;
                    sfxClick();
                }
            });
            stage.addActor(hit);
        }

        // SELECT/EQUIP/BUY action button: top-Y=560, h=52 → libgdxY = 242
        actionBtn = UiFactory.makeCentredButton(actionLabel(), btnStyle,
                260f, 52f, 172f);
        actionBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                onActionTapped(starLabel);
            }
        });
        stage.addActor(actionBtn);

        // BACK: top-Y=790, h=44 → libgdxY = 20
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

    private String actionLabel() {
        boolean owned = (game.ownedSkins & (1 << selectedSkin)) != 0;
        if (owned) {
            return selectedSkin == game.equippedSkin ? "EQUIPPED" : "EQUIP";
        }
        return "BUY  " + Constants.SKIN_PRICES[selectedSkin] + " \u2605";
    }

    private void onActionTapped(Label starLabel) {
        boolean owned = (game.ownedSkins & (1 << selectedSkin)) != 0;
        if (owned) {
            if (selectedSkin != game.equippedSkin) {
                game.equippedSkin = selectedSkin;
                game.savePrefs();
                sfxClick();
            }
        } else {
            int price = Constants.SKIN_PRICES[selectedSkin];
            if (game.starBalance >= price) {
                game.starBalance -= price;
                game.ownedSkins  |= (1 << selectedSkin);
                game.equippedSkin = selectedSkin;
                game.savePrefs();
                starLabel.setText("\u2605 " + game.starBalance);
                showToast("PURCHASED! " + SKIN_NAMES[selectedSkin]);
                if (game.sfxEnabled)
                    game.manager.get("sounds/sfx/sfx_level_complete.ogg", Sound.class).play(1f);
            } else {
                showToast("NOT ENOUGH STARS (" + price + " \u2605 NEEDED)");
                if (game.sfxEnabled)
                    game.manager.get("sounds/sfx/sfx_button_back.ogg", Sound.class).play(1f);
            }
        }
    }

    private void showToast(String msg) {
        toastText  = msg;
        toastTimer = 2.5f;
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

        // Update action button label (selection may change)
        actionBtn.setText(actionLabel());

        Gdx.gl.glClearColor(0f, 0.03f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(game.manager.get(BG, Texture.class),
                0f, 0f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // Draw card backgrounds and borders
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeType.Filled);
        for (int i = 0; i < Constants.SKIN_COUNT; i++) {
            boolean isSelected = (i == selectedSkin);
            sr.setColor(0.04f, 0.07f, 0.18f, isSelected ? 0.95f : 0.80f);
            sr.rect(CARD_X[i], CARD_Y[i], CARD_W, CARD_H);
        }
        sr.end();

        sr.begin(ShapeType.Line);
        for (int i = 0; i < Constants.SKIN_COUNT; i++) {
            boolean isSelected = (i == selectedSkin);
            boolean owned      = (game.ownedSkins & (1 << i)) != 0;
            if (isSelected) {
                sr.setColor(0.25f, 0.77f, 1.0f, 1.0f); // highlight teal
            } else if (owned) {
                sr.setColor(0.20f, 0.65f, 0.20f, 0.70f); // owned green
            } else {
                sr.setColor(0.50f, 0.50f, 0.55f, 0.55f); // locked grey
            }
            sr.rect(CARD_X[i], CARD_Y[i], CARD_W, CARD_H);
        }
        sr.end();

        // Draw skin sprites and labels
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        for (int i = 0; i < Constants.SKIN_COUNT; i++) {
            drawCardContents(i);
        }
        if (toastTimer > 0f) {
            float alpha = Math.min(1f, toastTimer * 2f);
            game.fontSmall.setColor(1f, 0.85f, 0.15f, alpha);
            game.fontSmall.draw(game.batch, toastText,
                    0f, 220f, Constants.WORLD_WIDTH, Align.center, false);
            game.fontSmall.setColor(Color.WHITE);
        }
        game.batch.end();

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void drawCardContents(int i) {
        float cx = CARD_X[i];
        float cy = CARD_Y[i];
        boolean owned   = (game.ownedSkins & (1 << i)) != 0;
        boolean equipped = (i == game.equippedSkin);

        // Ship sprite (centered in upper ~70% of card)
        Texture tex = game.manager.get(SKIN_TEX[i], Texture.class);
        float spriteSize = 52f;
        float spriteX = cx + (CARD_W - spriteSize) / 2f;
        float spriteY = cy + CARD_H * 0.35f;
        float alpha = owned ? 1.0f : 0.45f;
        game.batch.setColor(1f, 1f, 1f, alpha);
        game.batch.draw(tex, spriteX, spriteY, spriteSize, spriteSize);
        game.batch.setColor(Color.WHITE);

        // Skin name
        game.fontSmall.setColor(Color.WHITE);
        game.fontSmall.draw(game.batch, SKIN_NAMES[i],
                cx, cy + 50f, CARD_W, Align.center, false);

        // Status / price
        if (equipped) {
            game.fontSmall.setColor(0.25f, 0.77f, 1.00f, 1f);
            game.fontSmall.draw(game.batch, "EQUIPPED",
                    cx, cy + 36f, CARD_W, Align.center, false);
        } else if (owned) {
            game.fontSmall.setColor(0.35f, 0.90f, 0.35f, 1f);
            game.fontSmall.draw(game.batch, "OWNED",
                    cx, cy + 36f, CARD_W, Align.center, false);
        } else {
            game.fontSmall.setColor(1f, 0.85f, 0.15f, 1f);
            String priceStr = Constants.SKIN_PRICES[i] == 0 ? "FREE" : Constants.SKIN_PRICES[i] + " \u2605";
            game.fontSmall.draw(game.batch, priceStr,
                    cx, cy + 36f, CARD_W, Align.center, false);
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
