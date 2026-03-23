<function_calls>
<invoke name="Bash">
<parameter name="command">find projects/game-884-arkanoid-duel/core -name "*.java" | sort</parameter>
</invoke>
</function_calls>
<function_calls>
<invoke name="Bash">
<parameter name="command">find projects/game-885-dino-pairs/core -name "*.java" | sort</parameter>
</invoke>
</function_calls>
<function_calls>
<invoke name="Bash">
<parameter name="command">cat projects/game-885-dino-pairs/core/src/main/java/com/dinopairs885/app/MainGame.java</parameter>
</invoke>
</function_calls>
<function_calls>
<invoke name="Bash">
<parameter name="command">cat projects/game-885-dino-pairs/core/src/main/java/com/dinopairs885/app/GameScreen.java | head -100</parameter>
</invoke>
</function_calls>
<function_calls>
<invoke name="Bash">
<parameter name="command">ls projects/game-885-dino-pairs/core/src/main/java/com/dinopairs885/app/</parameter>
</invoke>
</function_calls>
<function_calls>
<invoke name="Bash">
<parameter name="command">cat projects/game-885-dino-pairs/core/src/main/java/com/dinopairs885/app/Constants.java</parameter>
</invoke>
</function_calls>
<function_calls>
<invoke name="Bash">
<parameter name="command">ls projects/game-888-ring-jumper/ 2>/dev/null || echo "not found"</parameter>
</invoke>
</function_calls>
<function_calls>
<invoke name="Bash">
<parameter name="command">find projects/game-888-ring-jumper -type f 2>/dev/null | head -20 || echo "directory empty or missing"</parameter>
</invoke>
</function_calls>

Now I have enough context. Let me write the GAME_SPEC.md.
<function_calls>
<invoke name="Write">
<parameter name="path">/home/kaliuzhnyi/asocity/projects/game-888-ring-jumper/GAME_SPEC.md</parameter>
<parameter name="content"># GAME_SPEC.md — Ring Jumper

**Game ID:** game-888  
**Title:** Ring Jumper  
**Package:** `com.ringjumper888000.app` *(6-digit suffix assigned by pipeline)*  
**Main Class:** `com.ringjumper888000.app.MainGame`  
**Java source root:** `core/src/main/java/com/ringjumper888000/app/`  
**Genre:** Reflex-Arcade  
**Platform:** Android (LibGDX, portrait, API 21+)  
**Viewport:** `FitViewport(360, 640)` logical pixels  

---

## 1. Screen Inventory

| # | Java Class | Purpose | Transitions Out |
|---|-----------|---------|-----------------|
| 1 | `MainMenuScreen` | Entry point; animated background, nav buttons | → `WorldsScreen` (Play), → `ShopScreen` (Shop), → `LeaderboardScreen` (Scores), → `HowToPlayScreen` (How to Play), → `SettingsScreen` (Settings) |
| 2 | `WorldsScreen` | Select/unlock one of 4 themed worlds | → `GameScreen` (tap unlocked world), → `MainMenuScreen` (Back) |
| 3 | `GameScreen` | Core gameplay loop; pod orbits, tap to jump rings | → `GameOverScreen` (death), → `MainMenuScreen` (pause → Menu), → `GameScreen` (pause → Restart) |
| 4 | `GameOverScreen` | Score recap; persists high score + stars | → `GameScreen` (Retry, same world), → `ShopScreen` (Shop), → `MainMenuScreen` (Menu) |
| 5 | `ShopScreen` | Browse and purchase 6 pod skins | → `MainMenuScreen` (Back), → `GameOverScreen` (Back, if entered from there) |
| 6 | `LeaderboardScreen` | Local top-10 high scores, sorted descending | → `MainMenuScreen` (Back) |
| 7 | `SettingsScreen` | Music / SFX / Vibration toggles | → `MainMenuScreen` (Back) |
| 8 | `HowToPlayScreen` | 3-panel illustrated tutorial | → `MainMenuScreen` (Back) |

---

## 2. Screen Flow

```
MainMenuScreen
  ├─ [Play]        → WorldsScreen
  │                    └─ [tap unlocked world] → GameScreen
  │                                                └─ [death] → GameOverScreen
  │                                                               ├─ [Retry]  → GameScreen (same world)
  │                                                               ├─ [Shop]   → ShopScreen
  │                                                               └─ [Menu]   → MainMenuScreen
  ├─ [Shop]        → ShopScreen → MainMenuScreen
  ├─ [Scores]      → LeaderboardScreen → MainMenuScreen
  ├─ [How to Play] → HowToPlayScreen → MainMenuScreen
  └─ [Settings]    → SettingsScreen → MainMenuScreen
```

Back gesture during `GameScreen` opens a `PauseOverlay` (not a separate screen — an in-screen overlay rendered over gameplay).

---

## 3. Game Objects — Classes, Fields, Methods

### 3.1 `MainGame extends Game`

```java
// Fields
SpriteBatch batch;
AssetManager assets;
Preferences prefs;          // name: "ring_jumper_prefs"
BitmapFont fontPrimary;     // font1.ttf, size 32
BitmapFont fontSecondary;   // font2.ttf, size 20
BitmapFont fontSmall;       // Roboto-Regular.ttf, size 16
Music menuMusic;
Music gameMusic;
int currentWorld;           // 0–3, loaded from PREF_CURRENT_WORLD
int starBalance;
int ownedSkins;             // bitmask
int equippedSkin;
int highScore;
int unlockedWorlds;         // bitmask

// Methods
void create()               // load assets, init prefs, show MainMenuScreen
void loadPrefs()
void savePrefs()
void playMusic(Music track)
void stopMusic()
```

### 3.2 `Constants`

See §6 for all values. Static final fields only; no instances.

### 3.3 `MainMenuScreen implements Screen`

```java
// Fields
MainGame game;
Texture background;         // bg_world_{currentWorld}.png
Texture logo;               // ui_logo.png
Texture[] buttons;          // ui_btn_play, ui_btn_generic (×4), ui_btn_generic (×4)
float bgRotation;           // accumulated rotation angle for gas-giant decoration
float starfieldOffset;      // parallax scroll offset

// Methods
void show()                 // start menuMusic
void render(float delta)    // draw background, logo, buttons; handle input
void handleInput()          // check tap against button bounds
```

### 3.4 `WorldsScreen implements Screen`

```java
// Fields
MainGame game;
int selectedWorld;          // highlighted card

// Methods
void show()
void render(float delta)
void drawWorldCard(int worldIndex, float x, float y)
boolean isWorldUnlocked(int worldIndex)  // checks unlockedWorlds bitmask
void showLockedToast(int requiredScore)
```

### 3.5 `GameScreen implements Screen`

```java
// Fields
MainGame game;
int worldIndex;

// Pod state
int   podRing;              // 1–4
float podAngle;             // degrees, 0 = right, increases counter-clockwise
float podAngularSpeed;      // degrees/second, from WORLD_POD_SPEEDS[worldIndex]
int   jumpDirection;        // +1 = outward, -1 = inward; flips each tap + at boundaries

// Debris
Array<DebrisField> debrisFields;

// Stars
Array<StarCollectible> stars;
float starSpawnTimer;       // counts down to next spawn
float starSpawnInterval;    // current interval (scales with difficulty)

// Score & currency
int   score;
float scoreAccumulator;     // fractional seconds
int   starsThisRun;

// Difficulty
float elapsedTime;          // total seconds since run start

// State
boolean paused;
boolean dead;
float   deathTimer;         // counts up to DEATH_ANIM_DURATION

// Rendering
ShapeRenderer shapeRenderer;
SpriteBatch    batch;
Texture        podTexture;  // pod_skin_{equippedSkin}.png
Texture        planetTexture; // planet_world_{worldIndex}.png
Texture        backgroundTexture; // bg_world_{worldIndex}.png

// Methods
void show()
void render(float delta)
void update(float delta)
void drawBackground()
void drawRings()            // 4 ellipses via ShapeRenderer
void drawPlanet()
void drawDebris()
void drawStars()
void drawPod()
void drawHUD()
void drawPauseOverlay()
void handleInput()
void onTap()                // jump pod ring; flip jumpDirection; clamp to [1,4]; enforce boundary direction
void spawnDebrisField()     // pick ring, arc span, rotation speed
void spawnStar()
void updateDifficulty()     // called each frame; adjusts fields per §8.2
void checkCollisions()      // pod vs debris chunks; pod vs star collectibles
void onPodDeath()
void onStarCollected(StarCollectible s)
boolean ellipseContainsAngle(int ring, float angle, float podRadius) // collision helper
```

### 3.6 `DebrisField`

```java
// Fields
int     ring;               // 1–4
float   arcStartAngle;      // degrees
float   arcSpanDegrees;     // current arc width
float   rotationSpeed;      // degrees/second (may be negative = opposite direction)
float   currentAngle;       // offset applied to arcStartAngle, accumulates each frame
int     chunkCount;         // 2–5
Array<DebrisChunk> chunks;

// Methods
void update(float delta)
void draw(SpriteBatch batch)
boolean checkCollision(float podRing, float podAngle) // circle-circle per chunk
```

### 3.7 `DebrisChunk`

```java
// Fields
Texture texture;            // debris_rock.png or debris_ice.png
float   relativeAngle;      // offset within arc
float   x, y;               // world coords, computed from ring + angle
float   radius;             // DEBRIS_CHUNK_RADIUS (9 px)
```

### 3.8 `StarCollectible`

```java
// Fields
int   ring;                 // 1–4
float angle;                // spawn angle, co-rotates with pod speed
float lifetime;             // counts down from current star visibility duration
float alpha;                // oscillates for blink effect
float x, y;                 // world coords
Texture texture;            // star_collectible.png
float radius;               // STAR_RADIUS (11 px)

// Methods
void update(float delta)
void draw(SpriteBatch batch)
boolean isExpired()
boolean overlaps(float px, float py, float podRadius)
```

### 3.9 `GameOverScreen implements Screen`

```java
// Fields
MainGame game;
int finalScore;
int starsEarned;
int worldIndex;
boolean newBestScore;

// Methods
void show()               // persist score, add stars, update leaderboard
void render(float delta)
void persistResults()     // update PREF_HIGH_SCORE, PREF_STARS_BALANCE, PREF_LEADERBOARD
```

### 3.10 `ShopScreen implements Screen`

```java
// Fields
MainGame game;
boolean fromGameOver;     // controls Back destination

// Methods
void show()
void render(float delta)
void drawSkinCard(int skinIndex, float x, float y)
void onCardTap(int skinIndex)
void showConfirmPopup(int skinIndex, int price)
void purchase(int skinIndex, int price)
```

### 3.11 `LeaderboardScreen implements Screen`

```java
// Fields
MainGame game;
Array<LeaderboardEntry> entries;  // up to 10, sorted desc

// Methods
void show()               // deserialise PREF_LEADERBOARD
void render(float delta)
```

### 3.12 `LeaderboardEntry`

```java
int score;
int worldIndex;
// Serialise: JSON object {"score":N,"world":N}
```

### 3.13 `SettingsScreen implements Screen`

```java
// Methods
void show()
void render(float delta)
void onToggle(String prefKey, boolean newValue)  // save + apply immediately
```

### 3.14 `HowToPlayScreen implements Screen`

```java
// Fields
int panelIndex;  // 0–2; advance with arrow taps or swipe

// Methods
void show()
void render(float delta)
void drawPanel(int index)  // draw caption + diagram for panel 0/1/2
```

---

## 4. Coordinate System & Ring Geometry

- Origin: **screen centre** `(180, 320)` in logical pixels.
- Pod position from `(ring, angle)`:

```
halfW = RING_HALF_WIDTHS[ring-1]          // {130, 185, 240, 295}
halfH = halfW * RING_VERTICAL_SCALE       // RING_VERTICAL_SCALE = 0.45f
x = originX + halfW * cos(angle)
y = originY + halfH * sin(angle)
```

- `angle` increases counter-clockwise in standard math convention; draw using `MathUtils.cosDeg` / `MathUtils.sinDeg`.
- Debris chunks share the same ellipse formula, offset by their `relativeAngle` within the arc.
- Stars share the same ellipse formula; they co-rotate at the pod's angular speed.

---

## 5. Asset Filenames

All assets reside under `assets/` in the project root.

### Pod Skins — `assets/sprites/` (or root; pipeline copies to `assets/`)
```
pod_skin_0.png   pod_skin_1.png   pod_skin_2.png
pod_skin_3.png   pod_skin_4.png   pod_skin_5.png
```

### Planet sprites
```
planet_world_0.png   planet_world_1.png
planet_world_2.png   planet_world_3.png
```

### Backgrounds
```
bg_world_0.png   bg_world_1.png
bg_world_2.png   bg_world_3.png
```

### Debris
```
debris_rock.png
debris_ice.png
```

### Collectibles & Effects
```
star_collectible.png
fx_collect_star.png    (4×4 spritesheet)
fx_pod_death.png       (4×4 spritesheet)
```

### UI
```
ui_btn_play.png
ui_btn_generic.png
ui_btn_back.png
ui_btn_retry.png
ui_icon_star.png
ui_icon_crown.png
ui_panel_card.png        (9-patch)
ui_badge_owned.png
ui_badge_equipped.png
ui_badge_locked.png
ui_world_card.png        (9-patch)
ui_logo.png
ui_toggle_on.png
ui_toggle_off.png
```

### Audio — `assets/sounds/` and `assets/music/`
```
music_menu.ogg    (streamed)
music_game.ogg    (streamed)
sfx_jump.ogg
sfx_star.ogg
sfx_death.ogg
sfx_button.ogg
sfx_unlock.ogg
```

### Fonts — `assets/fonts/`
```
font1.ttf             (primary: score, titles, logo)
font2.ttf             (secondary: labels, buttons, descriptions)
Roboto-Regular.ttf    (fallback body)
```

**Fallback rule:** If any asset file is missing at runtime, substitute with a 1×1 white Pixmap texture so the game does not crash. Log a warning via `Gdx.app.log`.

---

## 6. Constants.java — All Magic Numbers

```java
public final class Constants {

    // Viewport
    public static final float WORLD_WIDTH  = 360f;
    public static final float WORLD_HEIGHT = 640f;

    // Scene centre
    public static final float CENTRE_X = WORLD_WIDTH  / 2f;   // 180
    public static final float CENTRE_Y = WORLD_HEIGHT / 2f;   // 320

    // Planet
    public static final float PLANET_DIAMETER = 220f;

    // Rings
    public static final int   RING_COUNT = 4;
    public static final float[] RING_HALF_WIDTHS  = {130f, 185f, 240f, 295f};
    public static final float   RING_VERTICAL_SCALE = 0.45f;
    public static final float   RING_LINE_WIDTH = 2f;

    // Pod
    public static final float POD_SIZE        = 32f;
    public static final float POD_RADIUS      = 12f;
    public static final int   POD_START_RING  = 2;    // ring index 1-based
    public static final float POD_START_ANGLE = 0f;

    // Pod angular speeds per world (degrees/second)
    public static final float[] WORLD_POD_SPEEDS = {45f, 60f, 80f, 105f};

    // Worlds
    public static final int   WORLD_COUNT = 4;
    public static final int[] WORLD_UNLOCK_SCORES = {0, 300, 700, 1500};

    // Debris
    public static final float DEBRIS_CHUNK_RADIUS     = 9f;
    public static final float DEBRIS_CHUNK_SIZE        = 20f;
    public static final int   DEBRIS_CHUNKS_MIN        = 2;
    public static final int   DEBRIS_CHUNKS_MAX        = 5;
    public static final int   DEBRIS_FIELDS_INITIAL    = 1;
    public static final int   DEBRIS_FIELDS_MAX        = 6;
    public static final float DEBRIS_FIELDS_INTERVAL   = 20f;  // seconds between +1 field
    public static final float DEBRIS_ARC_INITIAL       = 40f;  // degrees
    public static final float DEBRIS_ARC_INCREMENT     = 5f;
    public static final float DEBRIS_ARC_INCREMENT_INTERVAL = 30f;
    public static final float DEBRIS_ARC_MAX           = 90f;
    public static final float DEBRIS_SPEED_INITIAL     = 30f;  // degrees/second
    public static final float DEBRIS_SPEED_MULTIPLIER  = 1.08f;
    public static final float DEBRIS_SPEED_INTERVAL    = 15f;
    public static final float DEBRIS_SPEED_MAX         = 120f;

    // Star collectible
    public static final float STAR_RADIUS             = 11f;
    public static final float STAR_SIZE               = 24f;
    public static final float STAR_VISIBILITY_INITIAL = 2.5f;  // seconds
    public static final float STAR_VISIBILITY_DECREMENT = 0.1f;
    public static final float STAR_VISIBILITY_INTERVAL = 25f;
    public static final float STAR_VISIBILITY_FLOOR   = 1.2f;
    public static final float STAR_SPAWN_INITIAL      = 4f;    // seconds between spawns
    public static final float STAR_SPAWN_INCREMENT    = 0.2f;
    public static final float STAR_SPAWN_INTERVAL     = 30f;
    public static final float STAR_SPAWN_MAX          = 7f;

    // Scoring
    public static final int   SCORE_PER_SECOND        = 1;
    public static final int   SCORE_PER_STAR           = 10;
    public static final int   SCORE_MILESTONE_STEP    = 100;
    public static final int   SCORE_MILESTONE_BONUS   = 50;

    // Shop — skin prices (index 0 = free)
    public static final int[] SKIN_PRICES = {0, 50, 200, 500, 1000, 2000};
    public static final int   SKIN_COUNT  = 6;

    // Death animation
    public static final float DEATH_ANIM_DURATION = 0.4f;   // seconds before transition

    // UI transitions
    public static final float TRANSITION_DURATION = 0.15f;  // fade/scale for menu buttons

    // Leaderboard
    public static final int   LEADERBOARD_MAX_ENTRIES = 10;

    // Preferences file name
    public static final String PREFS_NAME = "ring_jumper_prefs";
}
```

---

## 7. Data Persistence (SharedPreferences)

Preference file name: `"ring_jumper_prefs"` (`Gdx.app.getPreferences(Constants.PREFS_NAME)`)

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `PREF_STARS_BALANCE` | int | 0 | Total stars earned across all runs |
| `PREF_OWNED_SKINS` | int | 1 | Bitmask; bit N = skin N owned; bit 0 always set |
| `PREF_SKIN` | int | 0 | Index of currently equipped skin (0–5) |
| `PREF_HIGH_SCORE` | int | 0 | All-time highest score |
| `PREF_CURRENT_WORLD` | int | 0 | Last selected world index (0–3) |
| `PREF_UNLOCKED_WORLDS` | int | 1 | Bitmask; bit N = world N unlocked; bit 0 always set |
| `PREF_MUSIC` | boolean | true | Music enabled |
| `PREF_SFX` | boolean | true | Sound effects enabled |
| `PREF_VIBRATION` | boolean | true | Vibration enabled |
| `PREF_LEADERBOARD` | String | `""` | JSON array — up to 10 `{"score":N,"world":N}` objects, sorted desc by score |

All reads/writes happen on the GL thread. Call `prefs.flush()` after every write.

---

## 8. Difficulty Scaling (GameScreen.updateDifficulty)

Called once per frame with current `elapsedTime`. Compute each scaled parameter inline (no state machine needed):

```java
// Active debris field count
int targetFields = Constants.DEBRIS_FIELDS_INITIAL
    + (int)(elapsedTime / Constants.DEBRIS_FIELDS_INTERVAL);
targetFields = Math.min(targetFields, Constants.DEBRIS_FIELDS_MAX);
// Spawn new DebrisField objects until debrisFields.size == targetFields

// Debris arc span (degrees)
float arcSpan = Constants.DEBRIS_ARC_INITIAL
    + (int)(elapsedTime / Constants.DEBRIS_ARC_INCREMENT_INTERVAL)
      * Constants.DEBRIS_ARC_INCREMENT;
arcSpan = Math.min(arcSpan, Constants.DEBRIS_ARC_MAX);

// Debris rotation speed
int speedSteps = (int)(elapsedTime / Constants.DEBRIS_SPEED_INTERVAL);
float debrisSpeed = Constants.DEBRIS_SPEED_INITIAL
    * (float)Math.pow(Constants.DEBRIS_SPEED_MULTIPLIER, speedSteps);
debrisSpeed = Math.min(debrisSpeed, Constants.DEBRIS_SPEED_MAX);

// Star visibility duration
int visSteps = (int)(elapsedTime / Constants.STAR_VISIBILITY_INTERVAL);
float starVisibility = Constants.STAR_VISIBILITY_INITIAL
    - visSteps * Constants.STAR_VISIBILITY_DECREMENT;
starVisibility = Math.max(starVisibility, Constants.STAR_VISIBILITY_FLOOR);

// Star spawn interval
float starInterval = Constants.STAR_SPAWN_INITIAL
    + (int)(elapsedTime / Constants.STAR_SPAWN_INTERVAL)
      * Constants.STAR_SPAWN_INCREMENT;
starInterval = Math.min(starInterval, Constants.STAR_SPAWN_MAX);
```

Apply `arcSpan` and `debrisSpeed` to all *newly spawned* debris fields. Existing fields keep the speed at which they were spawned (no retroactive changes). When a `DebrisField` completes a full orbit (angle wraps 360°), reassign it to a random ring.

---

## 9. Pod Jump Logic

```java
void onTap() {
    int newRing = podRing + jumpDirection;
    if (newRing < 1) {
        newRing = 1;
        jumpDirection = +1;   // force outward next
    } else if (newRing > 4) {
        newRing = 4;
        jumpDirection = -1;   // force inward next
    } else {
        jumpDirection = -jumpDirection;  // alternate
    }
    podRing = newRing;
    // play sfx_jump
}
```

Jump is **instantaneous** — no position tweening.

---

## 10. Collision Detection

Both pod and game objects live on ring ellipses. Convert all positions to flat screen `(x, y)` via the ellipse formula (§4), then use circle-circle overlap:

```java
boolean circles_overlap(float x1, float y1, float r1, float x2, float y2, float r2) {
    float dx = x1 - x2, dy = y1 - y2;
    float dist2 = dx*dx + dy*dy;
    float sumR = r1 + r2;
    return dist2 < sumR * sumR;
}
```

- Pod radius: `Constants.POD_RADIUS` (12 px).  
- Debris chunk radius: `Constants.DEBRIS_CHUNK_RADIUS` (9 px).  
- Star radius: `Constants.STAR_RADIUS` (11 px).  
- Check collisions only when game is not paused and pod is not dead.

---

## 11. Leaderboard Persistence

```java
// Append run result, keep top-10
void addLeaderboardEntry(int score, int worldIndex) {
    Array<LeaderboardEntry> list = loadLeaderboard();
    list.add(new LeaderboardEntry(score, worldIndex));
    list.sort((a, b) -> b.score - a.score);
    if (list.size > Constants.LEADERBOARD_MAX_ENTRIES)
        list.removeRange(Constants.LEADERBOARD_MAX_ENTRIES, list.size - 1);
    saveLeaderboard(list);
}
// Serialise with LibGDX Json or manual string building
```

---

## 12. Audio Rules

- **Music:** Use `Gdx.audio.newMusic(...)` (streamed). Only one track plays at a time. Respect `PREF_MUSIC`.  
  - Menus: `music_menu.ogg` looped.  
  - GameScreen: `music_game.ogg` looped.  
- **SFX:** Use `Gdx.audio.newSound(...)` (memory). Play only if `PREF_SFX` is true.  
- **Vibration:** `Gdx.input.vibrate(50)` on death; `Gdx.input.vibrate(20)` on star collect. Guard with `PREF_VIBRATION`.  
- Dispose all `Music` and `Sound` objects in `MainGame.dispose()`.

---

## 13. Rendering Order (GameScreen per frame)

1. Clear screen (black).  
2. Draw `backgroundTexture` fullscreen.  
3. Draw rings (ShapeRenderer, `RING_LINE_WIDTH` lines, world-tinted colour, ellipses).  
4. Draw planet centred (`PLANET_DIAMETER`).  
5. Draw debris chunks.  
6. Draw star collectibles.  
7. Draw pod sprite.  
8. Draw particle effects (`fx_collect_star`, `fx_pod_death`).  
9. Draw HUD (score top-left, stars top-right).  
10. If `paused`: draw translucent overlay + pause buttons.  
11. If `dead` and `deathTimer < DEATH_ANIM_DURATION`: draw death animation (pod spins + shrinks).

---

## 14. Out of Scope (v1.0)

- Online / cloud leaderboards  
- Achievements or Google Play Games integration  
- In-app purchases or real-money monetisation  
- Landscape orientation  
- Tablet-specific layouts  
- Mid-run checkpoints or continues  
- Power-ups beyond star collection  
- Pod movement animation / tweening between rings  
- Animated planet surfaces (gas giant storms)  
- Localisation (English only)  
- Ads or analytics SDKs  
</parameter>
</invoke>
</function_calls>

`projects/game-888-ring-jumper/GAME_SPEC.md` written. Here's what it covers:

1. **Game Identity** — package name pattern, main class, viewport
2. **Screen Inventory** — all 8 screens with exact Java class names and transitions
3. **Screen Flow** — ASCII diagram matching the GDD
4. **Game Objects** — 14 classes with fields and method signatures: `MainGame`, `Constants`, `GameScreen`, `DebrisField`, `DebrisChunk`, `StarCollectible`, `GameOverScreen`, `ShopScreen`, `LeaderboardScreen`, `LeaderboardEntry`, `SettingsScreen`, `HowToPlayScreen`, `WorldsScreen`, `MainMenuScreen`
5. **Coordinate system** — exact ellipse formula for placing pod/debris/stars on rings
6. **Asset filenames** — exact names from GDD, all categories
7. **Constants.java** — every magic number as a typed `static final` field
8. **Data persistence** — all 10 SharedPreferences keys with types and defaults
9. **Pod jump logic** — exact boundary/alternation code snippet
10. **Difficulty scaling** — inline computation formulas
11. **Collision detection** — circle-circle via screen coords
12. **Rendering order** — 11-step draw order per frame
13. **Out of Scope** — verbatim from GDD