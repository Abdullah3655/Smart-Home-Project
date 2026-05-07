# Smart Home Automation — Design Report

**CSE3202 / SE 491 — 12th Project Assessment** &nbsp;·&nbsp; **Submission: May 8, 2026**
**Repository:** https://github.com/ahmefarouk1234d/smarthome

> **Option 1** — main report stays in 5 pages. Per-pattern detailed
> diagrams live in **Appendix A** after the References. The 5-page
> rubric limit applies to the *report describing patterns*; the
> appendix is supplementary detail material.

---

## 1. Project Description

A simplified **Smart Home Automation System** in Java. The user
navigates rooms, controls devices (lights, thermostats, locks, cameras),
applies whole-home automation modes, views past events, and undoes
recent actions through an intuitive JavaFX UI.

**Users can:** list rooms · enter a room and view its devices · turn
devices on/off · lock/unlock doors · adjust thermostat temperature ·
apply automation modes (Eco/Sleep/Away) · add new devices · wrap a
device with a logging decorator · view persisted device events · undo
the most recent action · receive real-time state-change notifications.

---

## 2. Class Descriptions

`SmartHomeHub` — Singleton + Strategy Context + Iterator aggregate.
**Attrs:** `INSTANCE`, `roomsById`, `automationMode`.
**Methods:** `getInstance()`, `addRoom`, `getRoom`, `getRooms`,
`enumerateRooms() : Enumeration<Room>`, `setAutomationMode`,
`applyAutomationMode()`, `createIterator() : RoomIterator`.

`Room` — Iterator host; aggregates devices.
**Attrs:** `roomId`, `name`, `devicesById`.
**Methods:** `addDevice`, `removeDevice`, `getDevice`,
`devices() : List<Device>`, `enumerateDevices() : Enumeration<Device>`.

`Device` (abstract) — Subject (Observer), Receiver (Command), Component (Decorator).
**Attrs:** `id`, `name`, `poweredOn`, `observers`.
**Methods:** `turnOn`, `turnOff`, `attach`, `detach`, `notifyObservers`.
**Concrete subclasses:** `Light` (`setBrightness`), `Thermostat`
(`setTemperature`), `Lock` (`lock`/`unlock`), `Camera`. Each has two
family variants (`Version1*`, `Version2*`) produced by the
corresponding `DeviceFactory`.

`DeviceFactory` (abstract) — Abstract Factory with four Factory Methods:
`createLight`, `createThermostat`, `createDoorLock`, `createCamera`.
Concretes: `Version1DeviceFactory`, `Version2DeviceFactory`.

`AutomationMode` (interface) — Strategy. Methods: `name()`,
`apply(SmartHomeHub)`. Concretes: `EcoMode` (24°C, dim 50%), `SleepMode`
(off + lock + 20°C), `AwayMode` (off + lock + arm + 15°C).

`DeviceCommand` (interface) — Command. Methods: `execute`, `undo`,
`describe`. Six concretes (`TurnOn/TurnOff/SetTemperature/Lock/Unlock/SetAutomationMode`).
`CommandInvoker` runs commands and owns the undo stack.

`HomeController` — Facade; the UI's only entry point. Methods:
`turnOnDevice/turnOffDevice/lockDevice/unlockDevice/setTemperature/setAutomationMode/getDevicesForRoom/getEventHistory/getCommandHistory/undoLastAction`.

`Database` — Singleton wrapping the SQLite connection. `getInstance()`,
`getConnection()`. Five DAOs (`UserDAO`, `RoomDAO`, `DeviceDAO`,
`DeviceEventDAO`, `CommandsLogDAO`) isolate SQL.

*A more detailed per-class catalogue is in `class-catalog.md`.*

---

## 3. Class Diagram and how each component meets the constraints

Full rendered class diagram (PlantUML — every class, all 9 patterns,
every relationship):

<p align="center">
  <img src="images/class-diagram-full.png" alt="Full class diagram of the Smart Home system" width="900"/>
</p>

The system is organised into **four layers**, each depending only on
layers below it. Per-pattern detailed sub-diagrams are in **Appendix A**.

- **Modularity & ease of expansion** — patterns isolate concerns by
  package. Adding a new mode (Strategy), device family (Abstract
  Factory), or command (Command) requires *one new class* with zero
  edits to existing code (Open–Closed Principle).
- **Prevent invalid/unsafe operations** — `Objects.requireNonNull`
  guards, type-checked Facade rejects (`lockDevice` on a non-Lock
  throws), idempotent state changes, Command pre-state capture for
  reliable undo, `PreparedStatement` everywhere (no SQL injection).
- **Intuitive accessible GUI** — mobile-styled 400×800 window; 48 px
  tap targets; high-contrast palette (8.4 : 1 — WCAG AAA); state badges
  combine colour with text/icons.

---

## 4. Implementation of Design Patterns (9 total)

| # | Pattern | Where it lives | Required methods |
|---|---|---|---|
| 1 | **Singleton** | `core.SmartHomeHub`, `persistence.Database` | `getInstance()`, private constructor |
| 2 | **Iterator** | `Room.enumerateDevices()`, `SmartHomeHub.enumerateRooms()`, `core.RoomIterator` | `enumerateDevices() : Enumeration<Device>`, `enumerateRooms() : Enumeration<Room>`, `hasMore()`, `getNext()` |
| 3 | **Observer** | `observer.Observer/Observable`, `devices.Device` | `attach`, `detach`, `notifyObservers(String)`, `update(Device, String)` |
| 4 | **Abstract Factory + Factory Methods** | `factory.DeviceFactory` + `Version1/Version2DeviceFactory` | `createLight`, `createThermostat`, `createDoorLock`, `createCamera` |
| 5 | **Strategy** | `strategy.AutomationMode` + 3 modes; `SmartHomeHub` is the Context | `name()`, `apply(SmartHomeHub)` |
| 6 | **Command** | `command.DeviceCommand` + 6 concretes; `CommandInvoker` | `execute()`, `undo()`, `describe()`; `CommandInvoker.execute/undo/canUndo` |
| 7 | **Decorator** | `devices.decorator.DeviceDecorator` + 2 wrappers | `wrappee` field; overridden `turnOn/turnOff` |
| 8 | **DAO** | `persistence.dao.*` (5 DAOs) | `insert`, `findById`, `findByRoom`, `findRecent` |
| 9 | **Facade** | `facade.HomeController` | `turnOnDevice`, `setAutomationMode`, `getEventHistory`, `undoLastAction`, … |

**Justifications.** *Singleton*: hub and database are global state.
*Iterator*: returns `Enumeration` per the brief, plus a custom GoF
`RoomIterator`. *Observer* (push): devices push events to UI, history
feed, and `DaoEventBridge` without coupling. *Abstract Factory*: two
coordinated families (Version1/Version2), every factory implements every
method (Liskov-substitutable). *Strategy*: hub holds the active mode;
new modes plug in without editing hub code. *Command*: every action is
an object with reliable undo; Invoker imports zero domain classes.
*Decorator*: stackable wrappers add behaviour without modifying any
device class. *DAO*: SQL isolated behind plain Java APIs. *Facade*:
single UI entry routing through subsystems.

---

## 5. Alternative Designs and Trade-Off Analysis

### 5.1 Observer Push vs. Pull

| Aspect | Push (chosen) | Pull |
|---|---|---|
| Notify signature | `update(Device d, String event)` | `update(Device d)` |
| **Performance** | Lower latency | Slightly higher (round-trip) |
| **Extensibility** | New fields force observer changes | Zero-cost field additions |
| **Cost** | Larger payload at notify | Smaller notify payload |
| **Maintainability** | Simple observers | Tight subject-coupling |

*Justification:* the small fixed event vocabulary (TURNED_ON, LOCKED,
TEMP_CHANGED, …) makes the push payload tiny and stable. Push gives
lower UI-refresh latency and keeps `DaoEventBridge` trivial.

### 5.2 Abstract Factory by family vs. Factory Method per type

| Aspect | Factory Method per type | Abstract Factory by family (chosen) |
|---|---|---|
| Class layout | One factory per device type | Abstract + 2 concrete families |
| Rubric phrasing | Partial — only Factory Methods | Full — *"Abstract Factory with Factory Methods"* |
| **Performance** | Identical | Identical |
| **Extensibility** | Cheap new types; expensive new families | Cheap new families; medium new types |
| **Cost (LOC)** | Lower per concrete factory | Slightly higher (all 4 methods per factory) |
| **Maintainability** | Per-factory cohesion | Family cohesion (compatible products) |

*Justification:* the brief explicitly demands "Abstract Factory **with**
Factory Methods" — both must be visible. We rejected an earlier
"Comfort vs. Security" axis because it required
`UnsupportedOperationException` stubs (LSP violation). Version1 /
Version2 generations let every factory implement every method
meaningfully.

---

## 6. Constraints Satisfied

| Constraint | How |
|---|---|
| **Modularity & expansion** | Patterns + per-package boundaries; new modes/factories/commands plug in by adding one class. |
| **Prevent invalid/unsafe ops** | Null guards, type-checked Facade rejects, idempotent state changes, Command pre-state for undo, prepared SQL. |
| **Intuitive accessible GUI** | Mobile-styled, 48 px tap targets, WCAG AAA contrast, mode-change confirmation dialogs, observer-driven live refresh. |

---

## 7. Screenshots — GUI in action

<table>
  <tr>
    <td align="center"><img src="images/home.svg" width="180" alt="Home"/><br/><b>Home</b><br/>Rooms + device cards</td>
    <td align="center"><img src="images/mode-confirm.svg" width="180" alt="Mode confirm"/><br/><b>Mode confirm</b><br/>Strategy with consequence dialog</td>
    <td align="center"><img src="images/history.svg" width="180" alt="History"/><br/><b>History</b><br/>Observer + DAO live feed</td>
  </tr>
  <tr>
    <td align="center"><img src="images/decorator.svg" width="180" alt="Decorator"/><br/><b>Decorator</b><br/>Wrap + captured log</td>
    <td align="center"><img src="images/add-device.svg" width="180" alt="Add device"/><br/><b>Add device</b><br/>Abstract Factory at runtime</td>
    <td align="center" style="font-size:small; color:#5a6478">Run with<br/><code>./mvnw javafx:run</code><br/>Window: 400×800</td>
  </tr>
</table>

---

## 8. References

- Refactoring Guru — pattern reference structures (https://refactoring.guru/design-patterns)
- Gamma, Helm, Johnson, Vlissides — *Design Patterns: Elements of Reusable Object-Oriented Software*
- Sun / Oracle Core J2EE Patterns — DAO definition

*Companion documents (also in the repo): `class-diagram.md`, `class-catalog.md`.*

---
---

# Appendix A — Per-Pattern Detail Diagrams

Detailed class diagrams for each pattern, isolated so the roles and
relationships are visible without surrounding noise. Supplementary
material — not part of the 5-page core report.

### A.1 Layer A — Presentation (UI)

<p align="center"><img src="images/pattern-presentation.png" width="540"/></p>

The UI is composed of `App` (JavaFX entry), `MainController`, three
screen controllers, the `AddDeviceController` modal, and the
`DaoEventBridge` boundary adapter that forwards device events to the
persistence layer.

### A.2 Layer B — Application (Facade + Command)

<p align="center"><img src="images/pattern-application.png" width="540"/></p>

`HomeController` (Facade) is the only class the UI calls. Each mutation
routes through `CommandInvoker`, which executes a concrete
`DeviceCommand` and pushes it onto the undo stack.

### A.3 Domain core — Singleton + Iterator + base devices

<p align="center"><img src="images/pattern-core.png" width="540"/></p>

`SmartHomeHub` is the Singleton + Strategy Context. `Room` is the
Iterator host (`enumerateDevices()` returns `Enumeration<Device>`).
`RoomIterator` provides the custom GoF Iterator interface.

### A.4 Observer — Device as Subject

<p align="center"><img src="images/pattern-observer.png" width="500"/></p>

`Device` implements `Observable`; observers attach via
`attach(Observer)` and receive `update(Device, String)` calls.

### A.5 Abstract Factory — two device families

<p align="center"><img src="images/pattern-abstract-factory.png" width="540"/></p>

`DeviceFactory` declares four Factory Methods.
`Version1DeviceFactory` and `Version2DeviceFactory` each implement all
four, returning their family's variants.

### A.6 Strategy — automation modes

<p align="center"><img src="images/pattern-strategy.png" width="540"/></p>

`AutomationMode` is the Strategy interface; `EcoMode/SleepMode/AwayMode`
are concretes; `SmartHomeHub` is the Context.

### A.7 Decorator — wrapping devices

<p align="center"><img src="images/pattern-decorator.png" width="540"/></p>

`DeviceDecorator` wraps a `Device` (Component) and forwards every
method. `LoggingDeviceDecorator` and `EnergyTrackedDecorator` add
cross-cutting behaviour without modifying any device class.
