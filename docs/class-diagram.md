# Smart Home — Class Diagrams

A focused diagram per design pattern, plus two overview diagrams (system
architecture and full class hierarchy). Each section can be read in
isolation — start with whichever pattern you want to understand first.

> **Viewing this:** GitHub renders Mermaid diagrams natively. Just open
> [this file on github.com](https://github.com/ahmefarouk1234d/smarthome/blob/main/docs/class-diagram.md)
> and every code block below appears as a rendered diagram.
>
> For the printed report, use the higher-resolution PlantUML version
> in [`class-diagram.puml`](class-diagram.puml) (see [`README.md`](README.md)
> for rendering instructions).

---

## Table of contents

1. [System architecture (layered view)](#1-system-architecture-layered-view)
2. [Singleton — `SmartHomeHub`, `Database`](#2-singleton)
3. [Iterator — `Room.devices()` and `RoomIterator`](#3-iterator)
4. [Observer — `Device` and friends](#4-observer)
5. [Abstract Factory + Factory Methods](#5-abstract-factory--factory-methods)
6. [Strategy — automation modes](#6-strategy)
7. [Command — actions with undo](#7-command)
8. [Decorator — wrapping devices](#8-decorator)
9. [DAO — persistence layer](#9-dao)
10. [Facade — `HomeController`](#10-facade)
11. [Putting it together — request flow](#11-putting-it-together)

---

## 1. System architecture (layered view)

How packages stack. Each layer depends only on layers below it.

```mermaid
flowchart TB
    subgraph UI [Presentation — JavaFX]
        App
        MainCtrl[MainController]
        HomeUI[Home / History / Decorator controllers]
        Bridge[DaoEventBridge]
    end

    subgraph APP [Application]
        Facade[HomeController «Facade»]
        Invoker[CommandInvoker]
        Cmds[6× DeviceCommand]
    end

    subgraph DOMAIN [Domain]
        Hub[SmartHomeHub «Singleton»]
        Rooms[Room «Iterator»]
        Devices[Device hierarchy «Observer»]
        Factories[2× DeviceFactory «Abstract Factory»]
        Modes[3× AutomationMode «Strategy»]
        Decorators[2× DeviceDecorator]
    end

    subgraph PERSIST [Persistence]
        DB[(Database «Singleton»)]
        DAOs[5× DAO]
        SQLite[(smarthome.db)]
    end

    UI --> APP
    APP --> DOMAIN
    Devices --> Bridge
    Bridge --> DAOs
    APP --> DAOs
    DAOs --> DB
    DB --> SQLite
```

---

## 2. Singleton

One global instance per class. Used for the smart-home **hub** and the
**database connection**.

```mermaid
classDiagram
    class SmartHomeHub {
        «Singleton»
        -INSTANCE : SmartHomeHub$
        -roomsById : Map
        +getInstance() SmartHomeHub$
        +addRoom(Room)
        +setAutomationMode(AutomationMode)
    }

    class Database {
        «Singleton»
        -INSTANCE : Database$
        -connection : Connection
        +getInstance() Database$
        +getConnection() Connection
    }

    note for SmartHomeHub "Private constructor.\nThread-safe via eager init."
    note for Database "Opens SQLite once.\nRuns schema + migrations on startup."
```

**Methods that *make* it the pattern**: `getInstance()` (static accessor),
private constructor, single `INSTANCE` field.

---

## 3. Iterator

`Room.devices()` returns `Enumeration<Device>` — that's the rubric
requirement. Plus a custom **GoF-style** Iterator (`RoomIterator`) at the
hub level for stronger pattern coverage.

```mermaid
classDiagram
    class Room {
        -devicesById : Map
        +devices() Enumeration~Device~
    }

    class SmartHomeHub {
        +createIterator() RoomIterator
    }

    class RoomIterableCollection {
        «interface»
        +createIterator() RoomIterator
    }

    class RoomIterator {
        «interface»
        +hasMore() boolean
        +getNext() Room
    }

    class HubRoomIterator {
        -position : int
        +hasMore() boolean
        +getNext() Room
    }

    SmartHomeHub ..|> RoomIterableCollection
    HubRoomIterator ..|> RoomIterator
    SmartHomeHub --> HubRoomIterator : creates
```

Two iterator implementations on purpose: `Enumeration<Device>` is what the
brief literally asks for; `RoomIterator` is the textbook GoF version with
`hasMore() / getNext()`.

---

## 4. Observer

Devices fire `notifyObservers(event)` after every state change. Multiple
listeners attach: live UI controllers + the persistence-side
`DaoEventBridge`.

```mermaid
classDiagram
    class Observer {
        «interface»
        +update(Device d, String event)
    }

    class Observable {
        «interface»
        +attach(Observer)
        +detach(Observer)
        +notifyObservers(String event)
    }

    class Device {
        «abstract»
        -observers : List~Observer~
        +turnOn()
        +turnOff()
        +notifyObservers(String)
    }

    class DaoEventBridge {
        +update(Device, String)
    }

    Device ..|> Observable
    DaoEventBridge ..|> Observer
    Device o-- Observer : "list of"
```

**Push, not pull.** When `Device.turnOn()` runs, observers are pushed
both the device and the event string — no polling.

---

## 5. Abstract Factory + Factory Methods

`DeviceFactory` is the Abstract Factory; each `create…` method is a
Factory Method. Two **families** of products: `Version1` (legacy) and
`Version2` (modern). Each family produces the full set of 4 device
types — so no `UnsupportedOperationException` throws (LSP-clean).

```mermaid
classDiagram
    class DeviceFactory {
        «abstract»
        +createLight(String) Device
        +createThermostat(String) Device
        +createDoorLock(String) Device
        +createCamera(String) Device
        #newId() String
    }

    class Version1DeviceFactory
    class Version2DeviceFactory

    class Light
    class Thermostat
    class Lock
    class Camera

    Version1DeviceFactory --|> DeviceFactory
    Version2DeviceFactory --|> DeviceFactory

    Version1DeviceFactory ..> Light : creates Version1Light
    Version1DeviceFactory ..> Thermostat : creates Version1Thermostat
    Version1DeviceFactory ..> Lock : creates Version1Lock
    Version1DeviceFactory ..> Camera : creates Version1Camera
    Version2DeviceFactory ..> Light : creates Version2Light
    Version2DeviceFactory ..> Thermostat : creates Version2Thermostat
    Version2DeviceFactory ..> Lock : creates Version2Lock
    Version2DeviceFactory ..> Camera : creates Version2Camera
```

(For brevity each family's concrete subclasses — `Version1Light` etc. —
are shown only as the type their family creates.)

---

## 6. Strategy

`AutomationMode` is the strategy interface. Three concrete strategies
(`Eco`, `Sleep`, `Away`) define different home behaviours. The
`SmartHomeHub` is the **Context** — it holds the current strategy and
delegates via `applyAutomationMode()`.

```mermaid
classDiagram
    class AutomationMode {
        «interface»
        +name() String
        +apply(SmartHomeHub)
    }

    class EcoMode {
        +apply(SmartHomeHub)
    }
    class SleepMode {
        +apply(SmartHomeHub)
    }
    class AwayMode {
        +apply(SmartHomeHub)
    }

    class SmartHomeHub {
        «Context»
        -automationMode : AutomationMode
        +setAutomationMode(AutomationMode)
        +applyAutomationMode()
    }

    EcoMode ..|> AutomationMode
    SleepMode ..|> AutomationMode
    AwayMode ..|> AutomationMode
    SmartHomeHub --> AutomationMode : "current strategy"
```

Each `apply()` walks rooms via Iterator and mutates devices via setters
that fire Observer events — three patterns collaborating in one call.

---

## 7. Command

Each user action is a `DeviceCommand` object — encapsulates `execute()`
and `undo()`. The `CommandInvoker` keeps a history stack so the most
recent action can be reversed. Optionally writes a row to `commands_log`
on every execute.

```mermaid
classDiagram
    class DeviceCommand {
        «interface»
        +execute()
        +undo()
        +describe() String
    }

    class TurnOnCommand
    class TurnOffCommand
    class SetTemperatureCommand
    class LockCommand
    class UnlockCommand
    class SetAutomationModeCommand

    class CommandInvoker {
        «Invoker»
        -history : Deque~DeviceCommand~
        -auditLog : CommandsLogDAO
        +execute(DeviceCommand)
        +undo() DeviceCommand
        +canUndo() boolean
    }

    class Device {
        «Receiver»
    }

    TurnOnCommand ..|> DeviceCommand
    TurnOffCommand ..|> DeviceCommand
    SetTemperatureCommand ..|> DeviceCommand
    LockCommand ..|> DeviceCommand
    UnlockCommand ..|> DeviceCommand
    SetAutomationModeCommand ..|> DeviceCommand

    CommandInvoker o-- DeviceCommand : "undo stack"
    DeviceCommand ..> Device : "delegates work to"
```

**Invoker never imports Device directly** — RG's litmus test for a
correct Command implementation. The Invoker only knows `DeviceCommand`.

---

## 8. Decorator

Wraps a `Device` to add cross-cutting behaviour (logging, energy
tracking) without modifying its class. Decorators stack — you can wrap
a wrapped wrapper.

```mermaid
classDiagram
    class Device {
        «Component»
        +turnOn()
        +turnOff()
    }

    class DeviceDecorator {
        «abstract Decorator»
        #wrappee : Device
        +turnOn()
        +turnOff()
    }

    class LoggingDeviceDecorator {
        -log : List~String~
        +getLog() List~String~
    }

    class EnergyTrackedDecorator {
        -totalOnMillis : long
        +getTotalOnMillis() long
    }

    DeviceDecorator --|> Device
    LoggingDeviceDecorator --|> DeviceDecorator
    EnergyTrackedDecorator --|> DeviceDecorator
    DeviceDecorator o-- Device : wraps
```

Each decorator carries the wrapped device's `id` and `name` — so the
wrapper IS the same logical device for room indexing and audit logs.

---

## 9. DAO

Five DAOs isolate all SQL behind plain Java APIs. The domain layer never
sees JDBC. Every DAO has a dual constructor: production uses the singleton
`Database`; tests inject an in-memory `Connection`.

```mermaid
classDiagram
    class Database {
        «Singleton»
        +getInstance() Database$
        +getConnection() Connection
    }

    class UserDAO {
        «DAO»
        +insert(User)
        +findById(String) User
        +verifyPin(String, String) boolean
    }
    class RoomDAO {
        «DAO»
        +insert(Room)
        +findAll() List~Room~
        +delete(String)
    }
    class DeviceDAO {
        «DAO»
        +insert(Device, roomId)
        +findByRoom(String) List~Device~
        +updateState(Device)
    }
    class DeviceEventDAO {
        «DAO»
        +insert(deviceId, eventType)
        +findRecent(int) List~DeviceEvent~
    }
    class CommandsLogDAO {
        «DAO»
        +insert(cmdId, deviceId, action, params, result)
        +findRecent(int) List~CommandLog~
    }

    UserDAO --> Database
    RoomDAO --> Database
    DeviceDAO --> Database
    DeviceEventDAO --> Database
    CommandsLogDAO --> Database

    DeviceDAO ..> DeviceFactory : "uses Abstract Factory\nat deserialization"
```

`DeviceDAO` is the most interesting — it round-trips polymorphic `Device`
subtypes through one row, using the Abstract Factory at runtime to
reconstruct the right family/type.

---

## 10. Facade

`HomeController` is the only class the UI talks to. It delegates to the
hub, the command invoker, and the DAOs — never reimplements domain logic.

```mermaid
classDiagram
    class HomeController {
        «Facade»
        -hub : SmartHomeHub
        -invoker : CommandInvoker
        -eventDAO : DeviceEventDAO
        -commandsLogDAO : CommandsLogDAO
        +turnOnDevice(String)
        +turnOffDevice(String)
        +lockDevice(String)
        +unlockDevice(String)
        +setTemperature(String, double)
        +setAutomationMode(String)
        +getDevicesForRoom(String) List~Device~
        +getEventHistory() List~DeviceEvent~
        +getCommandHistory() List~CommandLog~
        +undoLastAction() boolean
    }

    class JavaFXController {
        «Client»
    }

    class SmartHomeHub
    class CommandInvoker
    class DeviceEventDAO
    class CommandsLogDAO

    JavaFXController --> HomeController : "calls only this"
    HomeController --> SmartHomeHub
    HomeController --> CommandInvoker
    HomeController --> DeviceEventDAO
    HomeController --> CommandsLogDAO
```

**Thin Facade rule** (RG): every method here is one or two lines —
build a Command and hand it to the Invoker, or read from a DAO. No
domain logic.

---

## 11. Putting it together — request flow

How a single user action flows through the system, exercising 6 patterns
in one trip.

```mermaid
sequenceDiagram
    participant User
    participant UI as JavaFX Controller
    participant F as HomeController «Facade»
    participant I as CommandInvoker
    participant C as TurnOnCommand
    participant D as Device «Receiver»
    participant Obs as Observers
    participant DAO as DeviceEventDAO

    User->>UI: tap "Turn On"
    UI->>F: turnOnDevice(id)
    F->>F: findDevice(id)
    F->>I: execute(new TurnOnCommand(d))
    I->>C: execute()
    C->>D: turnOn()
    D->>Obs: notifyObservers("TURNED_ON")
    Obs->>UI: refresh card
    Obs->>DAO: insert(deviceId, "TURNED_ON")
    I->>I: history.push(C)
    Note over I: undoable later
```

Patterns visible in this single flow:
- **Facade** — UI calls only `HomeController`
- **Command** — every action becomes a `TurnOnCommand` object
- **Receiver** (Command) — `Device` does the actual work
- **Observer** — `notifyObservers` fans out to UI and DAO
- **DAO** — `DeviceEventDAO.insert` writes to SQLite
- **Singleton** — `HomeController` reaches `SmartHomeHub.getInstance()` to find the device

---

## Pattern roles at a glance

| Pattern | Roles | Key classes |
|---|---|---|
| **Singleton** | One instance | `SmartHomeHub`, `Database` |
| **Iterator** | Aggregate + Iterator | `Room` (Enumeration), `RoomIterator` |
| **Observer** | Subject + Observer | `Device`, `Observer` interface, `DaoEventBridge` |
| **Abstract Factory** | Abstract + 2 concretes | `DeviceFactory`, `Version1/Version2DeviceFactory` |
| **Strategy** | Strategy + concretes + Context | `AutomationMode`, `Eco/Sleep/Away`, `SmartHomeHub` |
| **Command** | Command + Concretes + Invoker + Receiver | `DeviceCommand`, 6 commands, `CommandInvoker`, `Device` |
| **Decorator** | Component + Base + Concretes | `Device`, `DeviceDecorator`, `LoggingDeviceDecorator`, `EnergyTrackedDecorator` |
| **DAO** | Persistence isolation | 5 DAOs |
| **Facade** | Single entry point | `HomeController` |
