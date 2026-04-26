# Purpose
Provide exact pattern-to-class mapping for report and review presentation.

# Exact files to create/update
- Report pattern mapping section file.

# Exact classes/interfaces
- Singleton -> `SmartHomeHub`, `Database`
- Abstract Factory -> `DeviceFactory` + concrete factories
- Observer -> `Observer`, `Observable`, `Device`
- Iterator -> `Room.devices()` returning `Enumeration<Device>`
- Strategy -> `AutomationMode`, `EcoMode`, `SleepMode`, `AwayMode`
- DAO -> `persistence.dao.*`
- Command -> `DeviceCommand`, concrete commands, `CommandInvoker`
- Facade -> `HomeController`
- Decorator -> `SecureDevice`, `LoggedDevice` (and optional `RateLimitedDevice`)

# Exact method signatures
- Include signature examples per pattern in report, especially:
  `update(Device d, String event)` and `Room.devices()`.

# Logic rules (must implement)
- Mapping must reference real implemented classes only.
- Each pattern entry includes where used and why selected.

# Dependencies from other parts
- Depends on final code/package naming.

# Out of scope
- Patterns not in approved 9-pattern inventory.

# Acceptance checklist
- All 9 patterns mapped to concrete classes.
- Mapping matches architecture and code structure exactly.

