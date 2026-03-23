# Figma AI Design Brief — Ring Jumper

---

## 1. Art Style & Color Palette

Ring Jumper uses a **deep-space neon-cosmic** visual style: rich void blacks and dark navy backgrounds lit by soft planetary glow, with electric neon rings tracing orbital paths. The aesthetic sits between retro arcade and modern synthwave — clean geometric shapes, glowing halos, and particle trails that feel both technical and dreamlike. Typography should feel crisp and futuristic without being cold; think soft-edged pixel fonts or rounded sci-fi letterforms.

**Primary Palette**
| Role | Hex | Usage |
|------|-----|-------|
| Void Black | `#05060F` | Screen backgrounds, deep space |
| Deep Navy | `#0B1230` | Panel fills, card backgrounds |
| Nebula Purple | `#2D1B69` | Atmospheric mid-tone, gradients |
| Gas Giant Teal | `#1ECFCF` | Planet atmosphere, ring glow |

**Accent Palette**
| Role | Hex | Usage |
|------|-----|-------|
| Solar Orange | `#FF7A1A` | Debris hazards, warning highlights |
| Star Gold | `#FFD84D` | Collectible stars, score text |

**Font Mood:** Orbitron or Exo2 — geometric, wide-tracked, sci-fi weight. Headings in uppercase with generous letter-spacing. Body/HUD in a lighter weight of the same family or Roboto-Regular as fallback.

---

## 2. App Icon — `icon_512.png` (512×512 px)

**Canvas:** 512×512 px square, no rounded corners applied (the OS masks it).

**Description:** The background is a radial gradient from `#0B1230` at center to `#05060F` at corners, with a faint nebula smear of `#2D1B69` drifting across the lower-left quadrant. Centered is a gas giant — a softly banded sphere in teal-cyan (`#1ECFCF`) and pale aqua (`#7FFFFF`), roughly 220 px diameter, with a subtle equatorial haze. Four concentric orbital rings circle the planet as thin ellipses — the innermost nearly touching the planet surface, the outermost reaching near the canvas edge — rendered in glowing white-to-cyan with a soft outer bloom. A small angular pod (≈30 px, bright white with a cyan exhaust trail) sits on the second ring at the 2 o'clock position, suggesting motion. A scatter of tiny white star dots fills the background. The overall mood is serene but kinetic — a lone explorer in a grand cosmos.

---

## 3. UI Screens (480×854 px portrait)

---

### MainMenuScreen

**A) Background Image**
Full-bleed deep space scene: radial gradient from `#0D1535` at center bloom to `#05060F` at all edges. A large gas giant occupies center-lower canvas — roughly 340 px diameter, showing soft teal-cyan banding (`#1ECFCF` / `#0E8FA0`) with a faint atmospheric halo bleeding outward 40 px. Four concentric orbital ring ellipses encircle the planet; rings are luminous thin arcs in white-cyan with a glow pass, slightly transparent where they pass "behind" the planet. A tiny pod silhouette sits on ring 2, upper-right arc. Scattered star particles of varying opacity and size fill the upper two-thirds. A faint purple nebula wash (`#2D1B69`, 30% opacity) trails across the upper-left. No text, no buttons, no UI chrome.

**B) Button Layout**
```
RING JUMPER (title label)  | top-Y=80px  | x=centered          | size=360x60
PLAY                       | top-Y=420px | x=centered          | size=260x56
SHOP                       | top-Y=496px | x=centered          | size=260x52
SCORES                     | top-Y=568px | x=centered          | size=260x52
HOW TO PLAY                | top-Y=640px | x=centered          | size=260x52
SETTINGS                   | top-Y=790px | x=right@20px        | size=56x56
```

---

### GameScreen

**A) Background Image**
Pure gameplay backdrop — no HUD chrome, no ring art (rings are drawn in code). Radial deep-space gradient: `#05060F` outer void, `#0D1A3A` mid-field, very faint `#1A2A50` at dead center where the planet sits. The gas giant is rendered large (≈300 px) at exact canvas center — soft teal/aqua banded sphere with atmospheric glow, partially cropped by ring layers above it. Background stars: two layers — large sparse dots (8–12 px, `#FFFFFF` at 80% opacity) and a dense field of 1–2 px micro-stars. A subtle purple-magenta nebula smear in the upper-right quadrant adds depth. No rings, no pod, no debris, no stars drawn here — all dynamic elements are code-rendered.

**B) Button Layout**
```
SCORE (value label)        | top-Y=24px  | x=centered          | size=200x40
BEST (value label)         | top-Y=68px  | x=centered          | size=160x32
PAUSE                      | top-Y=20px  | x=right@16px        | size=52x52
```

---

### GameOverScreen

**A) Background Image**
Same deep-space base as GameScreen but desaturated slightly — the gas giant at center glows dimmer, tinted toward cool grey-blue (`#0E2040`). A faint red-orange vignette (`#FF3300` at 15% opacity) pulses inward from all edges, suggesting destruction or danger. Scattered debris silhouettes — jagged rock chunks, 8–14 px, very dark grey — float in the mid-field around the planet. A horizontal decorative banner frame floats at vertical center (approx Y=280–560): a dark translucent rounded rectangle with a subtle neon-teal border stroke and inner glow — completely blank inside, ready for code-drawn text. Star particles are sparse and dimmer than normal play. No text, no buttons.

**B) Button Layout**
```
GAME OVER (title label)    | top-Y=180px | x=centered          | size=340x60
SCORE (value label)        | top-Y=290px | x=centered          | size=280x44
BEST (value label)         | top-Y=348px | x=centered          | size=240x36
STARS COLLECTED (label)    | top-Y=400px | x=centered          | size=280x36
RETRY                      | top-Y=530px | x=centered          | size=260x56
MENU                       | top-Y=606px | x=centered          | size=260x52
```

---

### ShopScreen

**A) Background Image**
Dark navy base (`#080E22`) with a subtle top-down vignette. Upper-center: a soft gold-white radial light bloom (`#FFD84D` at 20% opacity, ~200 px radius) suggesting a spotlight on featured items. Three decorative pod-display card frames arranged in a 2+1 grid layout below center — each card is a rounded rectangle (~130×160 px) with a neon-teal hairline border and a very faint teal inner glow; completely blank interiors. A second row of three identical blank card frames below the first row. Header area (top 140 px) has a slightly lighter navy panel (`#0D1535`) separated by a thin teal divider line. Small star-dust particles drift upward in the background. No text, no pod artwork, no prices — all rendered in code.

**B) Button Layout**
```
SHOP (title label)         | top-Y=36px  | x=centered          | size=240x50
STAR COUNT (label)         | top-Y=36px  | x=right@20px        | size=140x40
[Card slot 1]              | top-Y=160px | x=left@24px         | size=130x160
[Card slot 2]              | top-Y=160px | x=centered          | size=130x160
[Card slot 3]              | top-Y=160px | x=right@24px        | size=130x160
[Card slot 4]              | top-Y=340px | x=left@24px         | size=130x160
[Card slot 5]              | top-Y=340px | x=centered          | size=130x160
[Card slot 6]              | top-Y=340px | x=right@24px        | size=130x160
SELECT / EQUIP             | top-Y=560px | x=centered          | size=260x52
BACK                       | top-Y=790px | x=left@20px         | size=120x44
```

---

### LeaderboardScreen

**A) Background Image**
Deep void black base with a centered vertical pillar of soft blue-white light rising from the bottom edge — evoking a podium spotlight. Upper portion: faint star field. A tall decorative panel frame occupies the central column (X: 30–450, Y: 140–730) — dark navy fill (`#0B1230`, 85% opacity), rounded corners, neon-teal border with subtle outer glow. Inside the panel frame: ten evenly-spaced blank horizontal row shapes (each ~50 px tall, alternating `#0D1535` and `#091025` fills, no text). A thin gold accent stripe (`#FFD84D`) runs along the top edge of the panel frame. Top-3 rows have a very faint gold left-border highlight. No rank numbers, no names, no scores — all drawn in code.

**B) Button Layout**
```
LEADERBOARD (title label)  | top-Y=52px  | x=centered          | size=340x50
[Row 1 – Rank label]       | top-Y=150px | x=left@50px         | size=380x50
[Row 2]                    | top-Y=204px | x=left@50px         | size=380x50
[Row 3]                    | top-Y=258px | x=left@50px         | size=380x50
[Row 4]                    | top-Y=312px | x=left@50px         | size=380x50
[Row 5]                    | top-Y=366px | x=left@50px         | size=380x50
[Row 6]                    | top-Y=420px | x=left@50px         | size=380x50
[Row 7]                    | top-Y=474px | x=left@50px         | size=380x50
[Row 8]                    | top-Y=528px | x=left@50px         | size=380x50
[Row 9]                    | top-Y=582px | x=left@50px         | size=380x50
[Row 10]                   | top-Y=636px | x=left@50px         | size=380x50
BACK                       | top-Y=790px | x=left@20px         | size=120x44
```

---

### WorldsScreen

**A) Background Image**
Star-filled void with three large gas giant thumbnails arranged vertically — each a circular planet preview (≈160 px diameter) centered in a rounded square card frame (~200×220 px). Card 1 (top): teal-cyan planet (current style). Card 2 (middle): amber-orange planet — warm banding in `#FF7A1A`/`#FF4500` hues, semi-transparent "locked" overlay (dark tint at 50%). Card 3 (bottom): violet-magenta planet — purple-pink banding, also locked overlay. Each card has a neon border matching its planet color and a faint glow. The background behind all cards is `#05060F` with a gentle nebula gradient shifting from teal (top-left) to purple (bottom-right). No text labels, no lock icons, no star-count badges — code draws those.

**B) Button Layout**
```
SELECT WORLD (title label) | top-Y=40px  | x=centered          | size=320x50
[World 1 card hit area]    | top-Y=120px | x=centered          | size=200x220
[World 2 card hit area]    | top-Y=360px | x=centered          | size=200x220
[World 3 card hit area]    | top-Y=600px | x=centered          | size=200x220
BACK                       | top-Y=790px | x=left@20px         | size=120x44
```

---

### SettingsScreen

**A) Background Image**
Minimal deep-space background: `#05060F` base, very sparse star field, faint nebula wash in upper-right (`#2D1B69`, 20% opacity). A centered panel frame (X: 40–440, Y: 120–700): dark navy fill, rounded corners (16 px), teal border stroke. Inside the panel, three blank horizontal toggle rows are sketched as decorative shapes — each row is a hairline separator line with subtle alternating row tints; the right side of each row has an empty rounded pill shape (80×36 px) suggesting a toggle control, filled with mid-grey. No text labels on rows, no icons, no active states — code draws all of those.

**B) Button Layout**
```
SETTINGS (title label)     | top-Y=48px  | x=centered          | size=280x50
SOUND (row label)          | top-Y=180px | x=left@60px         | size=200x48
[Sound toggle]             | top-Y=180px | x=right@40px        | size=80x36
MUSIC (row label)          | top-Y=268px | x=left@60px         | size=200x48
[Music toggle]             | top-Y=268px | x=right@40px        | size=80x36
VIBRATION (row label)      | top-Y=356px | x=left@60px         | size=200x48
[Vibration toggle]         | top-Y=356px | x=right@40px        | size=80x36
BACK                       | top-Y=790px | x=left@20px         | size=120x44
```

---

### HowToPlayScreen

**A) Background Image**
Clean tutorial backdrop: `#080E22` base with a very subtle radial vignette darkening edges. A gas giant at ~60% opacity (so it reads as ambient, not distracting) centered in lower-half, roughly 240 px diameter, teal-cyan palette. Four concentric orbital ring ellipses drawn faintly — thin 1-2 px strokes at 40% opacity — suggesting the ring system. Three illustrative moment frames appear as blank rounded-rectangle cards arranged top-to-bottom in the center column (~340×130 px each, spaced 20 px apart), with neon-teal hairline borders and dark navy fills. The three cards serve as illustration panels (code draws arrows and pod positions inside them). Small decorative dots and arrow-chevron shapes in teal (`#1ECFCF`) at 30% opacity float between card panels as connective visual flow. No text, no numbered steps.

**B) Button Layout**
```
HOW TO PLAY (title label)  | top-Y=36px  | x=centered          | size=320x50
[Panel 1 — Tap to jump]    | top-Y=110px | x=centered          | size=340x130
[Panel 2 — Avoid debris]   | top-Y=260px | x=centered          | size=340x130
[Panel 3 — Collect stars]  | top-Y=410px | x=centered          | size=340x130
TAP ANYWHERE label         | top-Y=620px | x=centered          | size=300x44
BACK                       | top-Y=790px | x=left@20px         | size=120x44
```

---

## 4. Export Checklist

```
- icon_512.png (512x512)
- ui/main_menu_screen.png (480x854)
- ui/game_screen.png (480x854)
- ui/game_over_screen.png (480x854)
- ui/shop_screen.png (480x854)
- ui/leaderboard_screen.png (480x854)
- ui/worlds_screen.png (480x854)
- ui/settings_screen.png (480x854)
- ui/how_to_play_screen.png (480x854)
```