```markdown
# GDD.md — Ring Jumper

**Genre:** Reflex-Arcade  
**Platform:** Android (LibGDX)  
**Engine:** LibGDX (Java)  
**Version:** 1.0  
**Date:** 2026-03-23

---

## 1. Overview

Ring Jumper is a single-tap reflex arcade game set in deep space. A tiny pod automatically orbits a gas giant along one of four concentric rings. The player taps to jump the pod one ring outward; tapping again jumps it one ring inward. The direction alternates with each tap and resets at the boundaries (innermost / outermost ring). Rotating debris fields occupy segments of specific rings and must be avoided by switching rings. Collectible stars blink into existence on rings briefly — fly through them to earn currency for the in-game shop. Survival time and star collection drive score.

---

## 2. Screen List

| # | Screen | Role |
|---|--------|------|
| 1 | MainMenuScreen | Entry point, navigation hub |
| 2 | WorldsScreen | Select & unlock themed gas giant environments |
| 3 | GameScreen | Core gameplay |
| 4 | GameOverScreen | Score recap, retry / menu |
| 5 | ShopScreen | Browse and purchase pod skins |
| 6 | LeaderboardScreen | Local top-10 high scores |
| 7 | SettingsScreen | Sound, music, vibration toggles |
| 8 | HowToPlayScreen | Illustrated tap-mechanic tutorial |

---

## 3. Screen Flow

```
MainMenuScreen
  ├── [Play]        → WorldsScreen → GameScreen
  │                                     └── [Game Over] → GameOverScreen
  │                                                          ├── [Retry]   → GameScreen
  │                                                          └── [Menu]    → MainMenuScreen
  ├── [Shop]        → ShopScreen → MainMenuScreen
  ├── [Scores]      → LeaderboardScreen → MainMenuScreen
  ├── [How to Play] → HowToPlayScreen → MainMenuScreen
  └── [Settings]    → SettingsScreen → MainMenuScreen
```

---

## 4. Core Gameplay Loop

1. Pod spawns on **Ring 2** orbiting counter-clockwise at constant angular velocity.
2. Player taps screen → pod jumps **one ring outward** (2→3). Next tap → one ring **inward** (3→2). Direction alternates each tap. At Ring 1 (innermost), boundary forces next tap outward. At Ring 4 (outermost), boundary forces next tap inward.
3. **Debris fields** rotate on specific rings. Each field is an arc of 2–5 asteroid chunks that occupy a fixed angular span and rotate with the ring at varying speeds. Touching any chunk ends the run.
4. **Stars** blink onto a random ring position for 2.5 s then vanish. Flying through one awards +1 star (currency) and a score bonus.
5. **Score** accumulates every second of survival; bonus points added per star collected.
6. Difficulty ramps up over time: debris arc count, rotation speed, and angular coverage all increase.
7. Death → GameOverScreen.

---

## 5. Per-Screen Detail

### 5.1 MainMenuScreen

**Layout**
- Animated background: slow-rotating gas giant with rings visible; star-field parallax.
- Center: game logo "RING JUMPER" (large, stylised).
- Below logo (vertical stack of buttons): **Play**, **Shop**, **Scores**, **How to Play**, **Settings**.
- Top-right corner: star icon + current star balance (integer).

**Behaviour**
- Tapping **Play** goes to WorldsScreen.
- All buttons have a brief scale-pop animation on tap.
- Background music loops (track: `music_menu.ogg`).

---

### 5.2 WorldsScreen

**Layout**
- Title: "SELECT WORLD" top-centre.
- 4 world cards displayed in a 2×2 grid, each showing: planet thumbnail, world name, unlock condition or "UNLOCKED" badge.
- Back button (top-left).
- Currently selected world has a glowing border.

**Worlds**

| # | Name | Unlock Condition | Palette |
|---|------|-----------------|---------|
| 1 | Amber Giant | Free (default) | Orange / tan rings |
| 2 | Storm Lord | Reach 300 score once | Red-brown / purple lightning |
| 3 | Glacial Titan | Reach 700 score once | Ice blue / white rings |
| 4 | Void Sovereign | Reach 1 500 score once | Black / neon violet rings |

**Behaviour**
- Tapping an unlocked world selects it (saves `PREF_CURRENT_WORLD`) and starts GameScreen.
- Tapping a locked world shows a toast: "Reach [N] score to unlock".

---

### 5.3 GameScreen

**Layout**
- Full-screen gameplay area — circular orbiting scene centered.
- Gas giant fills roughly 30 % of screen diameter at centre.
- 4 concentric ring tracks drawn as thin ellipses (perspective foreshortening suggested by vertical compression).
- Pod sprite on current ring.
- Debris chunks on affected ring arcs.
- Stars (glowing 5-point star sprite) blink in/out.
- Top-left: current score (integer).
- Top-right: star icon + stars collected this run.
- No pause button (distraction-free); back gesture opens a minimal pause overlay.

**Pause Overlay**
- Translucent dark sheet over gameplay.
- Buttons: **Resume**, **Restart**, **Menu**.

**Behaviour**
- Pod orbits continuously; angular speed constant per world (world 1 slowest, world 4 fastest).
- Each tap moves the pod one ring (alternating out/in as described in §4).
- Jump is instant (no tween) — this is a reflex game.
- On collision with debris: screen flash red, vibration pulse (if enabled), transition to GameOverScreen after 0.4 s death animation (pod spins + shrinks).
- On star collection: `sfx_star.ogg`, +1 star balance, small particle burst.

---

### 5.4 GameOverScreen

**Layout**
- Background: blurred screenshot of final gameplay frame.
- Large "GAME OVER" text.
- Score this run (large number).
- Best score (smaller, with crown icon).
- Stars collected this run (star icon + count).
- Buttons: **Retry**, **Shop**, **Menu**.
- If new high score: animated "NEW BEST!" banner.

**Behaviour**
- On display: persists new high score if beaten (`PREF_HIGH_SCORE`), adds stars collected to balance (`PREF_STARS_BALANCE`), appends leaderboard entry.
- **Retry** restarts GameScreen with same world.
- **Shop** → ShopScreen (back from shop returns here).

---

### 5.5 ShopScreen

**Layout**
- Title: "POD SKINS" top-centre.
- Star balance top-right (icon + number).
- 6 skin cards arranged in a 2×3 scroll grid. Each card:
  - Pod preview (animated, slowly rotating).
  - Skin name.
  - Status badge: **EQUIPPED** (green) / **OWNED** (grey) / price in stars (locked gold).
- Back button top-left.

**Skins**

| # | Name | Description | Price |
|---|------|-------------|-------|
| 1 | Micro Probe | The original brushed-metal survey pod. | Free |
| 2 | Neon Orb | A pulsing sphere of electric cyan light. | 50 ★ |
| 3 | Crystal Sphere | A faceted gem pod that refracts starlight. | 200 ★ |
| 4 | Solar Flare | A molten core pod trailing a fiery aura. | 500 ★ |
| 5 | Dark Matter | A perfectly black pod that bends light around its rim. | 1 000 ★ |
| 6 | Cosmic Eye | An ancient relic pod with a glowing iris at its centre. | 2 000 ★ |

**Behaviour**
- Tapping an **owned** skin equips it; saves `PREF_SKIN`.
- Tapping a **locked** skin: if balance ≥ price → confirm popup ("Buy for N ★?") → on confirm deduct balance, set bit in `PREF_OWNED_SKINS`, equip.
- Tapping a locked skin with insufficient balance → toast "Not enough stars".
- Skin #1 always shows OWNED and cannot be bought.

---

### 5.6 LeaderboardScreen

**Layout**
- Title: "TOP SCORES" top-centre.
- Scrollable list of up to 10 entries. Each row: rank (#1–#10), initials placeholder "YOU", score, world icon.
- Highlighted row for player's own best entry.
- Back button.

**Behaviour**
- Scores stored locally (SharedPreferences, serialised list).
- List sorted descending by score.
- Duplicate runs: only best score per session kept; new run replaces lowest if list full.

---

### 5.7 SettingsScreen

**Layout**
- Title: "SETTINGS" top-centre.
- Toggle rows (label left, toggle switch right):
  - Music (on/off)
  - Sound Effects (on/off)
  - Vibration (on/off)
- Back button.

**Behaviour**
- Changes apply immediately and persist (`PREF_MUSIC`, `PREF_SFX`, `PREF_VIBRATION`).

---

### 5.8 HowToPlayScreen

**Layout**
- Title: "HOW TO PLAY" top-centre.
- 3 illustrated panels (swipeable or stacked with arrows):
  1. **Orbit** — diagram of pod on ring circling planet.
  2. **Jump** — finger-tap icon with arrows showing outward/inward movement between rings.
  3. **Survive & Collect** — debris arc (red, avoid) vs. star (gold, collect).
- Each panel: short 1–2 sentence caption.
- Back button.

---

## 6. Game Objects

### 6.1 Pod (Player)

- Sprite: one of 6 skin PNGs (`pod_skin_0.png` … `pod_skin_5.png`), ~32×32 px.
- Position: defined by (ring index 1–4, current angle in degrees).
- Angular velocity: constant per world (e.g., 45 °/s for world 1, increasing per world).
- Collision shape: circle, radius = 12 px logical.
- Jump: instantaneous ring index ± 1.

### 6.2 Gas Giant

- Static sprite centred on screen (`planet_world_N.png`), ~220×220 px.
- Visual only; not a collidable game object.

### 6.3 Rings (Tracks)

- 4 elliptical paths drawn procedurally (ShapeRenderer or Pixmap).
- Radii: ring 1 = 130, ring 2 = 185, ring 3 = 240, ring 4 = 295 (half-widths, logical px; vertical axis compressed 0.45× for perspective).
- Drawn as thin lines (2 px), color tinted per world theme.
- No collision; purely visual guides.

### 6.4 Debris Field

- A group of 2–5 `DebrisChunk` objects sharing the same ring index and occupying a contiguous angular arc.
- Each chunk: sprite (`debris_rock.png` or `debris_ice.png`), ~20×20 px.
- The arc rotates around the planet at a speed independent of the pod (can be faster, slower, or opposite direction).
- Collision shape per chunk: circle, radius 9 px.
- Multiple debris fields can exist simultaneously on the same or different rings.

### 6.5 Star Collectible

- Sprite: `star_collectible.png`, ~24×24 px, golden glow.
- Spawned at a random (ring, angle) position not currently occupied by debris.
- Blinks (alpha oscillates) for 2.5 s then fades out.
- Co-rotates with its ring at the same angular velocity as the pod.
- Collision shape: circle, radius 11 px.
- On overlap with pod: collected.

---

## 7. Controls

| Input | Action |
|-------|--------|
| Single tap anywhere | Jump pod one ring (alternating outward / inward) |
| Back gesture | Open pause overlay during gameplay |

No multi-touch or swipe inputs required.

---

## 8. Scoring & Difficulty

### 8.1 Scoring

| Event | Points |
|-------|--------|
| Survival | +1 per second |
| Star collected | +10 |
| Every 100 score milestone | +50 bonus |

Star currency awarded: 1 star per collectible touched (accumulated across runs, persisted).

### 8.2 Difficulty Progression

Difficulty is continuous, driven by elapsed game time `t` (seconds):

| Parameter | Initial | Scaling |
|-----------|---------|---------|
| Active debris fields | 1 | +1 every 20 s, max 6 |
| Debris arc span | 40° | +5° every 30 s, max 90° |
| Debris rotation speed | 30°/s | ×1.08 every 15 s, max 120°/s |
| Star visibility duration | 2.5 s | −0.1 s every 25 s, floor 1.2 s |
| Star spawn interval | 4 s | +0.2 s every 30 s, max 7 s |
| Pod angular speed | Per world | Unchanged |

Debris fields are reassigned to new rings randomly each time they complete a full orbit, keeping the player guessing.

---

## 9. Asset List

### 9.1 Pod Skins

| File | Description |
|------|-------------|
| `pod_skin_0.png` | Small brushed-metal oblate sphere with a dark viewport window |
| `pod_skin_1.png` | Smooth cyan sphere with a bright internal glow and electric rim |
| `pod_skin_2.png` | Faceted gem-cut sphere in pale blue-white with refraction highlights |
| `pod_skin_3.png` | Molten orange-red sphere ringed by wisps of flame |
| `pod_skin_4.png` | Matte black sphere with a thin violet light-bending fringe along its silhouette |
| `pod_skin_5.png` | Dark metallic sphere with a single large glowing teal iris at centre |

### 9.2 Planets (Gas Giants)

| File | Description |
|------|-------------|
| `planet_world_0.png` | Amber-orange banded gas giant with tan ring shadow |
| `planet_world_1.png` | Deep red-brown stormy planet with swirling purple cloud bands |
| `planet_world_2.png` | Pale ice-blue planet with white cloud streaks and faint ring glint |
| `planet_world_3.png` | Near-black planet with faint violet bioluminescent cloud patterns |

### 9.3 Backgrounds

| File | Description |
|------|-------------|
| `bg_world_0.png` | Warm amber starfield with distant nebula in orange and gold |
| `bg_world_1.png` | Deep red nebula with electric purple lightning arcs |
| `bg_world_2.png` | Cold navy sky with white star clusters and a faint ice comet trail |
| `bg_world_3.png` | Pure black void with scattered neon violet star points |

### 9.4 Debris

| File | Description |
|------|-------------|
| `debris_rock.png` | Jagged grey asteroid chunk, irregular polygon shape |
| `debris_ice.png` | Semi-translucent pale blue ice shard with fracture lines |

### 9.5 Collectibles & Effects

| File | Description |
|------|-------------|
| `star_collectible.png` | Five-point golden star with radial soft glow halo |
| `fx_collect_star.png` | Burst particle sprite sheet (4×4 frames) for star pickup |
| `fx_pod_death.png` | Explosion particle sprite sheet (4×4 frames) for pod destruction |

### 9.6 UI Elements

| File | Description |
|------|-------------|
| `ui_btn_play.png` | Large rounded-rect play button, green gradient |
| `ui_btn_generic.png` | Standard menu button, dark translucent with bright border |
| `ui_btn_back.png` | Left-arrow back button, small |
| `ui_btn_retry.png` | Circular arrow retry button |
| `ui_icon_star.png` | Small star icon for HUD currency display |
| `ui_icon_crown.png` | Crown icon for best-score label |
| `ui_panel_card.png` | 9-patch card background for shop skin cards |
| `ui_badge_owned.png` | "OWNED" stamp badge, grey |
| `ui_badge_equipped.png` | "EQUIPPED" stamp badge, green |
| `ui_badge_locked.png` | Lock icon overlay for unowned skins |
| `ui_world_card.png` | 9-patch card background for world selection |
| `ui_logo.png` | "RING JUMPER" game logo treatment |
| `ui_toggle_on.png` | Toggle switch in ON state |
| `ui_toggle_off.png` | Toggle switch in OFF state |

### 9.7 Audio

| File | Description |
|------|-------------|
| `music_menu.ogg` | Ambient synthesizer loop for menus, calm and spacious |
| `music_game.ogg` | Up-tempo electronic loop for gameplay, driving pulse |
| `sfx_jump.ogg` | Short whoosh / pop for pod ring jump |
| `sfx_star.ogg` | Bright chime / sparkle for star collection |
| `sfx_death.ogg` | Crunch / explosion burst for pod collision |
| `sfx_button.ogg` | Soft click for UI button taps |
| `sfx_unlock.ogg` | Ascending chime for world or skin unlock |

### 9.8 Fonts

| File | Usage |
|------|-------|
| `font1.ttf` | Primary display font — game logo, score, titles |
| `font2.ttf` | Secondary UI font — labels, descriptions, buttons |
| `Roboto-Regular.ttf` | Fallback body font |

---

## 10. Visual Style

- **Aesthetic:** Dark space; minimal UI chrome. Vibrant planet and pod colours pop against near-black backgrounds.
- **Camera:** Fixed orthographic, no panning. Scene is always centred on planet.
- **Rings:** Thin lines, slightly transparent, tinted to world palette.
- **Pod:** Always front-and-centre visually despite being a small sprite; clear silhouette.
- **Debris:** Slightly desaturated, chunky — easy to read as "danger" at a glance.
- **Stars:** Bright gold, animated alpha pulse — easy to read as "collect me".
- **Perspective illusion:** Vertical axis of all ellipses compressed to ~45 % of horizontal to imply a top-down angled view into the orbital plane.
- **Animations:** Minimal — pod does not tween between rings (instant jump). Menus use gentle fade / scale transitions (0.15 s).

---

## 11. Data Persistence (SharedPreferences)

All keys stored under preference file name `"ring_jumper_prefs"`.

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `PREF_STARS_BALANCE` | int | 0 | Total stars earned across all runs |
| `PREF_OWNED_SKINS` | int | 1 | Bitmask; bit 0 = skin #1 (always set), bit 5 = skin #6 |
| `PREF_SKIN` | int | 0 | Index of currently equipped skin (0–5) |
| `PREF_HIGH_SCORE` | int | 0 | All-time highest score |
| `PREF_CURRENT_WORLD` | int | 0 | Last selected world index (0–3) |
| `PREF_UNLOCKED_WORLDS` | int | 1 | Bitmask; bit 0 = Amber Giant (always set) |
| `PREF_MUSIC` | boolean | true | Music enabled |
| `PREF_SFX` | boolean | true | Sound effects enabled |
| `PREF_VIBRATION` | boolean | true | Vibration enabled |
| `PREF_LEADERBOARD` | String | "" | JSON-serialised array of up to 10 `{score, world}` objects |

---

## 12. Technical Constraints

- **Target:** Android API 21+ (Android 5.0).
- **Orientation:** Portrait only (`screenOrientation="portrait"`).
- **Resolution:** Design at 360×640 dp logical; scale via LibGDX `FitViewport(360, 640)`.
- **Frame rate target:** 60 fps. All game logic delta-time driven.
- **Memory:** No single texture atlas may exceed 2 048×2 048 px. Pack all UI sprites into one atlas; pack all game-world sprites into a second atlas.
- **Audio:** OGG format only. Music streamed; SFX loaded into memory.
- **No network calls.** All data is local (SharedPreferences + bundled assets).
- **No Google Play Services / ads / IAP** in v1.0.
- **Single-thread game loop** — all LibGDX lifecycle calls on GL thread; SharedPreferences reads/writes on GL thread acceptable given small payload size.

---

## 13. Out of Scope (v1.0)

- Online / cloud leaderboards.
- Achievements or Google Play Games integration.
- In-app purchases or real-money monetisation.
- Landscape orientation.
- Tablet-specific layouts.
- Mid-run checkpoints or continues.
- Power-ups beyond star collection.
- Pod movement animation / tweening between rings.
- Animated planet surfaces (gas giant storms).
- Localization (English only).
```

The file is ready to save to the project.

```bash
cat projects/game-888-ring-jumper/
```

Let me check if the game directory exists first.

```bash
ls projects/ | grep 888
```