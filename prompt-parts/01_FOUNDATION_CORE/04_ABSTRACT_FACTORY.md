# Purpose
Implement Abstract Factory + Factory Methods for device family creation.

# Exact files to create/update
- `src/main/java/com/smarthome/factory/DeviceFactory.java`
- `src/main/java/com/smarthome/factory/LightFactory.java`
- `src/main/java/com/smarthome/factory/ClimateFactory.java`
- `src/main/java/com/smarthome/factory/SecurityFactory.java`

# Exact classes/interfaces
- `DeviceFactory` (abstract class or interface)
- `LightFactory`
- `ClimateFactory`
- `SecurityFactory`

# Exact method signatures
- `Device createLight(String name)`
- `Device createThermostat(String name)`
- `Device createDoorLock(String name)`
- `Device createCamera(String name)`

# Logic rules (must implement)
- Generate UUID for each new device inside factory layer.
- Concrete factory returns only its owned family; non-owned methods fail predictably.
- Device IDs are immutable once assigned.

# Dependencies from other parts
- Depends on concrete device classes.
- Feeds IDs/contracts for DAO and UI usage.

# Out of scope
- Device persistence insertion.
- Command/facade orchestration.

# Acceptance checklist
- Factories produce devices with non-empty UUID IDs.
- Returned types match requested family.
- Unsupported creation path is handled consistently.

