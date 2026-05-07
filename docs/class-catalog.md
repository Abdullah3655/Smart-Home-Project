# Smart Home Automation — Class Catalog

## I. Project Description

A simplified **Smart Home Automation System** in Java. The user can
navigate rooms, control devices (lights, thermostats, locks, cameras),
apply whole-home automation modes, view event history, and undo actions
through an intuitive JavaFX interface.

**Users can:**

- view all rooms in the home and the devices each contains
- turn devices on or off; lock or unlock doors
- adjust thermostat temperatures
- apply automation modes (Eco / Sleep / Away)
- add new devices to a room (selecting type and family)
- wrap a device with a logging decorator and view captured calls
- view past device events (persisted across app restarts)
- view command history with descriptions
- undo the most recent action

State changes propagate via a push-style **Observer** chain — the live
UI, the history feed, and the persistent event log all listen to the
same events.

---

## II. Quick Index

| Layer | Classes |
|---|---|
| **Core** | `SmartHomeHub`, `Room`, `Database` |
| **Devices** | `Device`, `Light`, `Thermostat`, `Lock`, `Camera` |
| **Observer** | `Observer`, `Observable` |
| **Abstract Factory** | `DeviceFactory`, `Version1DeviceFactory`, `Version2DeviceFactory` |
| **Strategy** | `AutomationMode`, `EcoMode`, `SleepMode`, `AwayMode` |
| **Command** | `DeviceCommand`, six concrete commands, `CommandInvoker` |
| **Decorator** | `DeviceDecorator`, `LoggingDeviceDecorator`, `EnergyTrackedDecorator` |
| **Façade** | `HomeController` |
| **Persistence** | five DAOs, `DaoEventBridge` |

---

## III. Core Layer

### `SmartHomeHub`  *«singleton, strategy context, iterator aggregate»*

Represents the smart home itself. Owns the set of rooms and the active
automation mode. Implemented as a **thread-safe Singleton** so the whole
application sees one authoritative state.

**Pattern roles** — Singleton · Strategy Context · Iterator aggregate.
**Collaborators** — `Room`, `AutomationMode`, `RoomIterator`.

**Notable Attributes**
- `- INSTANCE : SmartHomeHub` *(static, lazy-initialised)*
- `- roomsById : Map<String, Room>`
- `- automationMode : AutomationMode`

**Possible Methods**
- `+ getInstance() : SmartHomeHub` *(static)* — Singleton accessor
- `+ addRoom(Room r) : void`
- `+ getRoom(String roomId) : Room`
- `+ getRooms() : Collection<Room>`
- `+ enumerateRooms() : Enumeration<Room>` — Iterator pattern method (rubric)
- `+ setAutomationMode(AutomationMode mode) : void` — install a Strategy
- `+ applyAutomationMode() : void` — Strategy Context delegate
- `+ createIterator() : RoomIterator` — Iterator pattern accessor (custom GoF interface)

---

### `Room`  *«iterator host»*

A logical container of devices (Kitchen, Living Room, Front Door).
Exposes its devices via the **Iterator pattern** as an
`Enumeration<Device>`.

**Pattern role** — Iterator host.
**Collaborators** — `Device`, `SmartHomeHub`.

**Notable Attributes**
- `- roomId : String`
- `- name : String`
- `- devicesById : Map<String, Device>`

**Possible Methods**
- `+ getRoomId() : String`
- `+ getName() : String`
- `+ addDevice(Device d) : void`
- `+ removeDevice(String deviceId) : void`
- `+ getDevice(String deviceId) : Device`
- `+ devices() : List<Device>` — modern accessor for callers
- `+ enumerateDevices() : Enumeration<Device>` — Iterator pattern method (rubric)

---

### `Database`  *«singleton»*

Owns the single SQLite JDBC connection. Implemented as a **Singleton**.
Loads `db/schema.sql` from the classpath and runs idempotent schema
migrations on first use.

**Pattern role** — Singleton.
**Collaborators** — every DAO; `java.sql.Connection`.

**Notable Attributes**
- `- INSTANCE : Database` *(static)*
- `- connection : Connection`

**Possible Methods**
- `+ getInstance() : Database` *(static)* — production accessor
- `+ forUrl(String url) : Database` *(static)* — test factory (in-memory)
- `+ getConnection() : Connection`

---

## IV. Devices

### `Device`  *«abstract, subject, receiver, component»*

Abstract superclass for any controllable thing in the home. Plays
three pattern roles simultaneously: **Subject** (Observer),
**Receiver** (Command), **Component** (Decorator). Every state-changing
method calls `notifyObservers(event)` to push the change.

**Pattern roles** — Observer Subject · Command Receiver · Decorator Component.
**Collaborators** — `Observer`, `DeviceCommand`, `DeviceDecorator`.

**Notable Attributes**
- `- id : String` *(UUID, immutable)*
- `- name : String`
- `- poweredOn : boolean`
- `- observers : List<Observer>`

**Possible Methods**
- `+ getId() : String`
- `+ getName() : String`
- `+ isPoweredOn() : boolean`
- `+ turnOn() : void` — fires `TURNED_ON`
- `+ turnOff() : void` — fires `TURNED_OFF`
- `+ attach(Observer o) : void`
- `+ detach(Observer o) : void`
- `# notifyObservers(String event) : void`

---

### `Light`  ·  `Thermostat`  ·  `Lock`  ·  `Camera`  *«concrete devices»*

Concrete subclasses of `Device`. Each adds type-specific state and
event identifiers:

- **`Light`** — `- brightness : int`; `+ setBrightness(int) : void` fires `BRIGHTNESS_CHANGED`
- **`Thermostat`** — `- temperature : double`; `+ setTemperature(double) : void` fires `TEMP_CHANGED`
- **`Lock`** — `- locked : boolean`; `+ lock() : void` / `+ unlock() : void` fire `LOCKED` / `UNLOCKED`
- **`Camera`** — uses inherited `turnOn` / `turnOff` to represent armed / disarmed

Each base type has two family variants (`Version1*`, `Version2*`)
produced by the corresponding `DeviceFactory`. Variants differ in
*policy*, not interface — e.g. `Version1Light.setBrightness(int)`
snaps to 25 % steps while `Version2Light` accepts any 0–100 value.

---

## V. Observer

### `Observer`  ·  `Observable`  *«interfaces»*

The two halves of the **Observer pattern**.

**`Observable`** is implemented by every Subject (primarily `Device`):

- `+ attach(Observer o) : void`
- `+ detach(Observer o) : void`
- `+ notifyObservers(String event) : void`

**`Observer`** is implemented by anything that wants to react:

- `+ update(Device d, String event) : void` — locked push-style signature

UI controllers, the history feed, and `DaoEventBridge` (persistence) all
implement `Observer`.

---

## VI. Abstract Factory

### `DeviceFactory`  *«abstract factory»*

Abstract superclass. The **Abstract Factory** half of the rubric line
*"Abstract Factory with Factory Methods"* — each `createXxx(String)` is
a **Factory Method** subclasses override.

**Pattern role** — Abstract Factory.
**Collaborators** — `Device` (and its subclasses).

**Possible Methods**
- `+ createLight(String name) : Device` *(abstract)*
- `+ createThermostat(String name) : Device` *(abstract)*
- `+ createDoorLock(String name) : Device` *(abstract)*
- `+ createCamera(String name) : Device` *(abstract)*
- `# newId() : String` — UUID generator for new devices

### `Version1DeviceFactory`  ·  `Version2DeviceFactory`  *«concrete factories»*

Concrete factories. Each implements all four methods, returning the
variant of its family. Both are **Liskov-substitutable** — no
`UnsupportedOperationException` stubs anywhere in the hierarchy.

---

## VII. Strategy

### `AutomationMode`  *«strategy»*

The **Strategy** interface for whole-home automation behaviour. Each
concrete strategy iterates rooms via the Iterator and mutates devices
according to its policy.

**Pattern role** — Strategy.
**Collaborators** — `SmartHomeHub` (Context); every `Device` subclass.

**Possible Methods**
- `+ name() : String` — UI-displayable mode name
- `+ apply(SmartHomeHub hub) : void` — runs the strategy's algorithm

### `EcoMode`  ·  `SleepMode`  ·  `AwayMode`  *«concrete strategies»*

- **`EcoMode`** — every thermostat to 24 °C, powered-on lights dimmed to 50 %
- **`SleepMode`** — lights off, doors locked, thermostats to 20 °C
- **`AwayMode`** — lights off, doors locked, cameras armed, thermostats to 15 °C

---

## VIII. Command

### `DeviceCommand`  *«command interface»*

The **Command** interface — every user action becomes an object that
holds a Receiver and captures pre-state in fields so `undo()` can
restore precisely.

**Pattern role** — Command.
**Collaborators** — `Device` (Receiver); `CommandInvoker`.

**Possible Methods**
- `+ execute() : void`
- `+ undo() : void` — reverses the action's effect
- `+ describe() : String` — label for command-history UI

### Six concrete commands

`TurnOnCommand` · `TurnOffCommand` · `SetTemperatureCommand` ·
`LockCommand` · `UnlockCommand` · `SetAutomationModeCommand` —
one per user action. Each captures its Receiver's pre-state in a field
for reliable undo.

### `CommandInvoker`  *«invoker»*

The **Invoker**. Runs commands and keeps the undo stack. Optionally
writes a row to `commands_log` via `CommandsLogDAO` on every successful
execute. Imports zero domain classes — only `DeviceCommand`.

**Pattern role** — Command Invoker.
**Collaborators** — `DeviceCommand` (only).

**Notable Attributes**
- `- history : Deque<DeviceCommand>` — LIFO undo stack
- `- auditLog : CommandsLogDAO` — optional, writes one row per execute

**Possible Methods**
- `+ execute(DeviceCommand c) : void`
- `+ canUndo() : boolean`
- `+ undo() : DeviceCommand`
- `+ getHistory() : List<DeviceCommand>`
- `+ clearHistory() : void`

---

## IX. Decorator

### `DeviceDecorator`  *«decorator»*

Abstract base for transparent **Decorator** wrappers. Holds a `wrappee`
and forwards every `Device` method to it; subclasses override the
methods they want to extend.

**Pattern role** — Decorator base.
**Collaborators** — `Device` (Component being wrapped).

**Notable Attributes**
- `- wrappee : Device`

**Possible Methods**
- All of `Device`'s methods, each delegating to `wrappee`.

### `LoggingDeviceDecorator`  *«concrete decorator»*

Captures every state-changing call as a string entry. Adds
`+ getLog() : List<String>`.

### `EnergyTrackedDecorator`  *«concrete decorator»*

Tracks total time the wrapped device has been powered on. Adds
`+ getTotalOnMillis() : long`.

---

## X. Façade

### `HomeController`  *«façade»*

The single class the JavaFX UI talks to. Wraps every mutation in a
`DeviceCommand` and hands it to `CommandInvoker`; routes reads through
DAOs. Pure orchestration — no domain logic of its own.

**Pattern role** — Façade.
**Collaborators** — `SmartHomeHub`, `CommandInvoker`, `DeviceEventDAO`,
`CommandsLogDAO`.

**Notable Attributes**
- `- hub : SmartHomeHub`
- `- invoker : CommandInvoker`
- `- eventDAO : DeviceEventDAO`
- `- commandsLogDAO : CommandsLogDAO`

**Possible Methods**
- `+ turnOnDevice(String deviceId) : void`
- `+ turnOffDevice(String deviceId) : void`
- `+ lockDevice(String deviceId) : void` — rejects non-`Lock` devices
- `+ unlockDevice(String deviceId) : void`
- `+ setTemperature(String deviceId, double value) : void` — rejects non-`Thermostat`
- `+ setAutomationMode(String modeName) : void`
- `+ getDevicesForRoom(String roomId) : List<Device>`
- `+ getEventHistory() : List<DeviceEvent>`
- `+ getCommandHistory() : List<CommandLog>`
- `+ undoLastAction() : boolean`

---

## XI. Persistence

### `UserDAO`  ·  `RoomDAO`  ·  `DeviceDAO`  ·  `DeviceEventDAO`  ·  `CommandsLogDAO`  *«DAOs»*

Five **DAO** classes, one per table, isolating SQL behind plain Java
methods. Every DAO has a dual constructor (production: singleton
`Database`; tests: injected `Connection`) and uses `PreparedStatement`
exclusively to prevent SQL injection.

`DeviceDAO` is notable: it round-trips polymorphic device subtypes by
re-using the **Abstract Factory** at deserialisation to reconstruct the
correct family variant.

**Pattern role** — DAO.
**Collaborators** — `Database`; their respective domain entities.

### `DaoEventBridge`  *«observer / boundary adapter»*

An `Observer` that lives at the UI–persistence boundary. Attached to
every device on app startup; each `update(d, event)` call writes a row
to `device_events` and updates `devices.state_blob`. Keeps the domain
layer free of any persistence dependency.

**Pattern role** — Observer / boundary adapter.
**Collaborators** — `Device` (subject), `DeviceEventDAO`, `DeviceDAO`.

---

## XII. Pattern Roles per Class

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
| `HomeController` | **Façade** |
| Five DAO classes | **DAO** |
| `DaoEventBridge` | **Observer** (boundary adapter) |

All **9** design patterns are implemented; the **4 mandatory** patterns
(Iterator, Abstract Factory + Factory Methods, Singleton, Observer)
have their required methods identified above.
