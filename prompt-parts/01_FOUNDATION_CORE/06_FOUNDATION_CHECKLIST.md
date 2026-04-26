# Purpose
Final gate for Foundation completion (must pass before moving to next parts).

# Exact files to create/update
- Foundation implementation files referenced in this folder.

# Exact classes/interfaces
- `SmartHomeHub`, `Room`, `Observer`, `Observable`, `DeviceFactory`, `AutomationMode` variants.

# Exact method signatures
- Must match method lists in `01_SINGLETON.md` through `05_STRATEGY.md`.

# Logic rules (must implement)
- Singleton works and is thread-safe.
- Iterator contract is `Enumeration<Device>`.
- Observer callback is exactly `update(Device d, String event)`.
- Factory assigns UUID IDs.
- Strategy can switch at runtime.

# Dependencies from other parts
- Unblocks JavaFX, Facade, Command, and DAO integration.

# Out of scope
- Database persistence.
- UI/facade specific behavior.

# Acceptance checklist
- All foundation classes compile together.
- Core flows run in a simple smoke test.
- No contract conflicts with global rules in `prompt-parts/README.md`.

