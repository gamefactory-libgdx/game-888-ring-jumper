package com.asocity.ringjumper000888;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.asocity.ringjumper000888.screens.MainMenuScreen;

public class MainGame extends Game {

    // ── Core singletons ──────────────────────────────────────────────────────
    public SpriteBatch  batch;
    public AssetManager manager;
    public Preferences  prefs;

    // ── Fonts ────────────────────────────────────────────────────────────────
    /** Title / score — HydrogenWhiskey.otf */
    public BitmapFont fontTitle;
    /** Body / buttons — Orbitron-Regular.ttf */
    public BitmapFont fontBody;
    /** Small labels — Orbitron-Regular.ttf smaller */
    public BitmapFont fontSmall;
    public BitmapFont fontIcon;  // Roboto with gear+star glyphs
    /** Large score display — HydrogenWhiskey.otf large */
    public BitmapFont fontScore;

    // ── Audio state ──────────────────────────────────────────────────────────
    public boolean musicEnabled = true;
    public boolean sfxEnabled   = true;
    public boolean vibrationEnabled = true;
    public Music   currentMusic = null;

    // ── Persistent game state (mirrored from prefs) ──────────────────────────
    public int starBalance;
    public int ownedSkins;       // bitmask; bit N = skin N owned
    public int equippedSkin;
    public int highScore;
    public int currentWorld;
    public int unlockedWorlds;   // bitmask

    // ── Lifecycle ────────────────────────────────────────────────────────────

    @Override
    public void create() {
        batch   = new SpriteBatch();
        manager = new AssetManager();
        prefs   = Gdx.app.getPreferences(Constants.PREFS_NAME);

        generateFonts();
        loadAssets();
        loadPrefs();

        setScreen(new MainMenuScreen(this));
    }

    // ── Font generation ──────────────────────────────────────────────────────

    private void generateFonts() {
        FreeTypeFontGenerator titleGen = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/HydrogenWhiskey.otf"));
        FreeTypeFontGenerator bodyGen  = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Orbitron-Regular.ttf"));

        FreeTypeFontParameter p = new FreeTypeFontParameter();

        // fontTitle — 48 pt with thick outline
        p.size        = 48;
        p.borderWidth = 3;
        p.borderColor = new Color(0f, 0f, 0f, 0.85f);
        fontTitle = titleGen.generateFont(p);

        // fontScore — 64 pt
        p.size        = 64;
        p.borderWidth = 3;
        p.borderColor = new Color(0f, 0f, 0f, 0.85f);
        fontScore = titleGen.generateFont(p);

        // fontBody — 28 pt
        p.size        = 28;
        p.borderWidth = 2;
        p.borderColor = new Color(0f, 0f, 0f, 0.85f);
        fontBody = bodyGen.generateFont(p);

        // fontSmall — 18 pt
        p.size        = 18;
        p.borderWidth = 1;
        p.borderColor = new Color(0f, 0f, 0f, 0.85f);
        fontSmall = bodyGen.generateFont(p);

        // fontIcon - fresh param, Roboto with special symbols
        FreeTypeFontGenerator iconGen = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Roboto-Regular.ttf"));
        FreeTypeFontParameter iconP = new FreeTypeFontParameter();
        iconP.size = 28;
        iconP.borderWidth = 1;
        iconP.borderColor = new Color(0f, 0f, 0f, 0.85f);
        iconP.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "⚙★";
        fontIcon = iconGen.generateFont(iconP);
        iconGen.dispose();
        titleGen.dispose();
        bodyGen.dispose();
    }

    // ── Asset loading ────────────────────────────────────────────────────────

    private void loadAssets() {
        // Music (3 standard pipeline tracks)
        manager.load("sounds/music/music_menu.ogg",      Music.class);
        manager.load("sounds/music/music_gameplay.ogg",  Music.class);
        manager.load("sounds/music/music_game_over.ogg", Music.class);

        // SFX
        manager.load("sounds/sfx/sfx_button_click.ogg",   Sound.class);
        manager.load("sounds/sfx/sfx_button_back.ogg",    Sound.class);
        manager.load("sounds/sfx/sfx_toggle.ogg",         Sound.class);
        manager.load("sounds/sfx/sfx_coin.ogg",           Sound.class);
        manager.load("sounds/sfx/sfx_jump.ogg",           Sound.class);
        manager.load("sounds/sfx/sfx_hit.ogg",            Sound.class);
        manager.load("sounds/sfx/sfx_game_over.ogg",      Sound.class);
        manager.load("sounds/sfx/sfx_level_complete.ogg", Sound.class);
        manager.load("sounds/sfx/sfx_power_up.ogg",       Sound.class);

        // UI buttons
        manager.load("ui/buttons/button_rectangle_depth_gradient.png",
                com.badlogic.gdx.graphics.Texture.class);
        manager.load("ui/buttons/button_rectangle_depth_flat.png",
                com.badlogic.gdx.graphics.Texture.class);
        manager.load("ui/buttons/button_round_depth_gradient.png",
                com.badlogic.gdx.graphics.Texture.class);
        manager.load("ui/buttons/button_round_depth_flat.png",
                com.badlogic.gdx.graphics.Texture.class);

        manager.finishLoading();
    }

    // ── Prefs ────────────────────────────────────────────────────────────────

    public void loadPrefs() {
        musicEnabled     = prefs.getBoolean(Constants.PREF_MUSIC,     true);
        sfxEnabled       = prefs.getBoolean(Constants.PREF_SFX,       true);
        vibrationEnabled = prefs.getBoolean(Constants.PREF_VIBRATION, true);
        starBalance      = prefs.getInteger(Constants.PREF_STARS_BALANCE,   0);
        ownedSkins       = prefs.getInteger(Constants.PREF_OWNED_SKINS,     1);
        equippedSkin     = prefs.getInteger(Constants.PREF_SKIN,            0);
        highScore        = prefs.getInteger(Constants.PREF_HIGH_SCORE,      0);
        currentWorld     = prefs.getInteger(Constants.PREF_CURRENT_WORLD,   0);
        unlockedWorlds   = prefs.getInteger(Constants.PREF_UNLOCKED_WORLDS, 1);
    }

    public void savePrefs() {
        prefs.putBoolean(Constants.PREF_MUSIC,           musicEnabled);
        prefs.putBoolean(Constants.PREF_SFX,             sfxEnabled);
        prefs.putBoolean(Constants.PREF_VIBRATION,       vibrationEnabled);
        prefs.putInteger(Constants.PREF_STARS_BALANCE,   starBalance);
        prefs.putInteger(Constants.PREF_OWNED_SKINS,     ownedSkins);
        prefs.putInteger(Constants.PREF_SKIN,            equippedSkin);
        prefs.putInteger(Constants.PREF_HIGH_SCORE,      highScore);
        prefs.putInteger(Constants.PREF_CURRENT_WORLD,   currentWorld);
        prefs.putInteger(Constants.PREF_UNLOCKED_WORLDS, unlockedWorlds);
        prefs.flush();
    }

    // ── Music helpers ─────────────────────────────────────────────────────────

    /** Play a looping music track. No-op if the same track is already playing. */
    public void playMusic(String path) {
        Music requested = manager.get(path, Music.class);
        if (requested == currentMusic && currentMusic.isPlaying()) return;
        if (currentMusic != null) currentMusic.stop();
        currentMusic = requested;
        currentMusic.setLooping(true);
        currentMusic.setVolume(0.7f);
        if (musicEnabled) currentMusic.play();
    }

    /** Play a one-shot music track (e.g. game over jingle). Never loops. */
    public void playMusicOnce(String path) {
        if (currentMusic != null) currentMusic.stop();
        currentMusic = manager.get(path, Music.class);
        currentMusic.setLooping(false);
        currentMusic.setVolume(0.7f);
        if (musicEnabled) currentMusic.play();
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
    }

    // ── Dispose ──────────────────────────────────────────────────────────────

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        manager.dispose();
        fontTitle.dispose();
        fontBody.dispose();
        if (fontIcon != null) fontIcon.dispose();
        fontSmall.dispose();
        fontScore.dispose();
    }
}
