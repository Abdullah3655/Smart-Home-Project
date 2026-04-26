# Purpose
Lock non-negotiable DAO boundaries and naming consistency.

# Exact files to create/update
- All DAO files under `src/main/java/com/smarthome/persistence/dao/`

# Exact classes/interfaces
- All DAO classes listed in DAO scope.

# Exact method signatures
- Use consistent naming style: `insert`, `findById`, `findAll`, `update`, `delete`, `listRecent`.

# Logic rules (must implement)
- DAO classes handle SQL only.
- Domain entities do not execute SQL.
- Facade/commands call DAOs through typed methods only.
- Always use prepared statements.

# Dependencies from other parts
- Depends on database singleton and schema contracts.

# Out of scope
- View formatting logic.
- Strategy/decorator internals.

# Acceptance checklist
- No SQL appears in domain/facade/controller classes.
- DAO naming is consistent across all files.
- Contract matches table fields and global project rules.

