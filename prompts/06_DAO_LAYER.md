# Prompt 06 - DAO Layer

Paste this into Claude/Cursor:

Implement DAO classes and keep SQL isolated from domain and UI.

## Required package

- `com.smarthome.persistence.dao`

## Implement DAOs

- `UserDAO`
- `RoomDAO`
- `DeviceDAO`
- `DeviceEventDAO`
- `ScheduleDAO`
- `CommandsLogDAO`

## Behavior requirements

- CRUD for core entities as needed by UI and facade.
- Insert observer events into `device_events`.
- Insert command runs into `commands_log`.

## Constraints

- DAO methods should be predictable and consistent.
- Domain objects must not contain SQL.

## Acceptance checks

- End-to-end read/write works for each major table.
- Event and command logs persist correctly.

