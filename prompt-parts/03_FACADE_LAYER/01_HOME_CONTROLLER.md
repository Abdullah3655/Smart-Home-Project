# Purpose
Implement concrete facade class used by every JavaFX controller.

# Exact files to create/update
- `src/main/java/com/smarthome/facade/HomeController.java`

# Exact classes/interfaces
- `HomeController`

# Exact method signatures
- Must include signatures listed in `02_FACADE_METHOD_SET.md`.

# Logic rules (must implement)
- Route mutating actions through `CommandInvoker`.
- Route reads through hub/DAO as appropriate.
- Normalize errors for controller-friendly messages.

# Dependencies from other parts
- Uses `SmartHomeHub`, `CommandInvoker`, and DAO classes.

# Out of scope
- SQL definitions.
- View rendering logic.

# Acceptance checklist
- Controller can perform all required actions via this class only.
- No duplicated orchestration in controllers.

