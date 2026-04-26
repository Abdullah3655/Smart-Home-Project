# Purpose
Define command execution model, history, and undo behavior for all mutating actions.

# Exact files to create/update
- `01_COMMAND_INTERFACE.md`
- `02_CONCRETE_COMMANDS.md`
- `03_COMMAND_INVOKER.md`
- `04_UNDO_RULES.md`

# Exact classes/interfaces
- `command.DeviceCommand`
- concrete command classes
- `command.CommandInvoker`

# Exact method signatures
- Enforced in subpart files.

# Logic rules (must implement)
- All mutating actions are commands.
- Invoker owns execution order and undo stack.

# Dependencies from other parts
- Depends on foundation device/hub APIs.
- Used by facade and DAO log integration.

# Out of scope
- FXML UI behavior.
- Table schema creation.

# Acceptance checklist
- Execute and undo lifecycle works for core commands.
- Command history is available for UI/report.

