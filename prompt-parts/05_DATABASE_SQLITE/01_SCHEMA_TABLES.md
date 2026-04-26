# Purpose
Define exact SQLite table structure for core domain, history, and scheduling.

# Exact files to create/update
- `src/main/resources/db/schema.sql` (or project-equivalent SQL path)

# Exact classes/interfaces
- SQL DDL only.

# Exact method signatures
- Not applicable.

# Logic rules (must implement)
- Create tables:
  - `users`
  - `rooms`
  - `devices`
  - `device_events`
  - `schedules`
  - `commands_log`
- Include command log fields exactly:
  - `command_id`, `device_id`, `action`, `params_json`, `result`, `timestamp`
- Add indexes on timestamp columns for history queries.
- Use this exact schema baseline from `SMARTHOME_COMPLETE_PLAN.md`:

```sql
CREATE TABLE users (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    name       TEXT NOT NULL UNIQUE,
    pin_hash   TEXT NOT NULL,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE rooms (
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE devices (
    id       TEXT PRIMARY KEY,
    name     TEXT NOT NULL,
    type     TEXT NOT NULL,
    room_id  INTEGER NOT NULL,
    config   TEXT,
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

CREATE TABLE device_events (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id  TEXT NOT NULL,
    event_type TEXT NOT NULL,
    old_state  TEXT,
    new_state  TEXT,
    timestamp  TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES devices(id)
);
CREATE INDEX idx_events_device ON device_events(device_id);
CREATE INDEX idx_events_time   ON device_events(timestamp);

CREATE TABLE schedules (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    name          TEXT NOT NULL,
    cron_expr     TEXT NOT NULL,
    mode_to_apply TEXT NOT NULL,
    enabled       INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE commands_log (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    command_id  TEXT NOT NULL,
    device_id   TEXT,
    action      TEXT NOT NULL,
    params_json TEXT,
    result      TEXT,
    timestamp   TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_cmdlog_time ON commands_log(timestamp);
```

# Dependencies from other parts
- Must align with DAO model fields and facade query needs.

# Out of scope
- DAO Java methods.
- UI table rendering.

# Acceptance checklist
- Running schema creates all required tables.
- `commands_log` has exact required columns.
- Timestamp-indexed queries execute without missing-column errors.

