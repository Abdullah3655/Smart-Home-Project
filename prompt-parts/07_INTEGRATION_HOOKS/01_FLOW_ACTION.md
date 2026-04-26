# Purpose
Wire the mandatory runtime path for any mutating user action.

# Exact files to create/update
- JavaFX controllers
- `facade/HomeController`
- `command/CommandInvoker` + concrete commands
- domain `Device`/`SmartHomeHub`
- observer implementation + DAO logging hooks

# Exact classes/interfaces
- `DashboardController` (or equivalent)
- `HomeController`
- `CommandInvoker`
- `DeviceCommand` implementations
- `Observer`/`Observable`
- `DeviceEventDAO`, `CommandsLogDAO`

# Exact method signatures
- Must use signatures already fixed in subpart contracts.

# Logic rules (must implement)
- Enforce action flow exactly:
  `GUI -> Facade -> CommandInvoker -> Command -> Domain -> Observer -> DAO -> SQLite`.
- Observer event log and command log both persist for mutating actions.

# Dependencies from other parts
- Requires completed facade, command, observer, and logging DAOs.

# Out of scope
- Direct controller-to-DAO updates.

# Acceptance checklist
- Trigger `turnOnDevice` from UI.
- Device state changes.
- Observer callback fires.
- Event and command records appear in DB.

