# Purpose
Define DAO contracts and CRUD/logging behavior with strict SQL isolation.

# Exact files to create/update
- `01_USER_ROOM_DEVICE_DAO.md`
- `02_EVENT_AND_COMMAND_LOG_DAO.md`
- `03_SCHEDULE_DAO.md`
- `04_DAO_CONTRACTS.md`

# Exact classes/interfaces
- `UserDAO`, `RoomDAO`, `DeviceDAO`, `DeviceEventDAO`, `ScheduleDAO`, `CommandsLogDAO`

# Exact method signatures
- Defined in subpart files.

# Logic rules (must implement)
- DAOs own SQL; domain objects are SQL-free.
- DAOs use `Database` singleton connection.
- Logging DAOs honor Observer and Command contracts.

# Dependencies from other parts
- Depends on finalized schema and database singleton.

# Out of scope
- JavaFX UI code.
- Command execution logic.

# Acceptance checklist
- DAO operations complete successfully for required entities.
- History and command logs can be persisted/retrieved.

