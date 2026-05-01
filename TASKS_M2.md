# M2 — Database + DAO Layer

> Owns: SQLite persistence, schema, DAO classes
> Branch: `m2-database`
> Reference: `prompts/05_DATABASE_SQLITE.md`, `prompts/06_DAO_LAYER.md`, `prompt-parts/05_DATABASE_SQLITE/*`, `prompt-parts/06_DAO_LAYER/*`
> Pattern owned: **DAO** (#6)

---

## Setup

```powershell
git checkout main
git pull
git checkout -b m2-database
```

You don't need the foundation classes (`Device`, `Room`) to be finished to start — schema + DB connection are independent. DAO classes will reference foundation types when they land.

---

## Task A — SQL schema

**Create** `src/main/resources/db/schema.sql`:

```sql
CREATE TABLE IF NOT EXISTS users (
    user_id      TEXT PRIMARY KEY,
    name         TEXT NOT NULL,
    pin          TEXT NOT NULL,
    role         TEXT NOT NULL DEFAULT 'USER',
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS rooms (
    room_id      TEXT PRIMARY KEY,
    name         TEXT NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS devices (
    device_id    TEXT PRIMARY KEY,
    name         TEXT NOT NULL,
    type         TEXT NOT NULL,
    room_id      TEXT NOT NULL,
    powered_on   INTEGER NOT NULL DEFAULT 0,
    state_json   TEXT,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

CREATE TABLE IF NOT EXISTS device_events (
    event_id     INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id    TEXT NOT NULL,
    event_type   TEXT NOT NULL,
    timestamp    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES devices(device_id)
);

CREATE TABLE IF NOT EXISTS schedules (
    schedule_id  TEXT PRIMARY KEY,
    device_id    TEXT,
    mode_name    TEXT,
    cron_expr    TEXT NOT NULL,
    enabled      INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (device_id) REFERENCES devices(device_id)
);

CREATE TABLE IF NOT EXISTS commands_log (
    command_id   TEXT PRIMARY KEY,
    device_id    TEXT,
    action       TEXT NOT NULL,
    params_json  TEXT,
    result       TEXT NOT NULL,
    timestamp    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Locked contract** — `commands_log` fields exact: `(command_id, device_id, action, params_json, result, timestamp)`.

---

## Task B — Database singleton

**Create** `src/main/java/com/smarthome/persistence/Database.java`:

```java
package com.smarthome.persistence;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * SINGLETON PATTERN
 * Owns the single SQLite connection for the application.
 * Loads schema.sql on first use to ensure tables exist.
 */
public class Database {
    private static final String DB_URL = "jdbc:sqlite:smarthome.db";
    private static final Database INSTANCE = new Database();

    private final Connection connection;

    private Database() {
        try {
            this.connection = DriverManager.getConnection(DB_URL);
            initSchema();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SQLite database", e);
        }
    }

    public static Database getInstance() { return INSTANCE; }

    public Connection getConnection() { return connection; }

    private void initSchema() throws Exception {
        try (var in = getClass().getResourceAsStream("/db/schema.sql");
             var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String sql = reader.lines().collect(Collectors.joining("\n"));
            try (Statement stmt = connection.createStatement()) {
                for (String statement : sql.split(";")) {
                    if (!statement.trim().isEmpty()) stmt.execute(statement);
                }
            }
        }
    }
}
```

---

## Task C — Six DAO classes

All under `src/main/java/com/smarthome/persistence/dao/`.

Each DAO follows the same shape:

```java
public class FooDAO {
    private final Connection conn;

    public FooDAO() { this.conn = Database.getInstance().getConnection(); }

    public void insert(Foo f) { /* prepared statement */ }
    public Foo findById(String id) { /* prepared statement */ }
    public List<Foo> findAll() { /* prepared statement */ }
    public void update(Foo f) { /* prepared statement */ }
    public void delete(String id) { /* prepared statement */ }
}
```

Required DAOs (minimum methods listed):

### 1. `UserDAO`
- `insert(User u)`, `findById(String id)`, `findByName(String name)`, `verifyPin(String userId, String pin) -> boolean`

### 2. `RoomDAO`
- `insert(Room r)`, `findById(String id)`, `findAll() -> List<Room>`, `delete(String id)`

### 3. `DeviceDAO`
- `insert(Device d, String roomId)`, `findById(String id)`, `findByRoom(String roomId) -> List<Device>`, `updatePowerState(String id, boolean on)`, `updateStateJson(String id, String json)`, `delete(String id)`

### 4. `DeviceEventDAO`
- `insert(String deviceId, String eventType)`, `findRecent(int limit) -> List<DeviceEvent>`, `findByDevice(String deviceId, int limit)`

### 5. `ScheduleDAO`
- `insert(Schedule s)`, `findEnabled() -> List<Schedule>`, `setEnabled(String id, boolean enabled)`, `delete(String id)`

### 6. `CommandsLogDAO`
- `insert(String commandId, String deviceId, String action, String paramsJson, String result)`
- `findRecent(int limit) -> List<CommandLog>`
- `findByDevice(String deviceId, int limit)`

You'll also need 2-3 small DTO classes (record types are perfect):

```java
public record DeviceEvent(long eventId, String deviceId, String eventType, Instant timestamp) {}
public record CommandLog(String commandId, String deviceId, String action, String paramsJson, String result, Instant timestamp) {}
public record Schedule(String scheduleId, String deviceId, String modeName, String cronExpr, boolean enabled) {}
```

---

## Task D — Hard contracts

Other layers depend on these. Don't change without consensus:

- **Schema table names:** `users`, `rooms`, `devices`, `device_events`, `schedules`, `commands_log`
- **`commands_log` columns** (exact order):
  `command_id, device_id, action, params_json, result, timestamp`
- **Device ID format:** UUID string (matches `DeviceFactory.newId()`)
- **Database singleton accessor:** `Database.getInstance()`
- **Use prepared statements ALWAYS** — protects against SQL injection (rubric: "prevent invalid/unsafe operations")

---

## Task E — Acceptance tests

Create `src/test/java/com/smarthome/persistence/PersistenceTest.java`:

```java
@Test void schemaInitializesAllTables() {
    Database.getInstance(); // triggers schema init
    // verify all 6 tables exist via PRAGMA table_info or sqlite_master query
}

@Test void roomDaoRoundTrip() {
    RoomDAO dao = new RoomDAO();
    dao.insert(new Room("test-1", "Test Kitchen"));
    Room r = dao.findById("test-1");
    assertEquals("Test Kitchen", r.getName());
}

@Test void commandsLogInsertAndRead() {
    CommandsLogDAO dao = new CommandsLogDAO();
    dao.insert("cmd-1", "dev-1", "TURN_ON", "{}", "OK");
    List<CommandLog> recent = dao.findRecent(10);
    assertFalse(recent.isEmpty());
    assertEquals("TURN_ON", recent.get(0).action());
}
```

**Important:** for tests, point to an in-memory DB (`jdbc:sqlite::memory:`) or use a separate test DB file that gets deleted between tests. Don't pollute `smarthome.db`.

---

## Submit

```powershell
./mvnw test
git push -u origin m2-database
```

PR title: `M2: SQLite schema + Database singleton + 6 DAO classes`
