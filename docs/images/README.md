# Report screenshots

This folder contains the visuals embedded in `../report.md` under
"Screenshots — GUI in action".

## What's here now

5 SVG mockups generated from the actual app's design tokens (same
colours, spacing, fonts, layout as the running JavaFX UI). They render
crisply in GitHub, in any markdown→PDF tool, and in Word.

| File | Shows |
|---|---|
| `home.svg` | Home screen — rooms with device cards, status banner, mode picker |
| `mode-confirm.svg` | The confirmation dialog over the dimmed Home screen |
| `history.svg` | History tab feed of device events |
| `decorator.svg` | Decorator showcase with wrap/unwrap and captured log |
| `add-device.svg` | Add Device modal showing type/family/name fields |

## Replacing with real screenshots (optional)

If you want to swap in real screenshots from the running app:

1. `cd "D:\SOFTWATE COMPONENT\smarthome"` and run `./mvnw javafx:run`
2. Navigate to each screen listed below; capture with `Win+Shift+S`
3. Save under this folder with the matching base name (e.g.
   `home.png` instead of `home.svg`)
4. In `../report.md`, replace `.svg` with `.png` in the matching
   `![…](images/…)` lines

| Filename | How to get there |
|---|---|
| `home.png` | Default landing screen on launch |
| `mode-confirm.png` | Tap ECO/SLEEP/AWAY in the top bar |
| `history.png` | Tap a few devices, then tap 📋 History in bottom nav |
| `decorator.png` | Tap 🎁 Demo, pick a device, "Wrap with Logging", then "Turn On (wrapped)" |
| `add-device.png` | Tap "+ Add device to {Room}" at the bottom of any room |

## Why SVG by default

- **Resolution-independent** — scales to any size without pixelation
- **Tiny file size** — each is <5 KB vs. ~50–200 KB for PNG screenshots
- **Renders in markdown→PDF tools that support Mermaid** (most do support SVG)
- **Diff-friendly** — text-based, so changes show up cleanly in git history
