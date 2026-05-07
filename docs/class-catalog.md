# Smart Home Automation — Class Catalog

> Format mirrors the Airport System reference example in the
> CSE3202 / SE 491 brief: `Notable Attributes` and `Possible Methods`
> per class. Embed in §1–§3 of the report.

---

## Project Description

A simplified **Smart Home Automation System** in Java. The user can
navigate rooms, control devices (lights, thermostats, locks, cameras),
apply whole-home automation modes, view event history, and undo actions
through an intuitive JavaFX interface.

### Users can

- View all rooms in the home and the devices each contains
- Turn devices on or off; lock or unlock doors
- Adjust thermostat temperatures
- Apply automation modes (Eco, Sleep, Away)
- Add new devices to a room (selecting type and family)
- Wrap a device with a logging decorator and view captured calls
- View past device events (persisted across app restarts)
- View command history with descriptions
- Undo the most recent action

State changes propagate via a push-style Observer chain — the live UI,
the history feed, and the persistent event log all listen to the same
events.

---

## Class Descriptions

### `SmartHomeHub`

Represents the smart home itself. Owns the set of rooms and the active
automation mode. Implemented as a **thread-safe Singleton** so the whole
application sees one authoritative state.

**Notable Attributes**
- `INSTANCE` — the lone static instance
- `roomsById` — map of every room, keyed by id
- `automationMode` — the currently active strategy

**Possible Methods**
- `static SmartHomeHub getInstance()` — Singleton accessor
- `void addRoom(Room r)`
- `Room getRoom(String roomId)`
- `Collection<Room> getRooms()`
- `Enumeration<Room> enumerateRooms()` — Iterator pattern method (rubric requirement)
- `void setAutomationMode(AutomationMode mode)` — install a Strategy
- `void applyAutomationMode()` — Context delegate
- `RoomIterator createIterator()` — Iterator pattern accessor (custom GoF interface)

---

### `Room`

A logical container of devices (Kitchen, Living Room, Front Door).
Exposes its devices via the **Iterator pattern** as an
`Enumeration<Device>`.

**Notable Attributes**
- `roomId` — unique room id
- `name` — display name
- `devicesById` — map of devices, keyed by id

**Possible Methods**
- `String getRoomId()`
- `String getName()`
- `void addDevice(Device d)`
- `void removeDevice(String deviceId)`
- `Device getDevice(String deviceId)`
- `List<Device> devices()` — modern accessor for callers
- `Enumeration<Device> enumerateDevices()` — Iterator pattern method (rubric requirement)

---

### `Device`

Abstract superclass for any controllable thing in the home. Plays
three pattern roles: **Subject** (Observer), **Receiver** (Command),
**Component** (Decorator). Every state-changing method calls
`notifyObservers(event)` to push the change.

**Notable Attributes**
- `id` — UUID, immutable
- `name` — display name
- `poweredOn` — current power state
- `observers` — list of attached `Observer`s

**Possible Methods**
- `String getId()`, `String getName()`, `boolean isPoweredOn()`
- `void turnOn()` — fires `TURNED_ON`
- `void turnOff()` — fires `TURNED_OFF`
- `void attach(Observer o)`, `void detach(Observer o)`
- `void notifyObservers(String event)`

---

### `Light` / `Thermostat` / `Lock` / `Camera`

Concrete subclasses of `Device`. Each adds type-specific state:

- **`Light`** — `brightness : int`; `setBrightness(int)` fires `BRIGHTNESS_CHANGED`
- **`Thermostat`** — `temperature : double`; `setTemperature(double)` fires `TEMP_CHANGED`
- **`Lock`** — `locked : boolean`; `lock()` / `unlock()` fire `LOCKED` / `UNLOCKED`
- **`Camera`** — uses inherited `turnOn` / `turnOff` to represent armed / disarmed

Each base class has two family variants (`Version1*`, `Version2*`)
produced by the corresponding `DeviceFactory`. Variants differ in
policy — e.g. `Version1Light.setBrightness(int)` snaps to 25 % steps
while `Version2Light` accepts any 0–100 value.

---

### `Observer` / `Observable` (interfaces)

The two halves of the **Observer pattern**.

`Observable` is implemented by every Subject (primarily `Device`):

- `void attach(Observer o)`
- `void detach(Observer o)`
- `void notifyObservers(String event)`

`Observer` is implemented by anything that wants to react:

- `void update(Device d, String event)` — locked push-style signature

UI controllers, the history feed, and `DaoEventBridge` (persistence) all
implement `Observer`.

---

### `DeviceFactory`

Abstract superclass. The **Abstract Factory** half of the rubric line
*"Abstract Factory with Factory Methods"* — each `createXxx(String)` is
a **Factory Method** subclasses override.

**Possible Methods**
- `abstract Device createLight(String name)`
- `abstract Device createThermostat(String name)`
- `abstract Device createDoorLock(String name)`
- `abstract Device createCamera(String name)`
- `protected String newId()` — UUID generator for new devices

### `Version1DeviceFactory` / `Version2DeviceFactory`

Concrete factories. Each implements all four methods, returning the
variant of its family — both are Liskov-substitutable, no
`UnsupportedOperationException` stubs.

---

### `AutomationMode` (interface)

The **Strategy** interface for whole-home automation behaviour. Each
concrete strategy iterates rooms via the Iterator and mutates devices
according to its policy.

**Possible Methods**
- `String name()` — UI-displayable mode name
- `void apply(SmartHomeHub hub)` — runs the strategy's algorithm

### `EcoMode` / `SleepMode` / `AwayMode`

Concrete strategies:

- **`EcoMode`** — every thermostat to 24 °C, powered-on lights dimmed to 50 %
- **`SleepMode`** — lights off, doors locked, thermostats to 20 °C
- **`AwayMode`** — lights off, doors locked, cameras armed, thermostats to 15 °C

---

### `DeviceCommand` (interface)

The **Command** interface — every user action becomes an object that
holds a Receiver and captures pre-state in fields so `undo()` can
restore precisely.

**Possible Methods**
- `void execute()`
- `void undo()` — reverses the action's effect
- `String describe()` — label for command-history UI

### `TurnOnCommand` / `TurnOffCommand` / `SetTemperatureCommand` / `LockCommand` / `UnlockCommand` / `SetAutomationModeCommand`

Six concrete commands, one per user action. Each captures its Receiver's
pre-state in a field for reliable undo.

### `CommandInvoker`

The **Invoker**. Runs commands and keeps the undo stack. Optionally
writes a row to `commands_log` via `CommandsLogDAO` on every successful
execute. Imports zero domain classes — only `DeviceCommand`.

**Notable Attributes**
- `history : Deque<DeviceCommand>` — LIFO undo stack
- `auditLog : CommandsLogDAO` — optional, writes one row per execute

**Possible Methods**
- `void execute(DeviceCommand c)`
- `boolean canUndo()`
- `DeviceCommand undo()`
- `List<DeviceCommand> getHistory()`
- `void clearHistory()`

---

### `DeviceDecorator`

Abstract base for transparent **Decorator** wrappers. Holds a `wrappee`
and forwards every `Device` method to it; subclasses override the
methods they want to extend.

**Notable Attributes**
- `wrappee : Device`

**Possible Methods**
- All of `Device`'s methods, each delegating to `wrappee`.

### `LoggingDeviceDecorator`

Captures every state-changing call. Adds `List<String> getLog()`.

### `EnergyTrackedDecorator`

Tracks total time the wrapped device has been on. Adds
`long getTotalOnMillis()`.

---

### `HomeController` (the **Facade**)

The single class the JavaFX UI talks to. Wraps every mutation in a
`DeviceCommand` and hands it to `CommandInvoker`; routes reads through
DAOs. Pure orchestration — no domain logic.

**Notable Attributes**
- `hub : SmartHomeHub`
- `invoker : CommandInvoker`
- `eventDAO : DeviceEventDAO`
- `commandsLogDAO : CommandsLogDAO`

**Possible Methods**
- `void turnOnDevice(String deviceId)`
- `void turnOffDevice(String deviceId)`
- `void lockDevice(String deviceId)` — rejects non-Lock devices
- `void unlockDevice(String deviceId)`
- `void setTemperature(String deviceId, double value)` — rejects non-Thermostats
- `void setAutomationMode(String modeName)`
- `List<Device> getDevicesForRoom(String roomId)`
- `List<DeviceEvent> getEventHistory()`
- `List<CommandLog> getCommandHistory()`
- `boolean undoLastAction()`

---

### `Database`

Owns the single SQLite JDBC connection. Implemented as a **Singleton**.
Loads `db/schema.sql` from the classpath and runs idempotent schema
migrations on first use.

**Notable Attributes**
- `INSTANCE`, `connection`

**Possible Methods**
- `static Database getInstance()` — production accessor
- `static Database forUrl(String url)` — test factory (in-memory)
- `Connection getConnection()`

---

### `UserDAO` / `RoomDAO` / `DeviceDAO` / `DeviceEventDAO` / `CommandsLogDAO`

Five **DAO** classes, one per table, isolating SQL behind plain Java
methods. Every DAO has a dual constructor (production: singleton
`Database`; tests: injected `Connection`) and uses `PreparedStatement`
exclusively to prevent SQL injection.

`DeviceDAO` is notable: it round-trips polymorphic device subtypes by
re-using the Abstract Factory at deserialization to reconstruct the
correct family variant.

---

### `DaoEventBridge`

An `Observer` that lives at the UI–persistence boundary. Attached to
every device on app startup; each `update(d, event)` call writes a row
to `device_events` and updates `devices.state_blob`. Keeps the domain
layer free of any persistence dependency.

---

## Design Pattern Roles per Class

| Class | Pattern role(s) |
|---|---|
| `SmartHomeHub` | **Singleton**, **Strategy Context**, **Iterator** aggregate |
| `Database` | **Singleton** |
| `Room` | **Iterator** host (returns `Enumeration<Device>`) |
| `RoomIterator` / `HubRoomIterator` | **Iterator** (custom GoF interface) |
| `Observer` / `Observable` | **Observer** roles |
| `Device` | **Subject** (Observer), **Receiver** (Command), **Component** (Decorator) |
| `DeviceFactory` + 2 concrete factories | **Abstract Factory + Factory Methods** |
| `AutomationMode` + 3 concrete modes | **Strategy** |
| `DeviceCommand` + 6 concrete commands | **Command** |
| `CommandInvoker` | **Invoker** (Command) |
| `DeviceDecorator` + 2 concrete decorators | **Decorator** |
| `HomeController` | **Facade** |
| 5 DAO classes | **DAO** |
| `DaoEventBridge` | **Observer** (boundary adapter) |

All 9 design patterns implemented; the 4 mandatory patterns
(Iterator, Abstract Factory + Factory Methods, Singleton, Observer)
have their required methods identified above.
