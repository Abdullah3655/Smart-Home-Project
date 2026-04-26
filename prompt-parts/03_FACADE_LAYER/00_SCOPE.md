# Purpose
Define the facade as the single application entry point for JavaFX.

# Exact files to create/update
- `01_HOME_CONTROLLER.md`
- `02_FACADE_METHOD_SET.md`
- `03_UI_WIRING_RULES.md`

# Exact classes/interfaces
- `facade.HomeController`

# Exact method signatures
- Method set fixed in `02_FACADE_METHOD_SET.md`.

# Logic rules (must implement)
- Facade orchestrates command, hub, and DAO calls.
- Facade does not absorb domain business logic.

# Dependencies from other parts
- Depends on foundation + command + DAO contracts.
- Consumed by JavaFX controllers.

# Out of scope
- FXML controller view logic.
- SQL statement implementation.

# Acceptance checklist
- All UI actions can be expressed through facade methods.
- No direct controller-to-domain/DAO paths remain.

