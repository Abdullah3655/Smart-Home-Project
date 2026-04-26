# Purpose
Provide a single database connection manager for all DAOs.

# Exact files to create/update
- `src/main/java/com/smarthome/persistence/Database.java`

# Exact classes/interfaces
- `Database` singleton

# Exact method signatures
- `public static Database getInstance()`
- `public Connection getConnection()`
- `public void initializeSchema()`
- `public void close()`

# Logic rules (must implement)
- Lazy singleton with thread-safe initialization.
- Reuse one connection lifecycle for app session.
- `initializeSchema()` runs idempotent schema creation.
- Graceful close on app shutdown.

# Dependencies from other parts
- Used by all DAO classes.

# Out of scope
- DAO query composition.
- Controller/facade action handling.

# Acceptance checklist
- Repeated `getConnection()` returns same active connection.
- Schema init can run multiple times safely.
- Close then reopen path is predictable.

