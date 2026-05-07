# Project documentation

| File | Purpose |
|---|---|
| [`class-diagram.md`](class-diagram.md) | **Primary class diagram** — Mermaid + layered architecture view. Renders inline on GitHub. |
| [`class-diagram.puml`](class-diagram.puml) | PlantUML source — exports to high-resolution PNG/SVG for the printed report. |
| [`superpowers/specs/`](superpowers/specs/) | Design specifications kept under version control. |

---

## How to render the class diagram

### Option 1 — view on GitHub (no setup, fastest)

Just open `class-diagram.md` on github.com:
> https://github.com/ahmefarouk1234d/smarthome/blob/main/docs/class-diagram.md

GitHub auto-renders Mermaid blocks as SVG. Take a screenshot of the rendered diagram for the report (Win+Shift+S).

### Option 2 — render PlantUML to PNG/SVG (sharper for print)

**Easiest (browser):**
1. Open https://www.plantuml.com/plantuml/uml
2. Paste the contents of `class-diagram.puml`
3. Click "Submit" → diagram renders → right-click "Save image as…"

**VS Code:**
1. Install the "PlantUML" extension (jebbs.plantuml).
2. Open `class-diagram.puml`.
3. Press `Alt+D` → preview opens.
4. Right-click the preview → "Export Current Diagram" → choose PNG.

**draw.io / diagrams.net:**
1. Open https://app.diagrams.net/
2. File → Import from → Device → pick `class-diagram.puml`
3. Edit/style the result, export PNG/SVG via File → Export As.

### Option 3 — local PlantUML

```powershell
# One-time setup (Windows, with Chocolatey)
choco install plantuml graphviz

# Render
plantuml docs/class-diagram.puml
# Produces docs/SmartHomeClassDiagram.png
```

---

## What the diagram shows

A single-page overview of all 9 design patterns and how the key classes relate:

- **Singleton** — `SmartHomeHub`, `Database`
- **Iterator** — `Room.devices()` returning `Enumeration<Device>`, plus a custom `RoomIterator`
- **Observer** — `Device` ↔ `Observer`/`Observable`, with `DaoEventBridge` as the persistence-side observer
- **Abstract Factory + Factory Methods** — `DeviceFactory` with `Version1` and `Version2` concrete factories
- **Strategy** — `AutomationMode` with `Eco`/`Sleep`/`Away` modes; `SmartHomeHub` is the Context
- **Command** — `DeviceCommand` interface, 6 concrete commands, `CommandInvoker` with undo
- **Decorator** — `DeviceDecorator` stackable wrappers (`LoggingDeviceDecorator`, `EnergyTrackedDecorator`)
- **DAO** — 5 DAOs isolating SQL behind a Java API
- **Facade** — `HomeController` (in `facade` package) — single entry point for the UI

Stereotypes (`«Singleton»`, `«Strategy»`, etc.) tag each class with its pattern role so the diagram is self-describing.

---

## Pairing with the report

Each pattern in the report's "Pattern X" sections should reference this diagram:

> *"The Singleton pattern is implemented in `SmartHomeHub` (see class diagram, top-left). The locked
> contract — private constructor + static `getInstance()` — guarantees a single
> domain hub for the application's lifetime."*

This way the diagram is the **single source of truth** for class names and pattern roles, and the
report's prose stays focused on *why* each pattern was chosen.
