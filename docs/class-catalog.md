# Smart Home Automation — Project Description and Class Catalog

> Formatted to mirror the Airport System reference example in the
> CSE3202 / SE 491 brief (`Notable Attributes` / `Possible Methods`
> per class). This document is intended to be embedded in the
> 5-page report.

---

## Project Description

In this project, we implement a simplified version of a **Smart Home
Automation System** in Java. The system allows users to interact with
their home, navigate rooms, control devices (lights, thermostats, locks,
cameras), apply whole-home automation modes, view event history, and
undo actions — while also discovering the world of design patterns!

### Users can:

- Get a list of all rooms in the home
- Enter a room and view its devices
- Turn devices on or off
- Lock or unlock doors
- Adjust thermostat temperatures (±1 °C, or set a specific value)
- Apply automation modes (Eco, Sleep, Away) to the whole home
- Add new devices to a room (selecting type and family)
- Wrap a device with a logging decorator and view the captured calls
- View past device events (persisted across app restarts)
- View past command history with descriptions
- Undo the most recent action

Devices automatically push state-change events to all interested
observers — the live UI updates and the persistent event log are both
fed by the same Observer chain.

---

## Here's a brief description of each class

### `SmartHomeHub`

Represents the smart-home itself. The hub is responsible for owning the
set of rooms, holding the currently active automation mode, and
coordinating cross-cutting access to the domain model. Implemented as a
**thread-safe Singleton** so the whole application sees one
authoritative state.

**Notable Attributes**

- `INSTANCE` — the lone static instance of the hub
- `roomsById` — map of every room in the home, keyed by room id
- `automationMode` — the currently active `AutomationMode` strategy

**Possible Methods**

- `static SmartHomeHub getInstance()` — Singleton accessor
- `void addRoom(Room r)` — register a room with the hub
- `Room getRoom(String roomId)` — find a room by id
- `Collection<Room> getRooms()` — return an unmodifiable view of all rooms
- `void setAutomationMode(AutomationMode mode)` — install a Strategy
- `AutomationMode getAutomationMode()` — read the active strategy
- `void applyAutomationMode()` — Context delegate that runs the active strategy
- `RoomIterator createIterator()` — Iterator pattern accessor for rooms

---

### `Room`

Represents a logical room inside the home (Kitchen, Living Room, Front
Door). Each room contains a collection of devices and exposes them via
the **Iterator pattern** as an `Enumeration<Device>` — the form
required by the assignment brief.

**Notable Attributes**

- `roomId` — unique room id
- `name` — human-readable name
- `devicesById` — map of devices in this room, keyed by device id

**Possible Methods**

- `String getRoomId()`
- `String getName()`
- `void addDevice(Device d)` — register a device with this room
- `void removeDevice(String deviceId)`
- `Device getDevice(String deviceId)`
- `Enumeration<Device> devices()` — Iterator pattern method (returns rooms' devices)

---

### `Device`

Abstract superclass representing any controllable thing in the home.
Each device has an immutable id, a human name, a power state, and a list
of attached observers. Devices implement `Observable` — every
state-changing method calls `notifyObservers(event)` to push the change
to every listener.

**Notable Attributes**

- `id` — UUID, immutable, set at construction
- `name` — display name
- `poweredOn` — current power state
- `observers` — list of `Observer` instances attached to this device

**Possible Methods**

- `String getId()`
- `String getName()`
- `boolean isPoweredOn()`
- `void turnOn()` — fires `TURNED_ON` event
- `void turnOff()` — fires `TURNED_OFF` event
- `void attach(Observer o)` — register a listener (Observable contract)
- `void detach(Observer o)` — unregister a listener
- `void notifyObservers(String event)` — push an event to every listener

---

### `Light` / `Thermostat` / `Lock` / `Camera`

Concrete subclasses of `Device`. Each adds type-specific state and
behaviour:

- **`Light`** — `brightness : int`, `setBrightness(int)`. Fires `BRIGHTNESS_CHANGED`.
- **`Thermostat`** — `temperature : double`, `setTemperature(double)`. Fires `TEMP_CHANGED`.
- **`Lock`** — `locked : boolean`, `lock()`, `unlock()`. Fires `LOCKED` / `UNLOCKED`.
- **`Camera`** — uses only the inherited on/off behaviour to represent armed/disarmed.

Each base class has two further family variants: `Version1Light`,
`Version2Light`, `Version1Thermostat`, etc. — products of the
**Abstract Factory** family (`Version1DeviceFactory` /
`Version2DeviceFactory`). Variants differ in policy (e.g.
`Version1Light` snaps brightness to 25 % steps, while `Version2Light`
accepts any 0–100 value).

---

### `Observer` / `Observable` (interfaces)

The two halves of the **Observer pattern**.

`Observable` is implemented by anything that may mutate (primarily
`Device`). It exposes:

- `void attach(Observer o)`
- `void detach(Observer o)`
- `void notifyObservers(String event)`

`Observer` is implemented by anything that wants to react to a device
change. It exposes a single method:

- `void update(Device d, String event)` — locked push-style signature

The signature is intentionally fixed: every observer receives the
affected device and a short uppercase event name (e.g. `"TURNED_ON"`,
`"LOCKED"`). UI controllers, the history feed, and the persistence
bridge all implement this same interface.

---

### `DeviceFactory`

Abstract superclass declaring the family of device-creation methods.
This is the **Abstract Factory** half of the rubric line *"Abstract
Factory with Factory Methods"* — each `createXxx(String name)` is a
**Factory Method** that subclasses override to instantiate the variant
belonging to their family.

**Notable Attributes**

- (none — factories are stateless)

**Possible Methods**

- `abstract Device createLight(String name)` — Factory Method
- `abstract Device createThermostat(String name)` — Factory Method
- `abstract Device createDoorLock(String name)` — Factory Method
- `abstract Device createCamera(String name)` — Factory Method
- `protected String newId()` — generates a UUID for the next device

### `Version1DeviceFactory` / `Version2DeviceFactory`

Concrete factories. Each implements **all four** factory methods,
returning the variant of its family. Both factories are
Liskov-substitutable for `DeviceFactory` — no
`UnsupportedOperationException` stubs.

---

### `AutomationMode` (interface)

The **Strategy** interface for whole-home automation behaviour. Each
concrete strategy walks all rooms via the Iterator and mutates devices
according to its policy, firing observer events along the way.

**Possible Methods**

- `String name()` — UI-displayable mode name
- `void apply(SmartHomeHub hub)` — runs the strategy's algorithm

### `EcoMode` / `SleepMode` / `AwayMode`

Concrete strategies. Each defines a different home behaviour:

- **`EcoMode`** — sets every thermostat to 24 °C and dims every powered-on light to 50 %.
- **`SleepMode`** — turns off every light, locks every door, sets thermostats to 20 °C.
- **`AwayMode`** — turns off every light, locks every door, arms every camera, sets thermostats to 15 °C.

---

### `DeviceCommand` (interface)

The **Command** interface — every user action becomes an object of
this type. Commands hold a reference to a Receiver (a `Device`,
`Lock`, `Thermostat`, or `SmartHomeHub`) and capture pre-execute state
in fields so `undo()` can restore it precisely.

**Possible Methods**

- `void execute()` — perform the action
- `void undo()` — reverse the action's effect, restoring pre-execute state
- `String describe()` — human-readable label for command-history UI

### `TurnOnCommand` / `TurnOffCommand` / `SetTemperatureCommand` / `LockCommand` / `UnlockCommand` / `SetAutomationModeCommand`

Six concrete commands, one per user action. Each captures pre-state in
a field so undo is reliable across multiple intermediate actions.

### `CommandInvoker`

The **Invoker**. Runs commands and maintains the undo stack. Optionally
writes a row to `commands_log` (via `CommandsLogDAO`) on every successful
execute.

**Notable Attributes**

- `history : Deque<DeviceCommand>` — LIFO undo stack
- `auditLog : CommandsLogDAO` — optional, writes one row per execute

**Possible Methods**

- `void execute(DeviceCommand c)` — runs `c.execute()` and pushes onto history
- `boolean canUndo()`
- `DeviceCommand undo()` — pops history, calls `last.undo()`
- `List<DeviceCommand> getHistory()` — read-only view of the stack
- `void clearHistory()`

---

### `DeviceDecorator`

Abstract base for transparent **Decorator** wrappers around a `Device`.
The decorator's wrappee field carries the wrapped instance; every
`Device` method delegates to it. Subclasses override individual methods
to add cross-cutting behaviour (logging, energy tracking) without
modifying any device class.

**Notable Attributes**

- `wrappee : Device` — the wrapped device

**Possible Methods**

- All of `Device`'s methods, each forwarded to `wrappee` by default.

### `LoggingDeviceDecorator`

Captures every state-changing call. Adds:

- `List<String> getLog()` — read-only view of captured actions

### `EnergyTrackedDecorator`

Tracks total time the wrapped device has spent powered on.

- `long getTotalOnMillis()` — total ms powered on, including the current session

---

### `HomeController` (the **Facade**)

The single class the JavaFX UI talks to. Wraps every mutation in a
`DeviceCommand` and hands it to the `CommandInvoker`. Reads route
through DAOs. Domain logic stays out of this class — it is purely
orchestration.

**Notable Attributes**

- `hub : SmartHomeHub`
- `invoker : CommandInvoker`
- `eventDAO : DeviceEventDAO`
- `commandsLogDAO : CommandsLogDAO`

**Possible Methods**

- `void turnOnDevice(String deviceId)` — routes through `TurnOnCommand`
- `void turnOffDevice(String deviceId)`
- `void lockDevice(String deviceId)` — rejects non-Lock devices with `IllegalArgumentException`
- `void unlockDevice(String deviceId)`
- `void setTemperature(String deviceId, double value)` — rejects non-Thermostats
- `void setAutomationMode(String modeName)` — translates a UI string to a Strategy
- `List<Device> getDevicesForRoom(String roomId)` — read-only
- `List<DeviceEvent> getEventHistory()` — pulls from `DeviceEventDAO`
- `List<CommandLog> getCommandHistory()` — pulls from `CommandsLogDAO`
- `boolean undoLastAction()` — exposes Command undo to the UI

---

### `Database`

Owns the single SQLite JDBC connection. Implemented as a
**Singleton**. Loads `db/schema.sql` from the classpath on first use
and runs idempotent schema migrations.

**Notable Attributes**

- `INSTANCE` — the lone static instance
- `connection` — the JDBC connection

**Possible Methods**

- `static Database getInstance()` — production accessor
- `static Database forUrl(String url)` — test factory (in-memory SQLite)
- `Connection getConnection()`

---

### `UserDAO` / `RoomDAO` / `DeviceDAO` / `DeviceEventDAO` / `CommandsLogDAO`

Five **DAO** classes, one per table, isolating SQL behind plain Java
methods. Every DAO follows the same convention:

- A no-arg production constructor that uses `Database.getInstance().getConnection()`
- A constructor accepting a `Connection` for tests with in-memory SQLite
- All queries use `PreparedStatement` to prevent SQL injection

`DeviceDAO` is the most interesting: it round-trips polymorphic device
subtypes through one row, using the **Abstract Factory** at runtime to
reconstruct the right concrete `Device` variant.

---

## Design Pattern Roles per Class — Summary

| Class | Pattern role(s) |
|---|---|
| `SmartHomeHub` | **Singleton**, **Strategy Context**, **Iterator** aggregate |
| `Database` | **Singleton** |
| `Room` | **Iterator** host (returns `Enumeration<Device>`) |
| `RoomIterator` / `HubRoomIterator` | **Iterator** (custom GoF interface) |
| `Observer` / `Observable` | **Observer** roles |
| `Device` | **Subject** (Observer), **Receiver** (Command), **Component** (Decorator) |
| `DeviceFactory` and 2 concrete factories | **Abstract Factory + Factory Methods** |
| `AutomationMode` and 3 concrete modes | **Strategy** |
| `DeviceCommand` and 6 concrete commands | **Command** |
| `CommandInvoker` | **Invoker** (Command) |
| `DeviceDecorator` and 2 concrete decorators | **Decorator** |
| `HomeController` (in `facade` package) | **Facade** |
| `UserDAO` / `RoomDAO` / `DeviceDAO` / `DeviceEventDAO` / `CommandsLogDAO` | **DAO** |
| `DaoEventBridge` | **Observer** (boundary adapter) |

All 9 design patterns are implemented; the 4 mandatory ones (Iterator,
Abstract Factory + Factory Methods, Singleton, Observer) are fully
covered with their **required methods identified above**.
