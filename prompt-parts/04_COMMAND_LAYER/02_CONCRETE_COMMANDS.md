# Purpose
Implement concrete command classes for all core mutating actions.

# Exact files to create/update
- `TurnOnCommand.java`
- `TurnOffCommand.java`
- `SetTemperatureCommand.java`
- `LockCommand.java`
- `UnlockCommand.java`
- `SetAutomationModeCommand.java`

# Exact classes/interfaces
- Each class implements `DeviceCommand`.

# Exact method signatures
- `execute()`, `undo()`, `commandId()`, `actionName()`

# Logic rules (must implement)
- Store target references and parameters at construction time.
- `execute` performs action; `undo` reverts where supported.
- `commandId()` returns per-command UUID.
- `actionName()` must match log-friendly action token.

# Dependencies from other parts
- Depends on `SmartHomeHub`, devices, and strategy APIs.

# Out of scope
- FXML/controller details.
- SQL statement construction.

# Acceptance checklist
- Each command executes correct operation.
- Undo returns system to prior state for undoable commands.
- Commands compile against shared interface.

