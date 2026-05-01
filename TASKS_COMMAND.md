# Command Pattern + Undo

> Owner: TBD (assignable to M1 or M3 once foundation polish lands)
> Branch: `feature-command-layer`
> Reference: `prompts/04_COMMAND_LAYER.md`, `prompt-parts/04_COMMAND_LAYER/*`
> Pattern owned: **Command** (#7) — required for the **undo** feature

---

## Why this pattern

Each user action becomes an **object** with `execute()` and `undo()` methods. The `CommandInvoker` runs commands and keeps a history stack so the user can undo recent actions.

This satisfies:
- The "undo" feature listed in user functionalities.
- The "prevent invalid/unsafe operations" constraint (validation lives inside `execute()`).
- The audit trail requirement (each `execute()` writes to `commands_log`).

---

## Setup

```powershell
git checkout main
git pull
git checkout -b feature-command-layer
```

**Depends on:**
- Foundation merged (`Device`, `SmartHomeHub`, devices subclasses with state-changing methods).
- DAO layer for `CommandsLogDAO` (for audit trail). If DAO not ready, leave the logging hook as a TODO.

---

## Task A — `DeviceCommand` interface

**Create** `src/main/java/com/smarthome/command/DeviceCommand.java`:

```java
package com.smarthome.command;

/**
 * COMMAND PATTERN
 * Encapsulates a smart-home action as an object so it can be queued,
 * logged, and undone.
 */
public interface DeviceCommand {
    void execute();
    void undo();
    String describe();   // human-readable, e.g. "Turn on Kitchen Light"
}
```

---

## Task B — Concrete commands (minimum 6)

Each lives under `src/main/java/com/smarthome/command/`. Pattern is identical for all:

```java
public class TurnOnCommand implements DeviceCommand {
    private final Device device;
    private boolean wasAlreadyOn;

    public TurnOnCommand(Device device) { this.device = device; }

    @Override public void execute() {
        wasAlreadyOn = device.isPoweredOn();
        device.turnOn();
    }

    @Override public void undo() {
        if (!wasAlreadyOn) device.turnOff();
    }

    @Override public String describe() {
        return "Turn on " + device.getName();
    }
}
```

Build the same shape for:

| Command class | Targets | `execute()` | `undo()` |
|---|---|---|---|
| `TurnOnCommand` | `Device` | `device.turnOn()` | `device.turnOff()` if it was off before |
| `TurnOffCommand` | `Device` | `device.turnOff()` | `device.turnOn()` if it was on before |
| `SetTemperatureCommand` | `Thermostat` | record old temp, set new | restore old temp |
| `LockCommand` | `Lock` | record old state, `lock.lock()` | `lock.unlock()` if was unlocked |
| `UnlockCommand` | `Lock` | record old state, `lock.unlock()` | `lock.lock()` if was locked |
| `SetAutomationModeCommand` | `SmartHomeHub` | record old mode, set new mode + apply | restore old mode + apply |

---

## Task C — `CommandInvoker`

**Create** `src/main/java/com/smarthome/command/CommandInvoker.java`:

```java
package com.smarthome.command;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.UUID;

/**
 * COMMAND PATTERN — Invoker
 * Runs commands and maintains an undo stack.
 * Optionally writes each execution to commands_log via CommandsLogDAO.
 */
public class CommandInvoker {
    private final Deque<DeviceCommand> history = new ArrayDeque<>();

    public void execute(DeviceCommand cmd) {
        String commandId = UUID.randomUUID().toString();
        try {
            cmd.execute();
            history.push(cmd);
            // TODO when DAO lands:
            // new CommandsLogDAO().insert(commandId, deviceIdFor(cmd), cmd.describe(), "{}", "OK");
        } catch (Exception e) {
            // new CommandsLogDAO().insert(commandId, ..., "FAIL: " + e.getMessage());
            throw e;
        }
    }

    public boolean canUndo() { return !history.isEmpty(); }

    public DeviceCommand undo() {
        if (history.isEmpty()) return null;
        DeviceCommand last = history.pop();
        last.undo();
        return last;
    }

    public List<DeviceCommand> getHistory() {
        return List.copyOf(history);
    }
}
```

---

## Task D — Hard contracts

- **Every concrete command captures `wasXxx` state before mutating** so `undo()` is reliable.
- **`undo()` is a no-op if state already matches what undo would set** (preserves idempotency).
- **`commands_log` insertion order:** before `execute()` succeeds, log a "PENDING" row OR after success, log "OK". Do not log only failures.
- **`SetAutomationModeCommand.undo()`** must call `oldMode.apply(hub)`, not just `hub.setAutomationMode(oldMode)` — otherwise device states stay changed by the new mode.

---

## Task E — Acceptance tests

`src/test/java/com/smarthome/command/CommandTest.java`:

```java
@Test void turnOnAndUndoRestoresState() {
    Light light = (Light) new ComfortFactory().createLight("L");
    assertFalse(light.isPoweredOn());

    CommandInvoker invoker = new CommandInvoker();
    invoker.execute(new TurnOnCommand(light));
    assertTrue(light.isPoweredOn());

    invoker.undo();
    assertFalse(light.isPoweredOn());
}

@Test void undoOnEmptyHistoryReturnsNull() {
    assertNull(new CommandInvoker().undo());
}

@Test void historyReflectsExecutionOrder() {
    CommandInvoker invoker = new CommandInvoker();
    Light l = (Light) new ComfortFactory().createLight("L");
    invoker.execute(new TurnOnCommand(l));
    invoker.execute(new TurnOffCommand(l));
    assertEquals(2, invoker.getHistory().size());
}

@Test void setTemperatureUndoRestoresOriginal() {
    Thermostat t = (Thermostat) new ComfortFactory().createThermostat("T");
    t.setTemperature(22.0);

    CommandInvoker invoker = new CommandInvoker();
    invoker.execute(new SetTemperatureCommand(t, 28.0));
    assertEquals(28.0, t.getTemperature(), 0.01);

    invoker.undo();
    assertEquals(22.0, t.getTemperature(), 0.01);
}
```

---

## Submit

```powershell
./mvnw test
git push -u origin feature-command-layer
```

PR title: `Command pattern: 6 commands + CommandInvoker with undo + history`

---

## Estimated effort

| Step | Time |
|---|---|
| `DeviceCommand` interface | 5 min |
| 6 concrete commands | 60 min |
| `CommandInvoker` | 30 min |
| Tests | 30 min |
| **Total** | **~2 hours** |
