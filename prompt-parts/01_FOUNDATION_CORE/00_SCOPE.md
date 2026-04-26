# Purpose
Define and deliver the 5 foundation patterns required before any higher-layer integration.

# Exact files to create/update
- `01_SINGLETON.md`
- `02_ITERATOR.md`
- `03_OBSERVER.md`
- `04_ABSTRACT_FACTORY.md`
- `05_STRATEGY.md`
- `06_FOUNDATION_CHECKLIST.md`

# Exact classes/interfaces
- `core.SmartHomeHub`
- `core.Room`
- `observer.Observer`
- `observer.Observable`
- `factory.DeviceFactory` + concrete factories
- `strategy.AutomationMode` + `EcoMode` + `SleepMode` + `AwayMode`

# Exact method signatures
- Enforced in each subpart file.

# Logic rules (must implement)
- Complete foundation with no SQL and no JavaFX dependencies.
- Preserve contracts used later by facade, command, and DAO.

# Dependencies from other parts
- This part has no upstream dependency except project skeleton.
- It unblocks JavaFX, command, and persistence integration.

# Out of scope
- DAO/table design.
- Facade/controller wiring.

# Acceptance checklist
- All 5 foundation pattern subparts pass their own checklists.
- Shared contracts match `SMARTHOME_COMPLETE_PLAN.md`.

