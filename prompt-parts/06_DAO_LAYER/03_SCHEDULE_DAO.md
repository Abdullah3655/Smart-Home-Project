# Purpose
Persist schedule definitions used by schedule editor and executor logic.

# Exact files to create/update
- `src/main/java/com/smarthome/persistence/dao/ScheduleDAO.java`

# Exact classes/interfaces
- `ScheduleDAO`

# Exact method signatures
- `void createSchedule(Schedule schedule)`
- `List<Schedule> listSchedules()`
- `void setScheduleEnabled(String scheduleId, boolean enabled)`
- `void deleteSchedule(String scheduleId)`

# Logic rules (must implement)
- Schedule IDs are UUID strings.
- Enable/disable updates status only, not schedule content.
- Reads return deterministic ordering (e.g., by next run time or created time).

# Dependencies from other parts
- Depends on schedules table.
- Used by facade and schedule UI.

# Out of scope
- Background execution thread behavior.

# Acceptance checklist
- Create/list/enable-disable/delete cycle works end-to-end.
- Disabled schedule remains stored but marked inactive.

