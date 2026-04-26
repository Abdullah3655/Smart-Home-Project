# Purpose
Centralize command execution, history tracking, undo, and optional logging hook.

# Exact files to create/update
- `src/main/java/com/smarthome/command/CommandInvoker.java`

# Exact classes/interfaces
- `CommandInvoker`

# Exact method signatures
- `void execute(DeviceCommand command)`
- `void undoLast()`
- `List<DeviceCommand> history()`

# Logic rules (must implement)
- Execute command then add to history/undo stack on success.
- Failed command execution is not pushed to undo stack.
- `undoLast` no-ops safely when stack is empty.
- Support integration callback to `CommandsLogDAO`.

# Dependencies from other parts
- Used by facade for all mutating actions.
- Uses command interface and concrete command types.

# Out of scope
- FXML event binding.
- SQL schema definitions.

# Acceptance checklist
- Running 3 commands yields history size 3.
- Undo last command reverts expected state.
- Empty undo does not crash.

