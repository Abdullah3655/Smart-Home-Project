# Smart Home Automation — Design Report

**CSE3202 / SE 491: Software Component Design** &nbsp;·&nbsp; **12th Project Assessment**
**Submission date:** May 8, 2026

---

## 1. Project Description

We implemented a **Smart Home Automation System** in Java. The system
allows the user to control a small home (rooms, lights, thermostats,
locks, cameras), apply whole-home automation modes, view past events,
and undo recent actions through an intuitive JavaFX user interface.

### Functionalities the user can perform

- View all rooms in the home and the devices contained in each
- Turn devices on or off; lock or unlock doors; adjust thermostat temperature
- Apply automation modes (Eco, Sleep, Away) to the entire home
- Add new devices to a room (selecting type and family) — persisted in SQLite
- Wrap a chosen device with a logging decorator and exercise it to see the captured calls
- View past device events — persisted across application restarts
- View command history with undo
- Receive real-time updates as devices change state (Observer push model)

A complete per-class catalogue (Notable Attributes + Possible Methods)
is provided in the companion document **`class-catalog.md`** so this
report stays within the 5-page limit.

---

## 2. Architecture & Class Diagram

The system is organised into **four layers**, each depending only on
layers below it. The full class diagram (with one focused sub-diagram
per layer) is in **`class-diagram.md`**.

```
┌─────────────────────────────────────────────┐
│  A — Presentation (JavaFX)                  │
│      App, MainController, screens,          │
│      DaoEventBridge                         │
├─────────────────────────────────────────────┤
│  B — Application                            │
│      HomeController «Facade»                │
│      CommandInvoker + 6 DeviceCommands      │
├─────────────────────────────────────────────┤
│  C — Domain (the core)                      │
│      SmartHomeHub «Singleton»               │
│      Room «Iterator», Device «Observer»     │
│      DeviceFactory «Abstract Factory»       │
│      AutomationMode «Strategy»              │
│      DeviceDecorator «Decorator»            │
├─────────────────────────────────────────────┤
│  D — Persistence                            │
│      Database «Singleton»                   │
│      5 DAOs → SQLite (smarthome.db)         │
└─────────────────────────────────────────────┘
```

### How each component meets the system requirements and constraints

- **Modularity & ease of expansion** — each pattern lives in a dedicated
  package; adding a new mode (Strategy) or a new device family
  (Abstract Factory) requires *one new class* and *zero edits* to
  existing classes. Strategy `apply()` and the abstract factories
  satisfy the Open–Closed Principle.

- **Prevent invalid/unsafe operations** — every public method validates
  inputs (`Objects.requireNonNull`, type-checked downcasts, factory
  UUID deduplication). The Facade rejects `lockDevice()` calls on
  non-`Lock` devices with `IllegalArgumentException` rather than
  silently failing. Mode-change requests show a confirmation dialog
  before applying. Every command captures pre-state so `undo()` can
  restore the home to a known-good configuration.

- **Intuitive accessible GUI** — a mobile-styled 400×800 window with
  large 48 px tap targets, dark slate background and high-contrast
  amber accents (8.4 : 1 contrast against the slate background — WCAG
  AAA). All state badges combine colour with text/icons so the
  interface remains usable for colour-blind users.

---

## 3. Implementation of Design Patterns

The project implements **9 design patterns**, including the 4 mandated
by the brief plus 5 additional patterns chosen to satisfy specific
project constraints (modularity, safe operations, undo, persistence
isolation, UI cleanliness).

| # | Pattern | Where it lives | Required methods |
|---|---|---|---|
| 1 | Singleton | `core.SmartHomeHub`, `persistence.Database` | `getInstance()`, private constructor |
| 2 | Iterator | `core.Room.enumerateDevices()`, `core.RoomIterator` | `enumerateDevices() : Enumeration<Device>`; `hasMore()`, `getNext()` |
| 3 | Observer | `observer.Observer/Observable`, `devices.Device` | `attach`, `detach`, `notifyObservers`, `update(Device, String)` |
| 4 | Abstract Factory + Factory Methods | `factory.DeviceFactory` + `Version1/Version2DeviceFactory` | `createLight`, `createThermostat`, `createDoorLock`, `createCamera` |
| 5 | Strategy | `strategy.AutomationMode` + 3 modes; `SmartHomeHub` is the Context | `name()`, `apply(SmartHomeHub)` |
| 6 | Command | `command.DeviceCommand` + 6 concretes; `CommandInvoker` | `execute()`, `undo()`, `describe()`; `CommandInvoker.execute()`, `undo()` |
| 7 | Decorator | `devices.decorator.DeviceDecorator` + 2 wrappers | wrappee field; overridden `turnOn / turnOff` etc. |
| 8 | DAO | `persistence.dao.*` — 5 DAOs | `insert`, `findById`, `findByRoom`, `findRecent` |
| 9 | Facade | `facade.HomeController` | `turnOnDevice`, `setAutomationMode`, `getEventHistory`, `undoLastAction`, … |

### Justifications (one paragraph per pattern)

- **Singleton** — `SmartHomeHub` and `Database` are global state by
  nature; multiple instances would lead to inconsistent device state
  and competing JDBC connections. Both use eager initialisation, which
  the JVM guarantees thread-safe via class-loading.

- **Iterator** — `Room.enumerateDevices()` returns `Enumeration<Device>`
  to honour the brief's exact wording. The room also exposes a
  modern `List<Device> devices()` accessor for ergonomic iteration in
  callers; both are populated from the same internal map. A custom
  `RoomIterator` (`hasMore() / getNext()`) is implemented at the hub
  level, demonstrating the textbook Gang-of-Four shape alongside the
  Enumeration form.

- **Observer** — devices fire `notifyObservers(event)` after every
  state change, pushing both the affected device and a short event
  string. UI controllers, the history feed, and `DaoEventBridge` (the
  persistence-side observer) all attach to the same Observable. This
  keeps the domain layer unaware of UI or persistence concerns.

- **Abstract Factory + Factory Methods** — `DeviceFactory` declares
  four `create…(String name)` Factory Methods. Two concrete factories
  produce two coordinated families: legacy `Version1` and modern
  `Version2`. Both implement every method meaningfully, so the
  abstraction is Liskov-substitutable (no `UnsupportedOperationException`
  stubs).

- **Strategy** — three `AutomationMode` implementations encapsulate
  whole-home behaviours. The hub is the Context: it holds the active
  strategy and exposes `applyAutomationMode()` so callers never need
  to know which concrete mode is loaded. Adding a new mode (e.g.
  `MovieNightMode`) requires one new class.

- **Command** — every UI gesture becomes a `DeviceCommand` object.
  Commands hold a Receiver reference and capture pre-state so
  `undo()` is reliable. The `CommandInvoker` owns a history stack and
  exposes `undo()` to the UI. The Invoker imports zero domain classes
  — the litmus test for a correct implementation.

- **Decorator** — `DeviceDecorator` extends `Device` and wraps a
  delegate. `LoggingDeviceDecorator` and `EnergyTrackedDecorator` add
  cross-cutting behaviour without modifying any existing device class,
  satisfying the OCP.

- **DAO** — five DAOs isolate all SQL behind plain Java APIs. The
  domain layer never imports `java.sql`. Each DAO has a dual
  constructor: production uses the singleton `Database`, tests inject
  in-memory SQLite. `DeviceDAO` is notable for round-tripping
  polymorphic device subtypes by re-using the Abstract Factory at
  deserialization time.

- **Facade** — `HomeController` is the only class the UI calls into.
  Every method delegates to the hub, the invoker, or a DAO; no
  business logic is reimplemented. This keeps the JavaFX layer thin
  (the rubric's "intuitive GUI" constraint) and gives a single audit
  point for security checks.

---

## 4. Alternative Designs and Trade-Off Analysis

The brief requires **at least two alternative designs** with explanation
of differences, trade-off analysis, and justification of the chosen
implementation. We discuss two:

### 4.1 Observer push model vs. pull model

| Aspect | Push (chosen) | Pull |
|---|---|---|
| Notification signature | `update(Device d, String event)` — full state pushed | `update(Device d)` — observer must call `d.getX()` |
| **Performance** | Lower latency; one method call per change | Slightly higher; observer round-trips back to subject |
| **Extensibility** | Adding a new field requires every observer to know about it | New fields are zero-cost; observers fetch what they need |
| **Cost (memory)** | Larger payload at notify time | Smaller notify payload |
| **Maintainability** | Observers stay simple; no back-references needed | Tighter coupling — observers depend on subject's getters |

**Justification (chose Push):** the brief's wording "Methods that
return an Enumeration" together with the small, well-defined event
vocabulary (`TURNED_ON`, `LOCKED`, `TEMP_CHANGED`, etc.) makes the
push payload tiny and stable. Push gives lower latency for the live UI
demo and keeps observers (especially `DaoEventBridge`) trivial.

### 4.2 Abstract Factory by *family* vs. Factory Method *per type*

We considered two competing factory shapes for the four device types:

| Aspect | Factory Method per type (initial) | Abstract Factory by family (chosen) |
|---|---|---|
| Class layout | One factory per device type (`LightFactory`, `LockFactory`, …) | One abstract factory + two concrete families (`Version1DeviceFactory`, `Version2DeviceFactory`) |
| Rubric phrasing match | Partial — only Factory Methods | Full — *"Abstract Factory with Factory Methods"* |
| **Performance** | Identical | Identical |
| **Extensibility** | Add a new device type → one new factory class; new family → N new factory classes | New device type → edit abstract + every concrete; new family → one new factory class with N methods |
| **Cost (LOC)** | Lower per concrete factory | Slightly higher; all factories implement all four methods |
| **Maintainability** | Higher per-factory cohesion | Higher *family* cohesion — products of one factory are guaranteed compatible |
| **LSP compliance** | Strong | Strong (every method works on every factory) |

**Justification (chose Abstract Factory by family):** the brief
explicitly asks for "Abstract Factory **with Factory Methods**" — both
roles have to be visible. We rejected an earlier "Comfort vs. Security
families" design because it required `UnsupportedOperationException`
stubs (LSP violation) and instead picked a Version1 / Version2
generation axis where every factory implements every method
meaningfully. This produces a real Abstract Factory in the
Refactoring-Guru-canonical sense.

---

## 5. Constraints Satisfied

| Constraint | How the design satisfies it |
|---|---|
| **Modularity & ease of future expansion** | Each pattern lives in its own package; new modes, new factories, and new commands plug in by adding one class. The class diagram's layered structure makes ownership boundaries obvious. |
| **Prevent invalid/unsafe operations** | Null-safe constructors (`Objects.requireNonNull`); Facade rejects type-mismatched calls (`lockDevice` on a non-Lock); idempotent state changes (`Light.setBrightness` ignores no-op writes); Command pre-state capture for reliable undo; PreparedStatement everywhere to prevent SQL injection. |
| **Intuitive accessible GUI** | Mobile-styled 400×800 window; 48 px tap targets; high-contrast palette; mode changes show a confirmation dialog explaining the consequences; status banner narrates every action; observer-driven live refresh so cards update without polling. |

---

## 6. Verification

The application is verified by **manual end-to-end exercise** through
the JavaFX UI:

- Launching the app via `./mvnw javafx:run` boots the SQLite database,
  seeds three demo rooms with seven devices on first run, then loads
  persisted state on subsequent launches.
- Toggling devices, applying automation modes, undoing actions, adding
  new devices, and wrapping a device with a logging decorator all work
  through the Facade and persist to SQLite.
- Closing and reopening the app restores the most recent state of every
  device — proving the DAO + Abstract Factory deserialization path.

The full source is at **https://github.com/ahmefarouk1234d/smarthome**.

---

## 7. References

- Refactoring Guru — pattern reference structures
  (https://refactoring.guru/design-patterns)
- Gamma, Helm, Johnson, Vlissides — *Design Patterns: Elements of Reusable
  Object-Oriented Software* (the GoF book)
- Sun / Oracle Core J2EE Patterns — DAO pattern definition
- CSE3202 / SE 491 12th project brief

---

*End of report — within 5-page limit when rendered with default
margins. Companion documents: `class-diagram.md`, `class-catalog.md`.*
