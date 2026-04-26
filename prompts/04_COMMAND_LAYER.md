# Prompt 04 - Command Layer (execute + undo + history)

Paste this into Claude/Cursor:

Implement command pattern for smart-home actions.

## Required package

- `com.smarthome.command`

## Implement

- `DeviceCommand` interface:
  - `execute()`
  - `undo()`
- Concrete commands (minimum):
  - `TurnOnCommand`
  - `TurnOffCommand`
  - `SetTemperatureCommand`
  - `LockCommand`
  - `UnlockCommand`
  - `SetAutomationModeCommand`
- `CommandInvoker`:
  - executes commands
  - keeps undo stack/history
  - exposes undo operation

## Integration

- Facade uses invoker for mutating operations.
- Commands operate on domain objects, not UI.

## Acceptance checks

- Execute + undo works for key commands.
- Invoker history reflects run order.

