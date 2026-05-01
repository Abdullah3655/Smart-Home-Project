# Decorator Pattern — Optional 9th Pattern

> Owner: TBD (defaults to M1 after foundation polish lands)
> Branch: `feature-decorator`
> Pattern owned: **Decorator** (#9)

---

## ⚠️ Read before starting — should we even include this?

**The minimum requirement is 5 patterns; you'll have 8 without Decorator** (Singleton, Iterator, Observer, Abstract Factory, Strategy, DAO, Command, Facade).

Decorator is only worth adding if it has a **real, justifiable use case** — graders penalize "pattern stuffing" (using a pattern just to claim it). Decide first: is there a feature in our system where *wrapping a Device to add cross-cutting behavior* is genuinely useful?

### Real candidates for decorating a `Device`

1. **`LoggingDevice`** — wraps any device, prints (or DB-logs) every state change with timestamp. Useful for the audit-trail requirement.
2. **`EnergyTrackedDevice`** — wraps any device, accumulates time-on duration for energy reports.
3. **`AccessControlledDevice`** — wraps any device, requires a valid `User` PIN before `turnOn()` succeeds. Ties into M4's security work.

If at least one of these maps to a real user-facing feature you'd actually demo, build Decorator. If not, **drop it and stay at 8 patterns** — better than weak Decorator.

---

## Setup (if you decide to proceed)

```powershell
git checkout main
git pull
git checkout -b feature-decorator
```

**Depends on:**
- Foundation merged (`Device`, `Observer`, `Observable`).
- Optionally, `CommandsLogDAO` if you want logging to persist (M2).

---

## Task A — Decorator base class

**Create** `src/main/java/com/smarthome/devices/decorator/DeviceDecorator.java`:

```java
package com.smarthome.devices.decorator;

import com.smarthome.devices.Device;
import com.smarthome.observer.Observer;

/**
 * DECORATOR PATTERN
 * Base class for adding cross-cutting concerns to any Device
 * (logging, energy tracking, access control) without subclass explosion.
 *
 * Wraps a delegate Device and forwards all calls. Subclasses override
 * the methods they want to extend.
 */
public abstract class DeviceDecorator extends Device {
    protected final Device delegate;

    protected DeviceDecorator(Device delegate) {
        super(delegate.getId(), delegate.getName());
        this.delegate = delegate;
    }

    @Override public void turnOn()  { delegate.turnOn(); }
    @Override public void turnOff() { delegate.turnOff(); }
    @Override public boolean isPoweredOn() { return delegate.isPoweredOn(); }

    @Override public void attach(Observer o) { delegate.attach(o); }
    @Override public void detach(Observer o) { delegate.detach(o); }
    @Override public void notifyObservers(String event) { delegate.notifyObservers(event); }
}
```

---

## Task B — Concrete decorators (build whichever fit your features)

### Option 1 — `LoggingDeviceDecorator`

```java
package com.smarthome.devices.decorator;

import com.smarthome.devices.Device;
import java.time.Instant;

public class LoggingDeviceDecorator extends DeviceDecorator {
    public LoggingDeviceDecorator(Device delegate) { super(delegate); }

    @Override public void turnOn() {
        log("turnOn");
        super.turnOn();
    }

    @Override public void turnOff() {
        log("turnOff");
        super.turnOff();
    }

    private void log(String action) {
        System.out.printf("[%s] %s -> %s%n",
            Instant.now(), delegate.getName(), action);
    }
}
```

### Option 2 — `EnergyTrackedDecorator`

```java
public class EnergyTrackedDecorator extends DeviceDecorator {
    private long onSinceMillis;
    private long totalOnMillis;

    public EnergyTrackedDecorator(Device delegate) { super(delegate); }

    @Override public void turnOn() {
        super.turnOn();
        onSinceMillis = System.currentTimeMillis();
    }

    @Override public void turnOff() {
        if (delegate.isPoweredOn()) {
            totalOnMillis += System.currentTimeMillis() - onSinceMillis;
        }
        super.turnOff();
    }

    public long getTotalOnMillis() {
        long live = delegate.isPoweredOn()
            ? System.currentTimeMillis() - onSinceMillis : 0;
        return totalOnMillis + live;
    }
}
```

### Option 3 — `AccessControlledDecorator` (needs M4 SecurityContext)

```java
public class AccessControlledDecorator extends DeviceDecorator {
    private final SecurityContext security;

    public AccessControlledDecorator(Device delegate, SecurityContext security) {
        super(delegate);
        this.security = security;
    }

    @Override public void turnOn() {
        if (!security.isAuthenticated()) {
            throw new SecurityException("Not authenticated");
        }
        super.turnOn();
    }
}
```

---

## Task C — Demonstrate composition

The whole point of Decorator is **stackability**. Show this in a test:

```java
@Test void decoratorsStack() {
    Device base = new ComfortFactory().createLight("Living Room");
    Device decorated = new LoggingDeviceDecorator(
                          new EnergyTrackedDecorator(base));

    decorated.turnOn();
    Thread.sleep(50);
    decorated.turnOff();

    long onMillis = ((EnergyTrackedDecorator) ((LoggingDeviceDecorator) decorated).delegate)
                       .getTotalOnMillis();
    assertTrue(onMillis >= 50);
}
```

---

## Task D — Hard contracts

- **Decorators must be transparent** — code that holds a `Device` reference shouldn't know if it's a base device or 5 decorators deep. The `attach`, `detach`, `notifyObservers`, and `isPoweredOn` calls must all forward correctly.
- **Constructor takes the delegate** — never wrap in a static factory. `new LoggingDeviceDecorator(rawLight)` is the only valid creation path.
- **Decorators carry the same `id` and `name`** as their delegate — so logs and DAOs treat them as the same device.

---

## Submit

```powershell
./mvnw test
git push -u origin feature-decorator
```

PR title: `Decorator pattern: device wrapping for [logging|energy tracking|access control]`

---

## When NOT to merge

If after writing the decorator you can't honestly write a 1-paragraph justification for why it's in the project (i.e., what *real* feature it delivers), **don't merge.** Submit-day code with one weak pattern is worse than submit-day code with one fewer pattern.
