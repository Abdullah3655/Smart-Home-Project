# Purpose
Define persistence baseline: schema, database singleton, and startup initialization.

# Exact files to create/update
- `01_SCHEMA_TABLES.md`
- `02_DATABASE_SINGLETON.md`
- `03_SCHEMA_MIGRATION_INIT.md`

# Exact classes/interfaces
- `persistence.Database`
- SQL schema file

# Exact method signatures
- Defined in `02_DATABASE_SINGLETON.md`.

# Logic rules (must implement)
- SQLite is required core persistence.
- Initialization must be idempotent.
- Schema must include all 6 required tables.

# Dependencies from other parts
- Provides storage contracts used by DAO, command logging, and history UI.

# Out of scope
- DAO CRUD details.
- Facade/controller bindings.

# Acceptance checklist
- DB boots from clean environment.
- Required tables exist.
- Connection singleton reused across DAO calls.

