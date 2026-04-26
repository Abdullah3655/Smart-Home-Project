# Purpose
Define startup persistence initialization and schema migration baseline.

# Exact files to create/update
- `src/main/java/com/smarthome/persistence/Database.java`
- `src/main/resources/db/schema.sql`

# Exact classes/interfaces
- `Database`

# Exact method signatures
- `initializeSchema()`

# Logic rules (must implement)
- Ensure DB file exists or create automatically.
- Execute `CREATE TABLE IF NOT EXISTS` DDL.
- Verify required table presence at startup.
- Keep init idempotent and safe for repeated runs.

# Dependencies from other parts
- Must complete before DAO operations.

# Out of scope
- Runtime business migrations beyond required project schema.

# Acceptance checklist
- Fresh run initializes DB successfully.
- Existing DB run does not corrupt or duplicate schema.
- Missing table case is repaired by init logic.

