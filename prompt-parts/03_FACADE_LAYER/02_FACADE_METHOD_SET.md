# Purpose
Fix stable facade API surface for UI, tests, and integration.

# Exact files to create/update
- `src/main/java/com/smarthome/facade/HomeController.java`

# Exact classes/interfaces
- `HomeController`

# Exact method signatures
- `void turnOnDevice(String deviceId)`
- `void turnOffDevice(String deviceId)`
- `void lockDevice(String deviceId)`
- `void unlockDevice(String deviceId)`
- `void setTemperature(String deviceId, double value)`
- `void setAutomationMode(String modeName)`
- `List<Device> getDevicesForRoom(String roomId)`
- `List<DeviceEvent> getEventHistory()`
- `List<CommandLog> getCommandHistory()`
- `void createSchedule(ScheduleRequest request)`

# Logic rules (must implement)
- Method names/signatures remain stable once published.
- Mutating methods invoke command workflow.
- Read methods do not mutate state.

# Dependencies from other parts
- Depends on domain models and log/schedule DTOs/entities.

# Out of scope
- FXML handler naming.
- SQL schema changes.

# Acceptance checklist
- JavaFX controllers compile against this API.
- Test plan references this same method set.

