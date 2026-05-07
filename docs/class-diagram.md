# Smart Home — Class Diagrams (by layer)

The system is organised into **4 layers**: Presentation, Application,
Domain, and Persistence. Each layer gets its own focused class diagram
below — showing the classes inside it plus the boundary arrows pointing
to the layers it depends on.

To keep each diagram readable, repetitive concrete classes are
represented by a single example per family (e.g. one `EcoMode` stands
in for `EcoMode/SleepMode/AwayMode`). Notes call out the omitted siblings.

> **Viewing this:** GitHub renders Mermaid natively — open
> [this file on github.com](https://github.com/ahmefarouk1234d/smarthome/blob/main/docs/class-diagram.md)
> and every diagram below appears as an SVG.
> For the printed report, see the higher-resolution
> [`class-diagram.puml`](class-diagram.puml).

---

## Table of contents

1. [Layered architecture overview](#1-layered-architecture-overview)
2. [Layer A — Presentation (JavaFX UI)](#2-layer-a--presentation-javafx-ui)
3. [Layer B — Application (Facade + Command)](#3-layer-b--application-facade--command)
4. [Layer C — Domain (Hub, Devices, Patterns)](#4-layer-c--domain-hub-devices-patterns)
5. [Layer D — Persistence (Database + DAOs)](#5-layer-d--persistence-database--daos)
6. [Putting it together — request flow](#6-putting-it-together--request-flow)

---

## 1. Layered architecture overview

How the four layers stack. Dependencies flow downward only — UI never
imports DAOs directly, the Domain layer is reusable in isolation.

```mermaid
flowchart TB
    subgraph A [A — Presentation]
        App
        MainCtrl[MainController]
        Screens[Home / History / Decorator<br/>controllers]
        AddDev[AddDeviceController]
        Bridge[DaoEventBridge]
        Bus[HomeBus]
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

**Imports allowed from:** Application (Facade) and Persistence (DAOs through `DaoEventBridge` only).

```mermaid
classDiagram
    direction TB

    class App {
        «JavaFX entry»
        +start(Stage)
        +seedDemoDataIfEmpty()
        +loadPersistedState()
        +attachPersistenceObservers()
    }

    class MainController {
        «top bar + nav»
        -sharedFacade : HomeController
        +applyMode(String, Button)
        +loadScreen(String, Button)
    }

    class HomeUIController {
        «Home screen»
        -facade : HomeController
        +renderAllRooms()
        +openAddDeviceModal(Room)
    }

    class HistoryController {
        «History screen»
        -facade : HomeController
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
        -targetRoom : Room
        +onAdd()
    }

    class DaoEventBridge {
        «Observer adapter»
        -eventDao : DeviceEventDAO
        -deviceDao : DeviceDAO
        +update(Device, String)
    }

    class HomeBus {
        «event bus»
        +subscribe(Runnable)$
        +notifyDataChanged()$
    }

    class FacadeRef
    class DAORef

    App --> MainController : loads main.fxml
    MainController --> HomeUIController : swaps in
    MainController --> HistoryController : swaps in
    MainController --> DecoratorController : swaps in
    HomeUIController --> AddDeviceController : opens modal

    %% Boundary arrows leaving this layer
    MainController ..> FacadeRef : "→ Application layer"
    HomeUIController ..> FacadeRef : "→ Application layer"
    AddDeviceController ..> FacadeRef : "→ Application layer"
    DaoEventBridge ..> DAORef : "→ Persistence layer"

    note for FacadeRef "HomeController «Facade»\n(Application layer)"
    note for DAORef "DeviceDAO + DeviceEventDAO\n(Persistence layer)"
```

**Pattern roles in this layer:** `DaoEventBridge` is an **Observer**.
The whole presentation layer demonstrates the **Facade** rubric line
("controllers must call a facade service") — every controller's
mutation path goes through `HomeController` exclusively.

---

## 3. Layer B — Application (Facade + Command)

The orchestration layer. `HomeController` is the Facade — the single
class the UI calls into. It wraps every mutation in a `DeviceCommand`
and hands it to the `CommandInvoker`, which executes and stores the
command on the undo stack.

**Imports allowed from:** Domain (Hub, Devices, Strategies) and Persistence (DAOs).

```mermaid
classDiagram
    direction TB

    class HomeController {
        «Facade»
        -hub : SmartHomeHub
        -invoker : CommandInvoker
        -eventDAO : DeviceEventDAO
        -commandsLogDAO : CommandsLogDAO
        +turnOnDevice(String)
        +turnOffDevice(String)
        +lockDevice / unlockDevice (String)
        +setTemperature(String, double)
        +setAutomationMode(String)
        +getDevicesForRoom(String) List~Device~
        +getEventHistory() List~DeviceEvent~
        +undoLastAction() boolean
    }

    class CommandInvoker {
        «Invoker»
        -history : Deque~DeviceCommand~
        -auditLog : CommandsLogDAO
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
        -receiver : Device
        -wasOnBefore : boolean
    }

    class HubRef
    class DevicesRef
    class DAORef

    TurnOnCommand ..|> DeviceCommand
    CommandInvoker o-- DeviceCommand : "history stack"
    HomeController --> CommandInvoker
    HomeController ..> TurnOnCommand : "creates"

    %% Boundary arrows leaving this layer
    HomeController ..> HubRef : "→ Domain layer"
    TurnOnCommand ..> DevicesRef : "→ Domain (Receiver)"
    HomeController ..> DAORef : "→ Persistence layer"
    CommandInvoker ..> DAORef : "→ Persistence (audit log)"

    note for TurnOnCommand "+ TurnOffCommand\n+ SetTemperatureCommand\n+ LockCommand\n+ UnlockCommand\n+ SetAutomationModeCommand"
    note for HubRef "SmartHomeHub\n(Domain layer)"
    note for DevicesRef "Device hierarchy\n(Domain layer)"
    note for DAORef "DeviceEventDAO\nCommandsLogDAO\n(Persistence layer)"
```

**Pattern roles in this layer:** **Facade** (`HomeController`),
**Command** (interface + 6 concretes + `CommandInvoker`).

---

## 4. Layer C — Domain (Hub, Devices, Patterns)

The pure business model — no JavaFX imports, no SQL imports. Owns the
core entities (`SmartHomeHub`, `Room`, `Device`) and the foundational
patterns (Singleton, Iterator, Observer, Abstract Factory, Strategy,
Decorator).

**Imports allowed from:** *nothing higher in the stack*. The Domain layer
is reusable in isolation.

```mermaid
classDiagram
    direction TB

    %% Singleton + Strategy Context + Iterator host
    class SmartHomeHub {
        «Singleton, Strategy Context»
        -INSTANCE : SmartHomeHub$
        -roomsById : Map
        -automationMode : AutomationMode
        +getInstance() SmartHomeHub$
        +addRoom(Room)
        +setAutomationMode(AutomationMode)
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

    %% Observer
    class Observer {
        «interface»
        +update(Device, String)
    }
    class Observable {
        «interface»
        +attach / detach
        +notifyObservers(String)
    }

    %% Devices — Component (Decorator) + Receiver (Command) + Subject (Observer)
    class Device {
        «abstract»
        -id, name, observers
        +turnOn() / turnOff()
        +notifyObservers(String)
    }
    class Light
    class Thermostat
    class DeviceDecorator {
        «abstract Decorator»
        #wrappee : Device
    }
    class LoggingDeviceDecorator
    class Version2Variants {
        «represents»
        Version2Light
        Version2Thermostat
        Version2Lock
        Version2Camera
    }

    %% Abstract Factory
    class DeviceFactory {
        «abstract, Abstract Factory»
        +createLight(String) Device
        +createThermostat(String) Device
        +createDoorLock(String) Device
        +createCamera(String) Device
    }
    class Version2DeviceFactory {
        «Concrete Factory»
    }

    %% Strategy
    class AutomationMode {
        «interface, Strategy»
        +name() String
        +apply(SmartHomeHub)
    }
    class EcoMode {
        «Concrete Strategy»
    }

    %% Inheritance + realization
    Light --|> Device
    Thermostat --|> Device
    DeviceDecorator --|> Device
    LoggingDeviceDecorator --|> DeviceDecorator
    Version2Variants --|> Device
    Version2DeviceFactory --|> DeviceFactory
    EcoMode ..|> AutomationMode
    Device ..|> Observable

    %% Composition + dependencies inside the layer
    SmartHomeHub o-- Room : "rooms"
    Room o-- Device : "devices"
    Device o-- Observer : "observers"
    DeviceDecorator o-- Device : "wraps"
    SmartHomeHub --> AutomationMode : "current strategy"
    SmartHomeHub --> RoomIterator : "creates"
    Version2DeviceFactory ..> Version2Variants : "creates"

    note for Light "+ Lock\n+ Camera"
    note for Version2Variants "Version1 family also exists\n(Version1Light/Thermostat/Lock/Camera)"
    note for Version2DeviceFactory "+ Version1DeviceFactory\n(legacy family)"
    note for EcoMode "+ SleepMode\n+ AwayMode"
    note for LoggingDeviceDecorator "+ EnergyTrackedDecorator"
```

**Pattern roles in this layer:** **Singleton** (Hub), **Iterator** (Room
+ RoomIterator), **Observer** (Device implements Observable), **Abstract
Factory + Factory Methods** (DeviceFactory + 2 family factories),
**Strategy** (AutomationMode + 3 modes), **Decorator** (DeviceDecorator
+ 2 wrappers). Six of the nine patterns live entirely in this layer.

---

## 5. Layer D — Persistence (Database + DAOs)

SQL isolation. The `Database` is a Singleton holding the JDBC connection;
each DAO wraps a single table behind plain Java methods. The domain
layer never sees JDBC.

**Imports allowed from:** Domain (`Device`, `Room`, etc. as method args)
and `factory` (DeviceDAO uses Abstract Factory at deserialization).

```mermaid
classDiagram
    direction TB

    class Database {
        «Singleton»
        -INSTANCE : Database$
        -connection : Connection
        +getInstance() Database$
        +forUrl(String) Database$
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

    class User {
        «record, DTO»
        userId, name, pin, role
    }
    class DeviceEvent {
        «record, DTO»
        eventId, deviceId, eventType, timestamp
    }
    class CommandLog {
        «record, DTO»
        commandId, deviceId, action, paramsJson, result, timestamp
    }

    class FactoryRef
    class DomainRef

    UserDAO --> Database
    RoomDAO --> Database
    DeviceDAO --> Database
    DeviceEventDAO --> Database
    CommandsLogDAO --> Database

    UserDAO ..> User : returns
    DeviceEventDAO ..> DeviceEvent : returns
    CommandsLogDAO ..> CommandLog : returns

    %% DeviceDAO uses Abstract Factory at runtime
    DeviceDAO ..> FactoryRef : "reconstructs via\nAbstract Factory"
    DeviceDAO ..> DomainRef : "round-trips Device"
    RoomDAO ..> DomainRef : "round-trips Room"

    note for FactoryRef "DeviceFactory hierarchy\n(Domain layer)"
    note for DomainRef "Device, Room\n(Domain layer)"
```

**Pattern roles in this layer:** **Singleton** (Database) and **DAO**
(5 DAOs). `DeviceDAO` also calls into the Domain's Abstract Factory
at deserialization — visible as the dashed arrow leaving the layer.

---

## 6. Putting it together — request flow

How a single user gesture flows across all four layers, exercising six
patterns in one trip.

```mermaid
sequenceDiagram
    actor User
    participant UI as JavaFX (Layer A)
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
| **A — Presentation** | Observer (`DaoEventBridge`) at the boundary |
| **B — Application** | Facade · Command |
| **C — Domain** | Singleton · Iterator · Observer · Abstract Factory · Strategy · Decorator |
| **D — Persistence** | Singleton · DAO |
| **All 9** | spread across A/B/C/D — but the Domain layer is the heart |
