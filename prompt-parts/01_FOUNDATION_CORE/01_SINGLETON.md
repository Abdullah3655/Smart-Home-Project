# Purpose
Implement the system coordinator singleton for domain state.

# Exact files to create/update
- `src/main/java/com/smarthome/core/SmartHomeHub.java`

# Exact classes/interfaces
- `SmartHomeHub`

# Exact method signatures
- `public static SmartHomeHub getInstance()`
- `public void addRoom(Room room)`
- `public Room getRoom(String roomId)`
- `public Collection<Room> getRooms()`
- `public void setAutomationMode(AutomationMode mode)`
- `public AutomationMode getAutomationMode()`

# Logic rules (must implement)
- Singleton must be thread-safe.
- Constructor must be private.
- Keep room lookup by room ID.
- Hold active `AutomationMode`.
- No SQL/persistence logic in this class.

# Dependencies from other parts
- Depends on `Room` and `AutomationMode`.
- Used by Facade and Command layers.

# Out of scope
- JavaFX controller code.
- DAO/database operations.

# Acceptance checklist
- Multiple calls to `getInstance()` return same object.
- `addRoom` then `getRoom` returns same room.
- Mode set/get works.

