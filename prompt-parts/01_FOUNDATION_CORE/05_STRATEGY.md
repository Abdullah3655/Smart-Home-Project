# Purpose
Implement runtime-switchable automation behavior using Strategy pattern.

# Exact files to create/update
- `src/main/java/com/smarthome/strategy/AutomationMode.java`
- `src/main/java/com/smarthome/strategy/EcoMode.java`
- `src/main/java/com/smarthome/strategy/SleepMode.java`
- `src/main/java/com/smarthome/strategy/AwayMode.java`

# Exact classes/interfaces
- `AutomationMode`
- `EcoMode`
- `SleepMode`
- `AwayMode`

# Exact method signatures
- `String name()`
- `void apply(SmartHomeHub hub)`

# Logic rules (must implement)
- `SmartHomeHub` keeps current strategy instance.
- Switching mode changes behavior through `apply`.
- Avoid mode-specific `if/else` in caller code.

# Dependencies from other parts
- Depends on `SmartHomeHub`.
- Called from Command and Facade layers.

# Out of scope
- UI dropdown rendering.
- DAO schema logic.

# Acceptance checklist
- Mode can switch between Eco/Sleep/Away at runtime.
- `name()` values are stable and human-readable.
- `apply` has observable effect on hub/device defaults.

