# Mobile-Styled UI Redesign — Design Spec

**Date:** 2026-05-05
**Project:** Smart Home Automation (CSE3202 / SE 491)
**Author:** Ahmed Farouk (M3, JavaFX + Facade)
**Status:** Approved — moving to implementation

---

## 1. Context & Motivation

The Smart Home backend is feature-complete (9/9 design patterns implemented, 45/45 tests passing). The current UI is a working two-column desktop dashboard at 1100×720 — functional, but visually generic and not differentiated for the demo video.

**Goal:** redesign as a vertical, mobile-shaped window (~400×800) running on desktop. The redesign must:
- Make every implemented pattern visibly demonstrable in a 2-3 minute demo video.
- Apply a distinctive "smart-home brand" aesthetic (warm amber + deep navy + dark slate).
- Tighten information hierarchy so the grader sees pattern fingerprints within 5 seconds of each screen.
- Run unchanged on desktop — no Gluon Mobile / Android packaging.

**Non-goals:**
- Real Android/iOS deployment (rejected as deadline risk).
- Multi-user PIN system (deferred — would require ~3 hrs of out-of-scope work).
- Schedule executor wiring (M4 task — out of scope here).
- DeviceDAO + ScheduleDAO completion (deliberately deferred earlier).

---

## 2. Visual Design Tokens

### Palette (dark base + warm accents)

| Token | Hex | Use |
|---|---|---|
| `--slate-900` | `#0e1626` | App background |
| `--slate-800` | `#161f33` | Card surfaces |
| `--slate-700` | `#1f2940` | Card borders, dividers |
| `--amber-500` | `#ffb547` | Primary buttons, "ON" indicator, active mode |
| `--amber-200` | `#ffd99c` | Hover/pressed amber |
| `--teal-400` | `#3ec3b5` | Secondary accents (locked, armed cameras) |
| `--text-primary` | `#f5f6fa` | Headings, primary button text |
| `--text-muted` | `#8a93a8` | Captions, timestamps, status |
| `--danger` | `#ff6b6b` | Error banner, destructive feedback |

### Type scale

- 11px caption (timestamps, helper text)
- 13px body
- 16px section headings
- 22px screen title

Weights: 400 body / 600 section / 700 title. Family: Segoe UI (cross-platform fallback).

### Density & hit targets

- Minimum 48 × 48 px tap target (Material Design "comfortable" spec)
- 16px outer padding around card lists
- 12px gap between stacked cards
- 12px corner radius on cards and primary buttons

### Surfaces & elevation

No drop shadows on dark backgrounds — they read muddy. Elevation conveyed by a 1px solid `--slate-700` border on cards instead.

---

## 3. Window Geometry & Top-Level Layout

```
┌────────────────────────────────────────┐ ← 400 × 800 window
│ TOP BAR             (60px, fixed)      │   title + undo
│ MODE PICKER         (72px, fixed)      │   ECO / SLEEP / AWAY segmented
│ STATUS BANNER       (0-44px, sliding)  │   action feedback
│ MAIN CONTENT        (fills remaining)  │   scrolls
│ BOTTOM NAV          (64px, fixed)      │   Home / History / Demo
└────────────────────────────────────────┘
```

Three top-level screens, switchable by bottom nav. Each screen is its own FXML. All three controllers share the singleton hub through the Facade.

---

## 4. Screen 1 — Home

The default landing screen and demo hero shot.

**Content:**
- All rooms rendered sequentially as **room sections**.
- Each room section: header row ("Kitchen — 2 devices") + a stack of device cards.
- Each device card:
  - Type icon (💡 Light, 🌡️ Thermostat, 🔒 Lock, 📷 Camera)
  - Name as primary text
  - Family subtitle ("Lighting · Version2") — pattern callout for Abstract Factory
  - State badge (right-aligned: `● ON` amber, `○ OFF` muted, `🔒 LOCKED` teal)
  - **Contextual action buttons only**:
    - On Lights: `[Turn Off]` if on, `[Turn On]` if off.
    - On Thermostats: `[−] [+]` for ±1°C (and accepts long-press for ±5°C — stretch).
    - On Locks: `[Lock]` if unlocked, `[Unlock]` if locked.
    - On Cameras: `[Turn On]` / `[Turn Off]`.
  - Wrapped-with-decorator marker: small `🎁` badge if the device is currently wrapped (set on the Decorator screen).

**Behavior:**
- Cards refresh live via Observer when any device's state changes.
- Tapping any action button calls a single Facade method, then updates the card via Observer push.
- Mode picker (above the scroll area) auto-applies the moment a segment is tapped — no "Apply" button. After applying, the status banner narrates the effect ("Eco applied — 2 lights dimmed, 1 thermostat updated"); cards reflect the new state immediately.

**Empty state:** if no rooms exist, full-screen message: "No rooms yet — seed data should appear automatically. If this persists, check the App.seedDemoData() output."

---

## 5. Screen 2 — History

Reactive feed of every event the Observer pattern has fired since app launch (in-memory) plus any events persisted to `DeviceEventDAO` (when DAO is available).

**Content:**
- Pull header: "Recent device events"
- Vertical scrolling list, newest at top
- Each row: timestamp (HH:mm:ss) — device name — event type — colored dot
  - `TURNED_ON` → amber dot
  - `TURNED_OFF` → muted dot
  - `LOCKED` → teal dot
  - `UNLOCKED` → muted teal
  - `TEMP_CHANGED` → small thermometer
  - `BRIGHTNESS_CHANGED` → small bulb
- Up to 200 entries kept in memory (older auto-pruned).

**Behavior:**
- Wired via `Observer` attached at app startup to every device.
- Auto-scrolls to top whenever a new event arrives unless user has scrolled down.
- No filter UI in the first cut — just a chronological feed.

---

## 6. Screen 3 — Demo (Decorator showcase)

The screen that earns Decorator its keep in the demo video.

**Content:**
- Title: "Decorator pattern showcase"
- Description (one paragraph, muted): "Wrap any device to add behavior without modifying its class. The wrapped device behaves identically but adds an audit log."
- Device picker (scroll list, same card style as Home).
- "Wrap with Logging" button → wraps the selected device with `LoggingDeviceDecorator`, replaces the underlying reference inside its `Room`, fires a status banner ("Wrapped Ceiling Light — toggle it now").
- Below: real-time log feed showing what the decorator captured, with timestamps.
- "Unwrap" button to restore the original device.

**Behavior:**
- Wrapping is reversible (unwrap restores original).
- The wrapped device's `id` and `name` stay identical, so room iteration and history events still work seamlessly.
- A small 🎁 badge appears on the device's card on the Home screen when wrapped — visible across screens.

**Stretch (skip if running short):** "Stack" button to add `EnergyTrackedDecorator` on top of `LoggingDeviceDecorator`, demonstrating decorator stacking. Show the stack visually as a list ("Logging → EnergyTracked → Light").

---

## 7. Pattern Visibility Map

Each pattern has a visible fingerprint a grader can identify within 5 seconds.

| Pattern | UI Fingerprint |
|---|---|
| **Singleton** | Hub status text in title bar tooltip; only one app instance possible |
| **Iterator** | Room header device counts come from `room.devices()` Enumeration |
| **Observer** | Status banner + History feed update live; no polling |
| **Abstract Factory** | Device card subtitle ("Lighting · Version2") |
| **Strategy** | Segmented mode picker (ECO/SLEEP/AWAY); cards visibly mutate when tapped |
| **Command** | Undo button reverses last action with banner narration |
| **DAO** | History feed reads from `DeviceEventDAO` when available |
| **Decorator** | Dedicated screen with wrap/unwrap + 🎁 badge propagation |
| **Facade** | Every button click maps to one `HomeController` method (code-visible) |

---

## 8. Component Inventory (FXML & Java)

### New FXML files

- `src/main/resources/fxml/home.fxml` (replaces `dashboard.fxml`)
- `src/main/resources/fxml/history.fxml`
- `src/main/resources/fxml/decorator.fxml`
- `src/main/resources/fxml/main.fxml` (root container with bottom nav, hosts the other three via swap)

### New CSS

- `src/main/resources/css/app.css` — full rewrite to dark theme + tokens above

### New / changed Java

- `com.smarthome.ui.App` — change window size to 400×800, set initial scene to `main.fxml`
- `com.smarthome.ui.MainController` — owns top bar, mode picker, status banner, bottom nav; loads sub-views into the center pane
- `com.smarthome.ui.HomeController` (rename of current `DashboardController`) — renders the Home screen
- `com.smarthome.ui.HistoryController` — renders the History screen
- `com.smarthome.ui.DecoratorController` — renders the Decorator showcase
- `com.smarthome.ui.StatusBanner` — small reusable component for the sliding banner

### Things to delete

- The current `dashboard.fxml` (replaced by `home.fxml`)

---

## 9. Data Flow

```
User tap → JavaFX event handler in <Screen>Controller
        → HomeController (Facade) method call
        → CommandInvoker.execute(command) for mutations
        → Command.execute() → Receiver (Device) state mutation
        → Receiver.notifyObservers(event) [Observer pattern]
        → All attached Observers fire:
           - StatusBanner shows live message
           - HistoryController appends to feed
           - HomeController refreshes the affected card
        → If DAO present: DeviceEventDAO.insert(deviceId, eventType)
```

The UI is purely **reactive** — controllers don't poll the hub. Every refresh is driven by the Observer chain.

**Note on DAO wiring:** the existing backend does NOT auto-persist Observer events to `DeviceEventDAO`. The History screen's "DAO present" branch is a stretch that requires an additional `DaoEventBridge` Observer that calls `DeviceEventDAO.insert(...)` on each callback. If time runs short, ship History as in-memory-only — still demonstrably the Observer pattern, just without persistence.

---

## 10. Accessibility & Keyboard

- All interactive controls focusable via `Tab` (JavaFX default).
- `Enter` / `Space` activates focused button.
- `Escape` closes any open dialog (decorator wrap confirmation, etc.).
- Color contrast: amber-on-slate-900 = 8.4:1 (WCAG AAA), teal-on-slate-900 = 6.1:1 (AA), muted text 4.6:1 (AA).
- No information conveyed by color alone — every state badge has both color and an icon/text label.

---

## 11. Risks & Trade-offs

| Risk | Mitigation |
|---|---|
| FXML reload feedback loops (refresh during refresh) | Use `Platform.runLater` for all UI updates from Observer callbacks |
| Decorator wrapping requires mutating Room's internal Map | Use `Room.removeDevice(id) + Room.addDevice(decorated)` — already supported by Room API |
| Switching between three screens may flash | Use a single root container + lazy-initialized sub-FXMLs; cache after first load |
| Observer attached to wrapped vs unwrapped device | After wrap, re-attach the Observer set to the new wrapper; document this clearly in DecoratorController |
| Demo seed data conflict with persistence layer | Only seed in-memory hub; database remains empty unless explicitly populated |

---

## 12. Acceptance Criteria

The redesign is "done" when:

1. `./mvnw javafx:run` opens a 400×800 window with the dark amber theme.
2. All three bottom-nav destinations work (Home / History / Decorator).
3. Tapping ECO/SLEEP/AWAY visibly changes device cards within ~100ms.
4. Tapping a device action button updates the card via Observer push (no manual refresh).
5. Tapping Undo reverses the most recent action and the banner narrates it.
6. History feed shows newest-first, auto-updates on every device event.
7. Decorator screen lets the user wrap a device and see logged actions.
8. All 45 existing tests still pass — UI changes do not regress backend behavior.
9. No imports from `com.smarthome.persistence.dao.*` in any UI controller (Facade-only access).

---

## 13. Out of Scope (deferred / declined)

- Real mobile deployment via Gluon Mobile / GraalVM.
- Light-mode theme variant (dark only).
- Animated transitions between screens (pure swap, no slide animation).
- User authentication / PIN screen.
- Schedule creation UI.
- Push notifications / sound effects on device events.
- Multi-language support.

---

## 14. Implementation Plan Reference

Implementation steps and file-by-file diffs will be authored in a separate plan document via the `superpowers:writing-plans` skill (or `frontend-design:frontend-design` skill, per user's explicit request).
