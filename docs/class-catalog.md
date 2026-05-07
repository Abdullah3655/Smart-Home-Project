# Smart Home — Class Catalog

A per-class description following the format from the assignment brief's
Airport System reference example. Each class lists *Notable Attributes*
and *Possible Methods*, plus the design pattern role(s) it plays.

> **Reading aid:** classes are grouped by package (which is also a
> stable design boundary). Within each package, abstract/interface
> classes appear before their concrete subclasses.

---

## Table of contents

1. [`com.smarthome.core`](#1-comsmarthomecore)
2. [`com.smarthome.observer`](#2-comsmarthomeobserver)
3. [`com.smarthome.devices`](#3-comsmarthomedevices)
4. [`com.smarthome.devices.decorator`](#4-comsmarthomedevicesdecorator)
5. [`com.smarthome.factory`](#5-comsmarthomefactory)
6. [`com.smarthome.strategy`](#6-comsmarthomestrategy)
7. [`com.smarthome.command`](#7-comsmarthomecommand)
8. [`com.smarthome.facade`](#8-comsmarthomefacade)
9. [`com.smarthome.persistence`](#9-comsmarthomepersistence)
10. [`com.smarthome.ui`](#10-comsmarthomeui)

---

## 1. `com.smarthome.core`

### `SmartHomeHub`
*The application's single domain hub.*
**Pattern roles:** Singleton · Strategy Context · Iterator aggregate.

**Notable attributes**
- `INSTANCE : SmartHomeHub` — the lone static instance
- `roomsById : Map<String, Room>` — every room in the home, keyed by id
- `automationMode : AutomationMode` — the currently active strategy

**Possible methods**
- `getInstance() : SmartHomeHub` — Singleton accessor
- `addRoom(Room) : void` — register a room
- `getRoom(String) : Room` — look up a room by id
- `getRooms() : Collection<Room>` — read-only view of all rooms
- `setAutomationMode(AutomationMode) : void` — change the active strategy
- `getAutomationMode() : AutomationMode` — current strategy
- `applyAutomationMode() : void` — Context-side delegate to `mode.apply(this)`
- `createIterator() : RoomIterator` — Iterator pattern accessor

### `Room`
*A logical container of devices.*
**Pattern roles:** Iterator host (returns `Enumeration<Device>`).

**Notable attributes**
- `roomId : String` — unique room id
- `name : String` — human-readable name
- `devicesById : Map<String, Device>` — devices in this room

**Possible methods**
- `getRoomId() : String`
- `getName() : String`
- `addDevice(Device) : void`
- `removeDevice(String deviceId) : void`
- `getDevice(String deviceId) : Device`
- `devices() : Enumeration<Device>` — Iterator pattern method (rubric requirement)

### `RoomIterator`
*Custom Gang-of-Four-style iterator interface.*
**Pattern roles:** Iterator interface.

**Possible methods**
- `hasMore() : boolean`
- `getNext() : Room`

### `HubRoomIterator`
*Concrete iterator over the hub's rooms.*

**Notable attributes**
- `rooms : List<Room>`
- `position : int`

**Possible methods**
- `hasMore() : boolean`
- `getNext() : Room`

### `RoomIterableCollection`
*Marker interface implemented by anything that produces a `RoomIterator`.*

**Possible methods**
- `createIterator() : RoomIterator`

---

## 2. `com.smarthome.observer`

### `Observer`
*Reactive listener interface.*
**Pattern roles:** Observer interface.

**Possible methods**
- `update(Device d, String event) : void` — locked push-style signature

### `Observable`
*Subject-side interface.*
**Pattern roles:** Subject interface.

**Possible methods**
- `attach(Observer) : void`
- `detach(Observer) : void`
- `notifyObservers(String event) : void`

---

## 3. `com.smarthome.devices`

### `Device`
*Abstract base for every controllable thing in the home.*
**Pattern roles:** Subject (Observer pattern) · Receiver (Command pattern) · Component (Decorator pattern).

**Notable attributes**
- `id : String` — UUID, immutable
- `name : String` — display name
- `poweredOn : boolean` — current power state
- `observers : List<Observer>` — attached listeners

**Possible methods**
- `getId() : String`
- `getName() : String`
- `setName(String) : void`
- `isPoweredOn() : boolean`
- `turnOn() : void` — fires `TURNED_ON` event
- `turnOff() : void` — fires `TURNED_OFF` event
- `attach(Observer) : void`
- `detach(Observer) : void`
- `notifyObservers(String event) : void`

### `Light extends Device`
**Notable attributes**
- `brightness : int` — 0..100

**Possible methods**
- `getBrightness() : int`
- `setBrightness(int) : void` — fires `BRIGHTNESS_CHANGED`

### `Thermostat extends Device`
**Notable attributes**
- `temperature : double` — target °C

**Possible methods**
- `getTemperature() : double`
- `setTemperature(double) : void` — fires `TEMP_CHANGED`

### `Lock extends Device`
**Notable attributes**
- `locked : boolean`

**Possible methods**
- `isLocked() : boolean`
- `lock() : void` — fires `LOCKED`
- `unlock() : void` — fires `UNLOCKED`

### `Camera extends Device`
*Inherits all behaviour from `Device`; no extra fields.*

### `Version1Light / Version1Thermostat / Version1Lock / Version1Camera`
*Legacy family — concrete products of `Version1DeviceFactory`. Each may
override its base behaviour (e.g. `Version1Light` snaps brightness to
discrete 25% steps).*

### `Version2Light / Version2Thermostat / Version2Lock / Version2Camera`
*Modern family — concrete products of `Version2DeviceFactory`. Smooth
behaviour (e.g. `Version2Light` accepts any 0..100 brightness).*

---

## 4. `com.smarthome.devices.decorator`

### `DeviceDecorator extends Device`
*Abstract base for transparent device wrappers.*
**Pattern roles:** Base Decorator (Decorator pattern).

**Notable attributes**
- `wrappee : Device` — the wrapped instance

**Possible methods**
- All `Device` methods, each delegating to `wrappee`.

### `LoggingDeviceDecorator extends DeviceDecorator`
*Captures every state-changing call.*

**Notable attributes**
- `log : List<String>` — captured action log

**Possible methods**
- `getLog() : List<String>`
- `turnOn() / turnOff()` — overrides record the call before delegating

### `EnergyTrackedDecorator extends DeviceDecorator`
*Tracks total time-on for energy reports.*

**Notable attributes**
- `onSinceMillis : long`
- `totalOnMillis : long`

**Possible methods**
- `getTotalOnMillis() : long`

---

## 5. `com.smarthome.factory`

### `DeviceFactory`
*Abstract Factory declaring the family of device-creation methods.*
**Pattern roles:** Abstract Factory + Factory Methods.

**Possible methods**
- `createLight(String name) : Device` — Factory Method
- `createThermostat(String name) : Device` — Factory Method
- `createDoorLock(String name) : Device` — Factory Method
- `createCamera(String name) : Device` — Factory Method
- `newId() : String` — protected helper, generates a UUID

### `Version1DeviceFactory extends DeviceFactory`
*Produces the `Version1*` family — every method returns the legacy variant.*

### `Version2DeviceFactory extends DeviceFactory`
*Produces the `Version2*` family — every method returns the modern variant.*

---

## 6. `com.smarthome.strategy`

### `AutomationMode`
*Strategy interface for whole-home automation modes.*
**Pattern roles:** Strategy interface.

**Possible methods**
- `name() : String` — UI-displayable mode name
- `apply(SmartHomeHub) : void` — runs the mode's algorithm against the hub

### `EcoMode implements AutomationMode`
*Energy-saving mode: thermostats → 24°C, on-lights → 50% brightness.*

### `SleepMode implements AutomationMode`
*Night mode: lights off, doors locked, thermostats → 20°C.*

### `AwayMode implements AutomationMode`
*Vacation mode: lights off, doors locked, cameras armed, thermostats → 15°C.*

---

## 7. `com.smarthome.command`

### `DeviceCommand`
*Command interface — one action object per UI gesture.*
**Pattern roles:** Command interface.

**Possible methods**
- `execute() : void` — perform the action
- `undo() : void` — restore pre-execute state
- `describe() : String` — human-readable label for history UI

### `TurnOnCommand / TurnOffCommand / SetTemperatureCommand / LockCommand / UnlockCommand / SetAutomationModeCommand`
*Concrete commands wrapping a `Device` (or `Thermostat`, `Lock`, or
`SmartHomeHub`) as Receiver. Each one captures pre-execute state in a
field so `undo()` can restore it precisely.*

### `CommandInvoker`
*Runs commands and keeps an undo stack.*
**Pattern roles:** Invoker (Command pattern).

**Notable attributes**
- `history : Deque<DeviceCommand>` — LIFO undo stack
- `auditLog : CommandsLogDAO` — optional, writes a row per execute

**Possible methods**
- `execute(DeviceCommand) : void` — runs + pushes onto history
- `canUndo() : boolean`
- `undo() : DeviceCommand` — pops history, calls `command.undo()`
- `getHistory() : List<DeviceCommand>` — read-only view
- `clearHistory() : void`

---

## 8. `com.smarthome.facade`

### `HomeController`
*The single entry point from the UI into the system.*
**Pattern roles:** Facade.

**Notable attributes**
- `hub : SmartHomeHub`
- `invoker : CommandInvoker`
- `eventDAO : DeviceEventDAO`
- `commandsLogDAO : CommandsLogDAO`

**Possible methods**
- `turnOnDevice(String deviceId) : void`
- `turnOffDevice(String deviceId) : void`
- `lockDevice(String deviceId) : void`
- `unlockDevice(String deviceId) : void`
- `setTemperature(String deviceId, double value) : void`
- `setAutomationMode(String modeName) : void`
- `getDevicesForRoom(String roomId) : List<Device>`
- `getEventHistory() : List<DeviceEvent>`
- `getCommandHistory() : List<CommandLog>`
- `createSchedule(ScheduleRequest) : void` — placeholder for M4
- `undoLastAction() : boolean` — convenience over invoker.undo()

### `ScheduleRequest`
*DTO for `createSchedule`. Records `(deviceId, modeName, cronExpression)`.*

---

## 9. `com.smarthome.persistence`

### `Database`
*Owns the singleton SQLite connection.*
**Pattern roles:** Singleton.

**Notable attributes**
- `INSTANCE : Database`
- `connection : Connection`

**Possible methods**
- `getInstance() : Database`
- `forUrl(String) : Database` — test factory for in-memory databases
- `getConnection() : Connection`

### `User / DeviceEvent / CommandLog`
*Plain Java records — DTOs returned by DAOs. Immutable, auto-generated
equals/hashCode/toString.*

### `UserDAO / RoomDAO / DeviceDAO / DeviceEventDAO / CommandsLogDAO`
*Five DAOs isolating SQL behind plain Java APIs.*
**Pattern roles:** DAO.

Each follows the same shape:

**Notable attributes**
- `conn : Connection` — injected (production = singleton; tests = in-memory)

**Possible methods (representative — varies by DAO)**
- `insert(...)` — write a new row using a `PreparedStatement`
- `findById(String) : T`
- `findRecent(int limit) : List<T>` — for audit/event tables
- `findByDevice(String, int) : List<T>`
- `update...(...)` / `delete(String)`

`DeviceDAO` is the most interesting: it round-trips polymorphic device
subtypes by storing `(type, family)` columns and reconstructing via the
right concrete `Device` constructor on read.

---

## 10. `com.smarthome.ui`

### `App extends javafx.application.Application`
*JavaFX entry point. Initialises Database, loads or seeds rooms/devices,
attaches the persistence Observer, and shows the main FXML.*

**Possible methods**
- `start(Stage primaryStage) : void`
- `main(String[] args) : void`

### `MainController`
*Owns the top bar, mode picker, status banner, and bottom navigation.
Constructs the production Facade and shares it with sub-screens.*

### `HomeController`
*Renders the home screen — rooms with their device cards, a "+" button
per room to open the Add-Device modal.*

### `HistoryController`
*Renders the History tab. Loads past events from `DeviceEventDAO` and
appends new ones live via Observer.*

### `DecoratorController`
*Decorator showcase tab. Lets the user wrap a chosen device with
`LoggingDeviceDecorator`, exercise it, and see the captured log.*

### `AddDeviceController`
*Modal controller for the Add-Device dialog. Translates user input
(type + family + name) into the matching factory call, then persists
via `DeviceDAO`.*

### `DaoEventBridge implements Observer`
*Boundary adapter between domain Observer events and the persistence
layer. One instance attaches to every device on startup; each
`update(d, event)` call writes a row to `device_events` and updates
the `devices` table's live state.*

### `HomeBus`
*Tiny static event bus so the top-bar mode/undo actions can tell
whichever screen is mounted to refresh.*

---

## Summary

| Package | Classes | Primary pattern role |
|---|---|---|
| `core` | 5 | Singleton + Iterator |
| `observer` | 2 | Observer |
| `devices` | 5 + 4 + 4 = 13 | Component (Decorator) + Receiver (Command) |
| `devices.decorator` | 3 | Decorator |
| `factory` | 3 | Abstract Factory |
| `strategy` | 4 | Strategy |
| `command` | 8 | Command |
| `facade` | 2 | Facade |
| `persistence` + `dao` | 1 + 8 = 9 | Singleton + DAO |
| `ui` | 8 | Presentation + boundary adapter |
| **Total** | **~57 classes** | **All 9 patterns implemented** |
