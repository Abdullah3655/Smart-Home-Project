# Foundation Polish — Tasks M1 Part 2 (Mahmoud)

> Owns: Abstract Factory refactor + Strategy bodies
> Branch: `m1-foundation-polish`
> Reference: `prompts/01_FOUNDATION_CORE.md`, `prompt-parts/01_FOUNDATION_CORE/04_ABSTRACT_FACTORY.md`, `05_STRATEGY.md`

Two known gaps remain in the foundation. Both small (~1 hour total).

---

## Task A — Promote `DeviceFactory` to a true Abstract Factory

**Why:** the assignment brief says *"Abstract Factory with Factory Methods"* — we currently have only Factory Methods. Without family-grouped factories, we lose marks on the Abstract Factory half of that requirement.

### What to change

**Replace** `src/main/java/com/smarthome/factory/DeviceFactory.java`:

```java
package com.smarthome.factory;

import com.smarthome.devices.Device;
import java.util.UUID;

/**
 * ABSTRACT FACTORY PATTERN
 * Declares the family of related smart-home device products.
 * Each create... method is a FACTORY METHOD that subclasses implement
 * for the device families they own.
 */
public abstract class DeviceFactory {
    public abstract Device createLight(String name);
    public abstract Device createThermostat(String name);
    public abstract Device createDoorLock(String name);
    public abstract Device createCamera(String name);

    protected String newId() { return UUID.randomUUID().toString(); }
}
```

**Add** `src/main/java/com/smarthome/factory/ComfortFactory.java`:

```java
package com.smarthome.factory;

import com.smarthome.devices.*;

/**
 * Concrete Abstract Factory for the COMFORT family
 * (lights + thermostats: ambient living conditions).
 */
public class ComfortFactory extends DeviceFactory {
    @Override public Device createLight(String n)      { return new Light(newId(), n); }
    @Override public Device createThermostat(String n) { return new Thermostat(newId(), n); }
    @Override public Device createDoorLock(String n) {
        throw new UnsupportedOperationException("ComfortFactory does not produce locks");
    }
    @Override public Device createCamera(String n) {
        throw new UnsupportedOperationException("ComfortFactory does not produce cameras");
    }
}
```

**Add** `src/main/java/com/smarthome/factory/SecurityFactory.java`:

```java
package com.smarthome.factory;

import com.smarthome.devices.*;

/**
 * Concrete Abstract Factory for the SECURITY family
 * (locks + cameras: home protection).
 */
public class SecurityFactory extends DeviceFactory {
    @Override public Device createLight(String n) {
        throw new UnsupportedOperationException("SecurityFactory does not produce lights");
    }
    @Override public Device createThermostat(String n) {
        throw new UnsupportedOperationException("SecurityFactory does not produce thermostats");
    }
    @Override public Device createDoorLock(String n) { return new Lock(newId(), n); }
    @Override public Device createCamera(String n)   { return new Camera(newId(), n); }
}
```

**Existing `LightFactory`/`ThermostatFactory`/`LockFactory`/`CameraFactory`:** keep as helpers OR delete. Either is fine; deleting is cleaner.

### Update the smoke test

```java
@Test void comfortFactoryCreatesItsFamily() {
    ComfortFactory f = new ComfortFactory();
    assertNotNull(f.createLight("L"));
    assertNotNull(f.createThermostat("T"));
    assertThrows(UnsupportedOperationException.class, () -> f.createDoorLock("X"));
}

@Test void securityFactoryCreatesItsFamily() {
    SecurityFactory f = new SecurityFactory();
    assertNotNull(f.createDoorLock("D"));
    assertNotNull(f.createCamera("C"));
    assertThrows(UnsupportedOperationException.class, () -> f.createLight("X"));
}
```

### Acceptance

- All tests pass.
- `DeviceFactory` is an abstract class with **4 factory methods**.
- 2 concrete family factories exist.

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
