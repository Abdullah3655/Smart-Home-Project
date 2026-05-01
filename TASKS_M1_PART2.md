# Foundation Polish — Tasks M1 Part 2 (Mahmoud)

> Owns: Abstract Factory refactor + Strategy bodies
> Branch: `m1-foundation-polish`
> Reference: `prompts/01_FOUNDATION_CORE.md`, `prompt-parts/01_FOUNDATION_CORE/04_ABSTRACT_FACTORY.md`, `05_STRATEGY.md`

Two known gaps remain in the foundation. Both small (~1 hour total).

---

## Task A — New/Old device families with Abstract Factory

**Why:** you need a real Abstract Factory where each family produces the same product set in different ways. This means both factories can create Light/Thermostat/Lock/Camera, but each returns its own variant implementation (`New...` vs `Old...`).

### What to change

**Replace** `src/main/java/com/smarthome/factory/DeviceFactory.java` with an abstract factory that declares all 4 factory methods:

```java
public abstract class DeviceFactory {
    public abstract Device createLight(String name);
    public abstract Device createThermostat(String name);
    public abstract Device createDoorLock(String name);
    public abstract Device createCamera(String name);
}
```

**Add concrete family factories:**
- `src/main/java/com/smarthome/factory/NewDeviceFactory.java`
- `src/main/java/com/smarthome/factory/OldDeviceFactory.java`

Each factory must implement all 4 methods:
- `NewDeviceFactory` returns `NewLight`, `NewThermostat`, `NewLock`, `NewCamera`
- `OldDeviceFactory` returns `OldLight`, `OldThermostat`, `OldLock`, `OldCamera`

**Add new concrete device variants:**
- `src/main/java/com/smarthome/devices/newgen/NewLight.java`
- `src/main/java/com/smarthome/devices/newgen/NewThermostat.java`
- `src/main/java/com/smarthome/devices/newgen/NewLock.java`
- `src/main/java/com/smarthome/devices/newgen/NewCamera.java`
- `src/main/java/com/smarthome/devices/legacy/OldLight.java`
- `src/main/java/com/smarthome/devices/legacy/OldThermostat.java`
- `src/main/java/com/smarthome/devices/legacy/OldLock.java`
- `src/main/java/com/smarthome/devices/legacy/OldCamera.java`

### New/Old behavior contract (minimum)

For each product type, New and Old must differ in behavior:
- **Light:** different brightness policy (e.g., new = smooth/clamped, old = stepped or stricter max).
- **Thermostat:** different defaults/range handling.
- **Lock:** different default lock state or lock/unlock policy.
- **Camera:** different default power/arm behavior.

Both variants must still be usable by existing layers:
- extend core classes (`Light`, `Thermostat`, `Lock`, `Camera`) so Strategy `instanceof` checks still work.

### Update the smoke test

Add tests like:

```java
@Test void newFactoryCreatesNewFamilyTypes() {
    DeviceFactory f = new NewDeviceFactory();
    assertTrue(f.createLight("L") instanceof NewLight);
    assertTrue(f.createThermostat("T") instanceof NewThermostat);
    assertTrue(f.createDoorLock("D") instanceof NewLock);
    assertTrue(f.createCamera("C") instanceof NewCamera);
}

@Test void oldFactoryCreatesOldFamilyTypes() {
    DeviceFactory f = new OldDeviceFactory();
    assertTrue(f.createLight("L") instanceof OldLight);
    assertTrue(f.createThermostat("T") instanceof OldThermostat);
    assertTrue(f.createDoorLock("D") instanceof OldLock);
    assertTrue(f.createCamera("C") instanceof OldCamera);
}
```

Also add one behavior-difference test (New vs Old for same device type).

### Acceptance

- All tests pass.
- `DeviceFactory` is abstract with 4 factory methods.
- `NewDeviceFactory` and `OldDeviceFactory` both implement all 4 methods.
- New/Old variants exist for all 4 device products and are behaviorally different.

---

## Task B — Fill in the Strategy `apply()` bodies

**Why:** all three modes' `apply()` methods are currently empty. Clicking "Eco" in the UI later will do nothing visible — patterns work structurally but there's no behavior to demo.

### Add state-changing methods to device subclasses

**`Light.java`** — add:
```java
private int brightness = 100;

public int getBrightness() { return brightness; }

public void setBrightness(int value) {
    this.brightness = Math.max(0, Math.min(100, value));
    notifyObservers("BRIGHTNESS_CHANGED");
}
```

**`Thermostat.java`** — add:
```java
private double temperatureC = 22.0;

public double getTemperature() { return temperatureC; }

public void setTemperature(double value) {
    this.temperatureC = value;
    notifyObservers(EVENT_TEMP_CHANGED);
}
```

**`Lock.java`** — add:
```java
private boolean locked = false;

public boolean isLocked() { return locked; }

public void lock()   { if (!locked) { locked = true;  notifyObservers(EVENT_LOCKED); } }
public void unlock() { if (locked)  { locked = false; notifyObservers(EVENT_UNLOCKED); } }
```

### Fill in each strategy

**`EcoMode.apply()`:**
```java
@Override
public void apply(SmartHomeHub hub) {
    Objects.requireNonNull(hub, "hub must not be null");
    for (Room room : hub.getRooms()) {
        Enumeration<Device> devices = room.devices();
        while (devices.hasMoreElements()) {
            Device d = devices.nextElement();
            if (d instanceof Thermostat t)        t.setTemperature(24.0);
            if (d instanceof Light l && l.isPoweredOn()) l.setBrightness(50);
        }
    }
}
```

**`SleepMode.apply()`:**
```java
@Override
public void apply(SmartHomeHub hub) {
    Objects.requireNonNull(hub, "hub must not be null");
    for (Room room : hub.getRooms()) {
        Enumeration<Device> devices = room.devices();
        while (devices.hasMoreElements()) {
            Device d = devices.nextElement();
            if (d instanceof Light l)      l.turnOff();
            if (d instanceof Lock lock)    lock.lock();
            if (d instanceof Thermostat t) t.setTemperature(20.0);
        }
    }
}
```

**`AwayMode.apply()`:**
```java
@Override
public void apply(SmartHomeHub hub) {
    Objects.requireNonNull(hub, "hub must not be null");
    for (Room room : hub.getRooms()) {
        Enumeration<Device> devices = room.devices();
        while (devices.hasMoreElements()) {
            Device d = devices.nextElement();
            if (d instanceof Light l)      l.turnOff();
            if (d instanceof Lock lock)    lock.lock();
            if (d instanceof Camera c)     c.turnOn();
            if (d instanceof Thermostat t) t.setTemperature(15.0);
        }
    }
}
```

### Add a behavior test

```java
@Test void ecoModeDimsLightsAndSetsModerateTemp() {
    SmartHomeHub hub = SmartHomeHub.getInstance();
    Room kitchen = new Room("k1", "Kitchen");
    Light light = (Light) new ComfortFactory().createLight("Kitchen Light");
    Thermostat thermo = (Thermostat) new ComfortFactory().createThermostat("Kitchen Thermo");
    light.turnOn();
    light.setBrightness(100);
    kitchen.addDevice(light);
    kitchen.addDevice(thermo);
    hub.addRoom(kitchen);

    new EcoMode().apply(hub);

    assertEquals(50, light.getBrightness());
    assertEquals(24.0, thermo.getTemperature(), 0.01);
}
```

### Acceptance

- All 3 modes produce visible state changes.
- All Lock/Thermostat/Light state-change methods fire observer events.
- `./mvnw test` passes ≥7 tests.

---

## Submit

```powershell
git checkout -b m1-foundation-polish
# ... make changes ...
./mvnw test
git push -u origin m1-foundation-polish
```

PR title: `Foundation polish: Abstract Factory family grouping + Strategy bodies`
