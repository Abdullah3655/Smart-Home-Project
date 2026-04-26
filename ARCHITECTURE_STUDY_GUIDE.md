# Smart Home Study Guide (Simple)

This document explains the parts you asked about in simple terms:

- JavaFX
- Facade
- Command
- Iterator
- Strategy
- DAO
- SQLite basics
- Required alternative designs
- How layers integrate end-to-end

## Architecture Diagram

```mermaid
flowchart TB
  subgraph presentation [Presentation Layer]
    JavaFX[JavaFX UI]
    FxController[FXML Controllers]
  end

  subgraph application [Application Layer]
    HomeFacade[facade.HomeController]
    CommandInvoker[command.CommandInvoker]
    CommandObjects[Command objects]
  end

  subgraph domain [Domain Layer]
    SmartHomeHub[core.SmartHomeHub]
    RoomIterator[core.Room devices() Enumeration]
    DeviceEntities[devices.*]
    ObserverContracts[observer.*]
    AutomationStrategy[strategy.AutomationMode]
    DeviceFactory[factory.*]
    DeviceDecorators[devices.decorator.*]
  end

  subgraph persistence [Persistence Layer]
    DaoLayer[persistence.dao.*]
    DatabaseSingleton[persistence.Database]
    SQLiteDB[(SQLite)]
  end

  JavaFX --> FxController
  FxController --> HomeFacade
  HomeFacade --> CommandInvoker
  CommandInvoker --> CommandObjects
  CommandObjects --> SmartHomeHub

  SmartHomeHub --> RoomIterator
  SmartHomeHub --> DeviceEntities
  SmartHomeHub --> AutomationStrategy
  DeviceFactory --> DeviceEntities
  DeviceDecorators --> DeviceEntities
  DeviceEntities --> ObserverContracts

  ObserverContracts --> DaoLayer
  CommandObjects --> DaoLayer
  DaoLayer --> DatabaseSingleton
  DatabaseSingleton --> SQLiteDB
```

## 1) JavaFX (GUI)

JavaFX is the visual layer of your project (the screens/buttons/tables).

- **FXML**: layout files (what appears on screen).
- **Controller classes**: handle button clicks and user actions.
- **CSS**: style/colors/spacing.

In this architecture, JavaFX should be **thin**:
- Read input from user.
- Call `facade.HomeController`.
- Display updated data.
- Do not contain business rules or SQL.

## 2) Facade Pattern (`facade.HomeController`)

Facade means: **one simple entry point** for a complex system.

Instead of GUI talking directly to many classes (`SmartHomeHub`, DAOs, command classes), GUI talks to one class:

- `HomeController.turnOnDevice(...)`
- `HomeController.setAutomationMode(...)`
- `HomeController.getEventHistory(...)`

Why useful:
- GUI code stays clean.
- Internals can change without breaking UI.
- Strong SRP/DIP (SOLID).

## 3) Command Pattern (`command.*` + `CommandInvoker`)

Command means each action is an object.

Examples:
- `TurnOnCommand`
- `TurnOffCommand`
- `SetTemperatureCommand`

Each command usually has:
- `execute()`
- `undo()` (if supported)

`CommandInvoker` is responsible for:
- running commands,
- storing command history,
- triggering undo.

Why useful:
- easy undo/redo,
- easy logging (`commands_log`),
- clean extension for new actions.

## 4) Iterator Pattern (`core.Room.devices()` Enumeration)

Iterator means controlled traversal of a collection.

In your plan, `Room.devices()` returns `Enumeration<Device>`.

Why this matters:
- You expose devices safely without exposing internal list implementation.
- It directly satisfies assignment requirement about Iterator/Enumeration.

## 5) Strategy Pattern (`strategy.AutomationMode`)

Strategy means selecting behavior at runtime.

Modes:
- `EcoMode`
- `SleepMode`
- `AwayMode`

All implement one shared interface, e.g. `AutomationMode`.

Why useful:
- switch logic without giant `if/else`.
- add new mode later without rewriting hub code (OCP).

## 6) DAO Pattern (`persistence.dao.*`)

DAO = Data Access Object.
All SQL belongs in DAO classes, not domain objects.

Examples:
- `UserDAO`
- `RoomDAO`
- `DeviceDAO`
- `DeviceEventDAO`
- `ScheduleDAO`
- `CommandsLogDAO`

Why useful:
- domain layer stays independent of database details,
- easier testing,
- easier DB change in future.

## 7) SQLite (Simple explanation)

SQLite is a lightweight file-based database.

Why chosen:
- no server setup,
- one file in project,
- fast for course demos,
- works perfectly with DAO pattern.

Core tables in your plan:
- `users`
- `rooms`
- `devices`
- `device_events`
- `schedules`
- `commands_log`

Important contracts from your main plan:
- `device_events` is populated from Observer notifications.
- `commands_log` fields: `(command_id, device_id, action, params_json, result, timestamp)`.

## 8) Required Alternative Designs (Report)

Your plan already fixed these two:

1. **Observer Push vs Pull**
   - Chosen: **Push**
   - Rejected: Pull
   - Reason: real-time events should not wait for extra query round-trip.

2. **DAO vs Active Record**
   - Chosen: **DAO**
   - Rejected: Active Record
   - Reason: assignment asks for modularity and future expansion; DAO keeps SQL separated.

For each one in report, compare:
- performance
- extensibility
- cost
- maintainability

## 9) How Layers Integrate (Full flow)

Architecture layers:
- **Presentation**: JavaFX
- **Application**: Facade + CommandInvoker
- **Domain**: Hub, devices, strategies, observer contracts
- **Persistence**: DAOs + SQLite

### Integration flow for a normal action

1. User clicks button in JavaFX.
2. JavaFX controller calls `HomeController` (Facade).
3. Facade creates/dispatches a command via `CommandInvoker`.
4. Command updates domain state (`SmartHomeHub`, `Device`).
5. Device notifies observers (`update(Device, String event)`).
6. Observer hook writes event to `DeviceEventDAO`.
7. Command execution is written to `CommandsLogDAO`.
8. GUI refreshes by reading updated state/history.

### Integration flow for mode change

1. User selects mode (`Eco/Sleep/Away`) in GUI.
2. Facade asks domain to apply selected strategy.
3. Hub behavior changes according to selected strategy implementation.

## 10) Team Work Split (clear parts to assign)

Use this section directly to assign each teammate one part.

### Part A — Foundation Domain + Core Patterns (Member 1)

**Scope**
- `core.*` (`SmartHomeHub`, `Room`, entities)
- `observer.*`
- `factory.*`
- `strategy.*`
- Iterator support in `Room.devices()` as `Enumeration`

**Must deliver**
- Singleton Hub works.
- Observer contracts and notifications work.
- Abstract Factory creates correct device types.
- Strategy modes (`Eco`, `Sleep`, `Away`) can switch at runtime.
- Iterator/Enumeration exposed from room/device collections.

**Depends on**
- Project skeleton only.

**Unblocks**
- JavaFX integration, command integration, DAO event hooks.

### Part B — Command Layer + Undo (Member 2)

**Scope**
- `command.*` + `CommandInvoker`
- Concrete command classes (turn on/off, set temp, lock/unlock, mode set)

**Must deliver**
- `execute()` on all core commands.
- `undo()` where required by your plan.
- Command history in invoker.
- Integration contract with DB logging (`commands_log` fields).

**Depends on**
- Part A domain model and device APIs.

**Unblocks**
- Facade behavior, command history UI, DB command logging.

### Part C — Database + DAO + SQLite (Member 3)

**Scope**
- `persistence.Database` singleton
- `persistence.dao.*`
- SQL schema file and DB initialization

**Must deliver**
- SQLite schema with tables: `users`, `rooms`, `devices`, `device_events`, `schedules`, `commands_log`.
- DAOs: `UserDAO`, `RoomDAO`, `DeviceDAO`, `DeviceEventDAO`, `ScheduleDAO`, `CommandsLogDAO`.
- Event logging hook from Observer to `device_events`.
- Command log insert using agreed contract.

**Depends on**
- Basic domain contracts from Part A.

**Unblocks**
- Event history tab, schedule tab, command history persistence.

### Part D — JavaFX GUI + Facade Integration (Member 4)

**Scope**
- `ui.*`, `resources/fxml/*`, `resources/css/*`
- `facade.HomeController`

**Must deliver**
- Dashboard and basic device controls.
- History and schedule views (reading from DAO/facade).
- Controllers call facade only (no direct SQL or domain wiring in UI).
- Facade API methods covering all user actions needed by UI.

**Depends on**
- Part A + Part B APIs, and Part C read/write methods.

**Unblocks**
- End-to-end demo and final integration.

### Part E — Testing + Report + Alternative Designs (Member 5)

**Scope**
- Unit/integration tests.
- UML/class diagram and report writing.
- Alternative designs section and trade-off analysis.

**Must deliver**
- Tests for critical flows:
  - GUI action -> facade -> command -> domain -> observer -> DAO.
  - Strategy mode change.
  - Iterator enumeration correctness.
- Report sections:
  - Pattern locations and methods.
  - SOLID mapping.
  - Alternative #1: Observer Push vs Pull.
  - Alternative #2: DAO vs Active Record.
  - Trade-off table with performance/extensibility/cost/maintainability.

**Depends on**
- All implementation parts.

**Unblocks**
- Final submission package.

## 11) Integration Contracts (everyone must agree early)

These are mandatory shared contracts between parts:

- **Observer payload**: `update(Device d, String event)`.
- **Device ID format**: UUID string from factory, reused in GUI + DB.
- **Command log fields**: `(command_id, device_id, action, params_json, result, timestamp)`.
- **Facade-only UI rule**: JavaFX controllers must call `HomeController`, not DAOs directly.
- **No business logic in UI**: keep validation/rules in facade/domain/command.

## 12) Quick memory version (exam/demo)

- **JavaFX**: UI only.
- **Facade**: one door from UI to system.
- **Command**: action object + undo/history.
- **Iterator**: `Enumeration` traversal.
- **Strategy**: swap behavior (modes).
- **DAO**: SQL isolation.
- **SQLite**: simple local DB file.
- **Alternatives**: Push vs Pull, DAO vs Active Record.
- **Integration**: GUI -> Facade -> Command -> Domain -> Observer -> DAO -> SQLite.

