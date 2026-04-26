# Mido's Task List — Foundation Layer (Member M1)

> Owns: Singleton + Iterator + Observer + Abstract Factory + Strategy
> Branch: `m1-foundation`
> Reference spec: `prompts/01_FOUNDATION_CORE.md` and `prompt-parts/01_FOUNDATION_CORE/*`

---

## Setup (5 min, one-time)

```powershell
cd C:\Users\Mohamed\smarthome
git checkout main
git pull
git checkout -b m1-foundation
```

All work goes on the `m1-foundation` branch. Never commit directly to `main`.

---

## Task 1 — Observer interfaces (no dependencies, do first)

**Files to create:**
- `src/main/java/com/smarthome/observer/Observer.java`
- `src/main/java/com/smarthome/observer/Observable.java`

**Required signatures:**
```java
// Observer.java
public interface Observer {
    void update(Device d, String event);
}

// Observable.java
public interface Observable {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers(String event);
}
```

**Locked contract:** `update(Device d, String event)` — other layers depend on it. Do not change parameter types or order.

**Commit:** `git commit -m "Observer + Observable interfaces"`

---

## Task 2 — Device hierarchy

**Files to create:**
- `src/main/java/com/smarthome/devices/Device.java` (abstract, implements `Observable`)
- `src/main/java/com/smarthome/devices/Light.java`
- `src/main/java/com/smarthome/devices/Thermostat.java`
- `src/main/java/com/smarthome/devices/Lock.java`
- `src/main/java/com/smarthome/devices/Camera.java`

**`Device` must:**
- Hold an internal `List<Observer>` (private).
- Have `String id` (immutable, set in constructor).
- Implement `attach`, `detach`, `notifyObservers`.
- Have `String name` and per-device state.
- Provide state-change methods that call `notifyObservers("EVENT_NAME")`.

**Event strings to use:**
- `TURNED_ON`, `TURNED_OFF`, `TEMP_CHANGED`, `LOCKED`, `UNLOCKED`

**Commit:** `git commit -m "Device base class + 4 concrete devices (Light, Thermostat, Lock, Camera)"`

---

## Task 3 — Strategy (AutomationMode + 3 modes)

**Files to create:**
- `src/main/java/com/smarthome/strategy/AutomationMode.java`
- `src/main/java/com/smarthome/strategy/EcoMode.java`
- `src/main/java/com/smarthome/strategy/SleepMode.java`
- `src/main/java/com/smarthome/strategy/AwayMode.java`

**Required signatures:**
```java
public interface AutomationMode {
    String name();
    void apply(SmartHomeHub hub);
}
```

**Behavior per mode (concrete examples — feel free to adjust):**
- `EcoMode.apply(hub)` → set thermostats to 24°C, dim lights
- `SleepMode.apply(hub)` → turn off all lights except in bedrooms, lock doors
- `AwayMode.apply(hub)` → lock all doors, turn off lights, arm cameras

**Commit:** `git commit -m "Strategy: AutomationMode + Eco/Sleep/Away modes"`

---

## Task 4 — Room (Iterator pattern)

**Files to create:**
- `src/main/java/com/smarthome/core/Room.java`

**Required signatures:**
```java
public class Room {
    public Room(String roomId, String name) { ... }
    public String getRoomId();
    public String getName();
    public void addDevice(Device device);
    public void removeDevice(String deviceId);
    public Device getDevice(String deviceId);
    public Enumeration<Device> devices();   // MUST be Enumeration, not Iterator
}
```

**Internal storage:** use a private `Map<String, Device>` for O(1) ID lookup. Convert to `Enumeration` via `Collections.enumeration(map.values())`.

**Commit:** `git commit -m "Room with Enumeration<Device> iterator"`

---

## Task 5 — SmartHomeHub (Singleton)

**Files to create:**
- `src/main/java/com/smarthome/core/SmartHomeHub.java`

**Required signatures:**
```java
public class SmartHomeHub {
    private SmartHomeHub() { ... }                       // private constructor
    public static SmartHomeHub getInstance();            // thread-safe
    public void addRoom(Room room);
    public Room getRoom(String roomId);
    public Collection<Room> getRooms();
    public void setAutomationMode(AutomationMode mode);
    public AutomationMode getAutomationMode();
}
```

**Thread-safety pattern (recommended) — Bill Pugh holder idiom:**

```java
public class SmartHomeHub {
    private SmartHomeHub() {}
    private static class Holder {
        private static final SmartHomeHub INSTANCE = new SmartHomeHub();
    }
    public static SmartHomeHub getInstance() { return Holder.INSTANCE; }
    // ... rest
}
```

**Why Bill Pugh holder:** the JVM guarantees the inner class loads only when `getInstance()` is first called, and class loading is itself thread-safe. No `synchronized`, no `volatile`, no double-checked locking.

**Commit:** `git commit -m "SmartHomeHub thread-safe singleton (Bill Pugh holder)"`

---

## Task 6 — Abstract Factory (4 files)

**Files to create:**
- `src/main/java/com/smarthome/factory/DeviceFactory.java`
- `src/main/java/com/smarthome/factory/LightFactory.java`
- `src/main/java/com/smarthome/factory/ClimateFactory.java`
- `src/main/java/com/smarthome/factory/SecurityFactory.java`

**Required signatures (on the abstract):**
```java
public abstract class DeviceFactory {
    public abstract Device createLight(String name);
    public abstract Device createThermostat(String name);
    public abstract Device createDoorLock(String name);
    public abstract Device createCamera(String name);

    protected String newId() { return java.util.UUID.randomUUID().toString(); }
}
```

**Concrete factories:**
- `LightFactory` → only `createLight` returns a real device; the others throw `UnsupportedOperationException("LightFactory does not create thermostats")`.
- `ClimateFactory` → only `createThermostat` works.
- `SecurityFactory` → `createDoorLock` and `createCamera` work.

**Commit:** `git commit -m "Abstract Factory: DeviceFactory + Light/Climate/Security factories"`

---

## Task 7 — Smoke test (the "done" gate)

**File to create:**
- `src/test/java/com/smarthome/FoundationSmokeTest.java`

**Must verify all 5 acceptance checks:**

```java
package com.smarthome;

import com.smarthome.core.*;
import com.smarthome.devices.*;
import com.smarthome.factory.*;
import com.smarthome.strategy.*;
import org.junit.jupiter.api.Test;
import java.util.Enumeration;
import static org.junit.jupiter.api.Assertions.*;

class FoundationSmokeTest {

    @Test void singletonReturnsSameInstance() {
        assertSame(SmartHomeHub.getInstance(), SmartHomeHub.getInstance());
    }

    @Test void factoryCreatesDeviceWithUuid() {
        Device light = new LightFactory().createLight("Kitchen Light");
        assertNotNull(light.getId());
        assertFalse(light.getId().isEmpty());
    }

    @Test void roomDevicesReturnsEnumeration() {
        Room r = new Room("r1", "Kitchen");
        r.addDevice(new LightFactory().createLight("L1"));
        r.addDevice(new LightFactory().createLight("L2"));
        Enumeration<Device> e = r.devices();
        int count = 0;
        while (e.hasMoreElements()) { e.nextElement(); count++; }
        assertEquals(2, count);
    }

    @Test void observerReceivesUpdateOnDeviceChange() {
        Light light = (Light) new LightFactory().createLight("L1");
        StringBuilder captured = new StringBuilder();
        light.attach((d, event) -> captured.append(event));
        light.turnOn();
        assertEquals("TURNED_ON", captured.toString());
    }

    @Test void strategyModeSwitchChangesBehavior() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        hub.setAutomationMode(new EcoMode());
        assertEquals("Eco", hub.getAutomationMode().name());
        hub.setAutomationMode(new SleepMode());
        assertEquals("Sleep", hub.getAutomationMode().name());
    }
}
```

**Run it:**
```powershell
.\mvnw.cmd test
```

Must show `Tests run: 5, Failures: 0, Errors: 0`.

**Commit:** `git commit -m "Foundation smoke test: 5 acceptance checks all green"`

---

## Task 8 — Push and open PR

```powershell
git push -u origin m1-foundation
```

Then on GitHub:
1. Open https://github.com/ahmefarouk1234d/smarthome
2. Yellow banner appears: **"Compare & pull request"** → click.
3. Title: `Foundation: Singleton + Iterator + Observer + Factory + Strategy`
4. Body: paste the smoke test output (`Tests run: 5, Failures: 0`).
5. Tag the reviewer (Ahme).

---

## Summary

| # | File(s) | Pattern | Lines (rough) |
|---|---|---|---|
| 1 | `observer/Observer.java`, `Observable.java` | Observer (interfaces) | ~10 |
| 2 | `devices/Device.java` + 4 concrete | Observer (impl) | ~120 |
| 3 | `strategy/*.java` (4 files) | Strategy | ~60 |
| 4 | `core/Room.java` | Iterator | ~40 |
| 5 | `core/SmartHomeHub.java` | Singleton | ~40 |
| 6 | `factory/*.java` (4 files) | Abstract Factory | ~70 |
| 7 | `test/.../FoundationSmokeTest.java` | (verification) | ~50 |

**Total: ~400 lines of Java, 14 files, 5 patterns implemented.**

---

## Hard contracts — DO NOT change these

Other layers compile against these — change them and other people's code breaks.

- **Observer:** `void update(Device d, String event)`
- **Observable:** `attach(Observer o)`, `detach(Observer o)`, `notifyObservers(String event)`
- **Iterator on Room:** `Enumeration<Device> devices()` — must be `java.util.Enumeration`, not `Iterator`
- **Singleton:** `SmartHomeHub.getInstance()` — thread-safe
- **Device ID:** UUID string (generated in factory)
- **Package root:** `com.smarthome` — do not rename

---

## Help / blockers

- Questions about a specific task? Ask in the team chat.
- Spec ambiguous? Read `prompt-parts/01_FOUNDATION_CORE/*.md` for the precise contract.
- Stuck more than 45 min on one task? Stop and ask — likely overengineering.
