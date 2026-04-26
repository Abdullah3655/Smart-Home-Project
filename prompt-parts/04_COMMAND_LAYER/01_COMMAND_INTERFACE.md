# Purpose
Define stable contract for all command objects.

# Exact files to create/update
- `src/main/java/com/smarthome/command/DeviceCommand.java`

# Exact classes/interfaces
- `DeviceCommand`

# Exact method signatures
- `void execute()`
- `void undo()`
- `String commandId()`
- `String actionName()`

# Logic rules (must implement)
- `commandId` must be UUID string.
- Command objects should be immutable after construction.

# Dependencies from other parts
- Implemented by concrete command classes.

# Out of scope
- DAO insert logic.
- Controller event parsing.

# Acceptance checklist
- Every concrete command implements all interface methods.
- `actionName` is stable for logs/report.

