# Purpose
Enforce strict presentation-layer boundaries.

# Exact files to create/update
- JavaFX controller classes under `src/main/java/com/smarthome/ui/`.

# Exact classes/interfaces
- `DashboardController`, `HistoryController`, `ScheduleController`.

# Exact method signatures
- Handler methods remain in controller classes; internal calls target facade methods.

# Logic rules (must implement)
- Controllers import facade API only for application actions.
- Controllers must not import DAO classes.
- Controllers must not call `SmartHomeHub` directly.
- All mutating actions go through `HomeController` -> command path.

# Dependencies from other parts
- Depends on completed facade method set.

# Out of scope
- DAO implementation details.
- Strategy/device internal logic.

# Acceptance checklist
- Static search finds no DAO imports in controller classes.
- Static search finds no direct `SmartHomeHub` usage in controllers.
- All action handlers delegate to facade.

