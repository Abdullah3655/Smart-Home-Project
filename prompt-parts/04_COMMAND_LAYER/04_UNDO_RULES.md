# Purpose
Define strict undo policy and logging behavior.

# Exact files to create/update
- `src/main/java/com/smarthome/command/CommandInvoker.java`
- Concrete command files under `src/main/java/com/smarthome/command/`

# Exact classes/interfaces
- `CommandInvoker`
- `DeviceCommand` implementations

# Exact method signatures
- `undo()` in each command
- `undoLast()` in invoker

# Logic rules (must implement)
- Only successfully executed commands can be undone.
- Non-undoable command behavior must be explicit and documented.
- Failed execute does not enter undo stack.
- Undo action should be loggable to `commands_log` with result metadata.

# Dependencies from other parts
- Depends on command interface and logging contract.

# Out of scope
- Controller-level undo UX.

# Acceptance checklist
- Undo stack contains only successful commands.
- Undo log entry is created when undo is invoked.
- Non-undoable command is handled predictably.

