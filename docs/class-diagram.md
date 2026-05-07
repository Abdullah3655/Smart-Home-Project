# Smart Home — Class Diagrams (by layer)

The system is organised into **4 layers**: Presentation, Application,
Domain, and Persistence. Each layer below has its own focused class
diagram showing the classes inside it. Cross-layer dependencies are
shown only in the architecture overview at the top — keeping each
per-layer diagram clean of crossing arrows.

> **Viewing this:** GitHub renders Mermaid natively — open
> [this file on github.com](https://github.com/ahmefarouk1234d/smarthome/blob/main/docs/class-diagram.md)
> and every diagram below appears as an SVG.

---

## Table of contents

1. [Architecture overview](#1-architecture-overview)
2. [Layer A — Presentation (JavaFX UI)](#2-layer-a--presentation-javafx-ui)
3. [Layer B — Application (Facade + Command)](#3-layer-b--application-facade--command)
4. [Layer C — Domain (Hub, Devices, Patterns)](#4-layer-c--domain-hub-devices-patterns)
5. [Layer D — Persistence (Database + DAOs)](#5-layer-d--persistence-database--daos)
6. [Putting it together — request flow](#6-putting-it-together--request-flow)

---

## 1. Architecture overview

How the four layers stack and depend on each other. Dependencies flow
downward only — UI never imports DAOs directly.

```mermaid
flowchart TB
    subgraph A [A — Presentation]
        App
        MainCtrl[MainController]
        Screens[Home / History / Decorator<br/>+ AddDevice]
        Bridge[DaoEventBridge]
    end

    subgraph B [B — Application]
        Facade[HomeController «Facade»]
        Invoker[CommandInvoker]
        Cmds[6× DeviceCommand]
    end

    subgraph C [C — Domain]
        Hub[SmartHomeHub «Singleton»]
        Rooms[Room «Iterator»]
        Devices[Device hierarchy «Observer»]
        Factories[2× DeviceFactory «Abstract Factory»]
        Modes[3× AutomationMode «Strategy»]
        Decorators[2× DeviceDecorator]
    end

    subgraph D [D — Persistence]
        DB[(Database «Singleton»)]
        DAOs[5× DAO]
        SQLite[(smarthome.db)]
    end

    A --> B
    B --> C
    A --> C
    A --> D
    B --> D
    DAOs --> DB
    DB --> SQLite
```

---

## 2. Layer A — Presentation (JavaFX UI)

The visible application: an `App` that boots JavaFX, a `MainController`
that owns the chrome (top bar, mode picker, status banner, bottom nav),
plus three screen controllers swapped through the central host.
`DaoEventBridge` is a special Observer that lives at the layer boundary
and forwards device events to the Persistence layer.

**Depends on:** Application layer (calls Facade), Persistence layer
(via `DaoEventBridge`).

```mermaid
classDiagram
    direction TB

    class App {
        «JavaFX entry»
        +start(Stage)
    }

    class MainController {
        «top bar + nav»
        -sharedFacade : HomeController
        +applyMode(String)
        +loadScreen(String)
    }

    class HomeUIController {
        «Home screen»
        +renderAllRooms()
        +openAddDeviceModal(Room)
    }

    class HistoryController {
        «History screen»
        +loadHistoricalEvents()
        +attachLiveObserver()
    }

    class DecoratorController {
        «Decorator showcase»
        +onWrap()
        +onUnwrap()
    }

    class AddDeviceController {
        «Add Device modal»
        +onAdd()
    }

    class DaoEventBridge {
        «Observer adapter»
        +update(Device, String)
    }

    class HomeBus {
        «event bus»
        +subscribe(Runnable)$
        +notifyDataChanged()$
    }

    App --> MainController : loads main.fxml
    MainController --> HomeUIController : swaps in
    MainController --> HistoryController : swaps in
    MainController --> DecoratorController : swaps in
    HomeUIController --> AddDeviceController : opens modal
```

**Pattern roles in this layer:** `DaoEventBridge` plays the **Observer**
role at the layer boundary. The whole presentation layer demonstrates
the **Facade** rubric line — every controller's mutation path goes
through `HomeController` exclusively.

---

## 3. Layer B — Application (Facade + Command)

The orchestration layer. `HomeController` is the Facade — the single
class the UI calls into. It wraps every mutation in a `DeviceCommand`
and hands it to the `CommandInvoker`, which executes and stores the
command on the undo stack.

**Depends on:** Domain layer (Hub + Device receivers), Persistence layer (DAOs).

```mermaid
classDiagram
    direction TB

    class HomeController {
        «Facade»
        +turnOnDevice(String)
        +turnOffDevice(String)
        +lockDevice(String)
        +setTemperature(String, double)
        +setAutomationMode(String)
        +getEventHistory() List~DeviceEvent~
        +undoLastAction() boolean
    }

    class CommandInvoker {
        «Invoker»
        -history : Deque~DeviceCommand~
        +execute(DeviceCommand)
        +undo() DeviceCommand
        +canUndo() boolean
    }

    class DeviceCommand {
        «interface, Command»
        +execute()
        +undo()
        +describe() String
    }

    class TurnOnCommand {
        «Concrete Command»
        +execute()
        +undo()
    }

    class TurnOffCommand {
        «Concrete Command»
    }

    class SetTemperatureCommand {
        «Concrete Command»
    }

    class LockCommand_UnlockCommand {
        «Concrete Commands»
    }

    class SetAutomationModeCommand {
        «Concrete Command»
    }

    TurnOnCommand ..|> DeviceCommand
    TurnOffCommand ..|> DeviceCommand
    SetTemperatureCommand ..|> DeviceCommand
    LockCommand_UnlockCommand ..|> DeviceCommand
    SetAutomationModeCommand ..|> DeviceCommand

    HomeController --> CommandInvoker
    HomeController ..> DeviceCommand : creates
    CommandInvoker o-- DeviceCommand : history stack
```

**Pattern roles in this layer:** **Facade** (`HomeController`),
**Command** (interface + 6 concretes + `CommandInvoker`). The Invoker
imports only `DeviceCommand` — never any concrete Receiver — which is
RG's litmus test for a correct Command implementation.

---

## 4. Layer C — Domain (Hub, Devices, Patterns)

The pure business model — no JavaFX imports, no SQL imports. Houses
six of the nine patterns: Singleton (Hub), Iterator (Room), Observer
(Device), Abstract Factory (DeviceFactory), Strategy (AutomationMode),
and Decorator (DeviceDecorator).

**Depends on:** *nothing higher in the stack*. The Domain is reusable in
isolation.

### 4.1 Core entities — Hub, Room, Device

```mermaid
classDiagram
    direction TB

    class SmartHomeHub {
        «Singleton, Strategy Context»
        -INSTANCE : SmartHomeHub$
        -roomsById : Map
        -automationMode : AutomationMode
        +getInstance() SmartHomeHub$
        +addRoom(Room)
        +applyAutomationMode()
        +createIterator() RoomIterator
    }

    class Room {
        «Iterator host»
        -devicesById : Map
        +addDevice(Device)
        +devices() Enumeration~Device~
    }

    class RoomIterator {
        «interface, Iterator»
        +hasMore() boolean
        +getNext() Room
    }

    class Device {
        «abstract»
        -id, name, observers
        +turnOn()
        +turnOff()
        +notifyObservers(String)
    }

    class Light {
        +setBrightness(int)
    }

    class Thermostat {
        +setTemperature(double)
    }

    class Lock {
        +lock()
        +unlock()
    }

    class Camera

    Light --|> Device
    Thermostat --|> Device
    Lock --|> Device
    Camera --|> Device

    SmartHomeHub o-- Room : "rooms"
    Room o-- Device : "devices"
    SmartHomeHub --> RoomIterator : creates
```

### 4.2 Observer — Device as Subject

```mermaid
classDiagram
    direction LR

    class Observer {
        «interface»
        +update(Device, String)
    }

    class Observable {
        «interface»
        +attach(Observer)
        +detach(Observer)
        +notifyObservers(String)
    }

    class Device {
        «abstract Subject»
        -observers : List~Observer~
        +notifyObservers(String)
    }

    Device ..|> Observable
    Device o-- Observer : list of
```

### 4.3 Abstract Factory — two device families

```mermaid
classDiagram
    direction TB

    class DeviceFactory {
        «abstract, Abstract Factory»
        +createLight(String) Device
        +createThermostat(String) Device
        +createDoorLock(String) Device
        +createCamera(String) Device
    }

    class Version1DeviceFactory {
        «Concrete Factory»
        legacy variants
    }

    class Version2DeviceFactory {
        «Concrete Factory»
        modern variants
    }

    class Version1Light {
        Version1Thermostat
        Version1Lock
        Version1Camera
    }

    class Version2Light {
        Version2Thermostat
        Version2Lock
        Version2Camera
    }

    Version1DeviceFactory --|> DeviceFactory
    Version2DeviceFactory --|> DeviceFactory
    Version1DeviceFactory ..> Version1Light : creates family
    Version2DeviceFactory ..> Version2Light : creates family
```

### 4.4 Strategy — automation modes

```mermaid
classDiagram
    direction TB

    class AutomationMode {
        «interface, Strategy»
        +name() String
        +apply(SmartHomeHub)
    }

    class EcoMode {
        «Concrete Strategy»
    }
    class SleepMode {
        «Concrete Strategy»
    }
    class AwayMode {
        «Concrete Strategy»
    }

    class SmartHomeHub {
        «Context»
        +applyAutomationMode()
    }

    EcoMode ..|> AutomationMode
    SleepMode ..|> AutomationMode
    AwayMode ..|> AutomationMode
    SmartHomeHub --> AutomationMode : current
```

### 4.5 Decorator — wrapping devices

```mermaid
classDiagram
    direction TB

    class Device {
        «Component»
        +turnOn()
        +turnOff()
    }

    class DeviceDecorator {
        «abstract Decorator»
        #wrappee : Device
    }

    class LoggingDeviceDecorator {
        «Concrete Decorator»
        -log : List~String~
    }

    class EnergyTrackedDecorator {
        «Concrete Decorator»
        -totalOnMillis : long
    }

    DeviceDecorator --|> Device
    LoggingDeviceDecorator --|> DeviceDecorator
    EnergyTrackedDecorator --|> DeviceDecorator
    DeviceDecorator o-- Device : wraps
```

---

## 5. Layer D — Persistence (Database + DAOs)

SQL isolation. The `Database` is a Singleton holding the JDBC connection;
each DAO wraps a single table behind plain Java methods. The Domain layer
never sees JDBC.

**Depends on:** Domain (`Device`, `Room` as method args) and Factory
(DeviceDAO uses Abstract Factory at deserialization).

```mermaid
classDiagram
    direction TB

    class Database {
        «Singleton»
        -INSTANCE : Database$
        +getInstance() Database$
        +getConnection()
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
        +insert(...)
        +findRecent(int) List~CommandLog~
    }

    class User {
        «record, DTO»
    }
    class DeviceEvent {
        «record, DTO»
    }
    class CommandLog {
        «record, DTO»
    }

    UserDAO --> Database
    RoomDAO --> Database
    DeviceDAO --> Database
    DeviceEventDAO --> Database
    CommandsLogDAO --> Database

    UserDAO ..> User
    DeviceEventDAO ..> DeviceEvent
    CommandsLogDAO ..> CommandLog
```

**Pattern roles in this layer:** **Singleton** (Database) and **DAO**
(5 DAOs). `DeviceDAO` also uses the Domain's Abstract Factory at
deserialization to round-trip polymorphic device subtypes.

---

## 6. Putting it together — request flow

How a single user gesture flows across all four layers, exercising six
patterns in one trip.

```mermaid
sequenceDiagram
    actor User
    participant UI as JavaFX (A)
    participant F as HomeController «Facade» (B)
    participant I as CommandInvoker (B)
    participant C as TurnOnCommand (B)
    participant D as Device «Receiver» (C)
    participant Obs as Observers
    participant DAO as DAOs (D)

    User->>UI: tap "Turn On"
    UI->>F: turnOnDevice(id)
    F->>F: findDevice(id)
    F->>I: execute(new TurnOnCommand(d))
    I->>C: execute()
    C->>D: turnOn()
    D->>Obs: notifyObservers("TURNED_ON")
    Obs->>UI: refresh card
    Obs->>DAO: persist event
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

## Pattern roles by layer at a glance

| Layer | Patterns it owns |
|---|---|
| **A — Presentation** | Observer (`DaoEventBridge` at the boundary) |
| **B — Application** | Facade · Command |
| **C — Domain** | Singleton · Iterator · Observer · Abstract Factory · Strategy · Decorator |
| **D — Persistence** | Singleton · DAO |
| **All 9** | spread across A/B/C/D — but the Domain layer is the heart |
