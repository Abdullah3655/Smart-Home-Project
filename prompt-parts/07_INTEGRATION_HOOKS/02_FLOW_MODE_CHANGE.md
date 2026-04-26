# Purpose
Wire strategy mode switching through approved application path.

# Exact files to create/update
- JavaFX mode control handler
- `facade/HomeController`
- `SetAutomationModeCommand` (or equivalent)
- strategy classes and hub integration

# Exact classes/interfaces
- `AutomationMode`, `EcoMode`, `SleepMode`, `AwayMode`
- `SetAutomationModeCommand`
- `SmartHomeHub`

# Exact method signatures
- `setAutomationMode(String modeName)` on facade
- `apply(SmartHomeHub hub)` on strategy

# Logic rules (must implement)
- Flow must be:
  `GUI mode select -> Facade.setAutomationMode -> Command/Hub -> Strategy.apply`.
- No caller-side mode branching logic in UI.

# Dependencies from other parts
- Depends on strategy and command completion.

# Out of scope
- Additional non-required modes.

# Acceptance checklist
- Switching among Eco/Sleep/Away changes behavior.
- UI code unchanged when adding new strategy class.

