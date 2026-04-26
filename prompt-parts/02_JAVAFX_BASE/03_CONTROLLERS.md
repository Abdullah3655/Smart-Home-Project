# Purpose
Implement thin controllers that delegate all operations to facade.

# Exact files to create/update
- `src/main/java/com/smarthome/ui/DashboardController.java`
- `src/main/java/com/smarthome/ui/HistoryController.java`
- `src/main/java/com/smarthome/ui/ScheduleController.java`

# Exact classes/interfaces
- `DashboardController`
- `HistoryController`
- `ScheduleController`

# Exact method signatures
- `void onToggleDevice(ActionEvent e)`
- `void onSetMode(ActionEvent e)`
- `void onRefreshHistory(ActionEvent e)`
- `void onCreateSchedule(ActionEvent e)`

# Logic rules (must implement)
- Handlers parse UI input and call facade methods.
- Controller state refreshes UI models/tables from facade responses.
- Error handling is user-facing (alerts), not stacktrace-only.

# Dependencies from other parts
- Depends on `facade.HomeController` and facade method set.

# Out of scope
- Direct DAO calls.
- Domain mutation bypassing facade.

# Acceptance checklist
- Each button path triggers expected facade method.
- History and schedules load without direct SQL in controller.
- Controllers compile with FXML handlers.

