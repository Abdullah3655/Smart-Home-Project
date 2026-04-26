# Purpose
Implement core entity DAOs used by facade and UI views.

# Exact files to create/update
- `src/main/java/com/smarthome/persistence/dao/UserDAO.java`
- `src/main/java/com/smarthome/persistence/dao/RoomDAO.java`
- `src/main/java/com/smarthome/persistence/dao/DeviceDAO.java`

# Exact classes/interfaces
- `UserDAO`
- `RoomDAO`
- `DeviceDAO`

# Exact method signatures
- `void insert(...)`
- `Optional<T> findById(String id)`
- `List<T> findAll()`
- `void update(...)`
- `void delete(String id)`

# Logic rules (must implement)
- Use prepared statements only.
- Map DB rows to domain/entity models consistently.
- Keep ID fields as UUID strings.

# Dependencies from other parts
- Depends on schema and database singleton.
- Used by facade reads/writes.

# Out of scope
- Observer event logging.
- Command log persistence.

# Acceptance checklist
- CRUD lifecycle works for user, room, and device records.
- Invalid ID paths return empty/handled result without crashes.

