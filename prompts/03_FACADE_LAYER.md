# Prompt 03 - Facade Layer (`facade.HomeController`)

Paste this into Claude/Cursor:

Implement the Facade pattern to be the only entry point from JavaFX controllers.

## Required package

- `com.smarthome.facade`

## Implement

- `HomeController` class exposing clear methods such as:
  - `turnOnDevice(String deviceId)`
  - `turnOffDevice(String deviceId)`
  - `setAutomationMode(String modeName)`
  - `getEventHistory()`
  - `getCommandHistory()`
  - `createSchedule(...)`

## Wiring

- Facade delegates to `CommandInvoker`, hub, and DAOs.
- JavaFX controllers depend only on facade interface/class.

## Constraints

- Keep facade thin (orchestration only).
- Do not move domain logic into facade.

## Acceptance checks

- All UI actions route through facade.
- No controller imports from DAO or domain internals.

