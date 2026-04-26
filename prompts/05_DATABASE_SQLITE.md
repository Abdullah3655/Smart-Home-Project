# Prompt 05 - SQLite Foundation (schema + connection singleton)

Paste this into Claude/Cursor:

Set up SQLite persistence baseline for the smart-home app.

## Required output

- SQL schema file under `resources` or `db` folder.
- `com.smarthome.persistence.Database` singleton connection manager.

## Required tables

- `users`
- `rooms`
- `devices`
- `device_events`
- `schedules`
- `commands_log`

## Required command log fields

`command_id`, `device_id`, `action`, `params_json`, `result`, `timestamp`

## Constraints

- Use sqlite-jdbc driver.
- Keep DB singleton reusable and safe for app lifecycle.

## Acceptance checks

- DB file initializes successfully.
- Tables are created if missing.

