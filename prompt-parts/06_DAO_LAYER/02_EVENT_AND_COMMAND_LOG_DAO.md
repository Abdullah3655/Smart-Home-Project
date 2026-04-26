# Purpose
Implement strict persistence for observer events and command history.

# Exact files to create/update
- `src/main/java/com/smarthome/persistence/dao/DeviceEventDAO.java`
- `src/main/java/com/smarthome/persistence/dao/CommandsLogDAO.java`

# Exact classes/interfaces
- `DeviceEventDAO`
- `CommandsLogDAO`

# Exact method signatures
- `void insertEvent(String eventId, String deviceId, String event, String detailsJson, Instant timestamp)`
- `List<DeviceEvent> listByDevice(String deviceId, int limit, int offset)`
- `void insertCommandLog(String commandId, String deviceId, String action, String paramsJson, String result, Instant timestamp)`
- `List<CommandLog> listRecent(int limit, int offset)`

# Logic rules (must implement)
- Observer flow writes `device_events` on each device notification.
- Command flow writes `commands_log` with exact required fields:
  `(command_id, device_id, action, params_json, result, timestamp)`.
- Sort history descending by timestamp for recent views.

# Dependencies from other parts
- Depends on observer contract and command contract.
- Used by history UI via facade.

# Out of scope
- Command execution logic itself.
- Controller-level table rendering.

# Acceptance checklist
- Trigger event -> row appears in `device_events`.
- Execute command -> row appears in `commands_log`.
- Recent list queries return expected order and pagination behavior.

