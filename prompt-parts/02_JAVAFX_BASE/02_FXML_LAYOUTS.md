# Purpose
Define UI layouts needed for core flows and persistence views.

# Exact files to create/update
- `src/main/resources/fxml/dashboard.fxml`
- `src/main/resources/fxml/history.fxml`
- `src/main/resources/fxml/schedule.fxml`

# Exact classes/interfaces
- FXML bound to controller classes in `com.smarthome.ui`.

# Exact method signatures
- Controller handler names used in FXML must exist in controller files.

# Logic rules (must implement)
- Dashboard contains room/device listing and action controls.
- History view contains event and command history tables.
- Schedule view contains create/list/enable-disable controls.

# Dependencies from other parts
- Depends on facade method set for action handlers.

# Out of scope
- Business logic in FXML.
- SQL queries in controller bindings.

# Acceptance checklist
- All FXML files load without parse errors.
- Controls map to existing handlers.
- Navigation between views works (single scene with tabs or multi-view).

