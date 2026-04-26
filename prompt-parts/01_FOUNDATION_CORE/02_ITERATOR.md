# Purpose
Expose room devices through Iterator pattern using `Enumeration<Device>`.

# Exact files to create/update
- `src/main/java/com/smarthome/core/Room.java`

# Exact classes/interfaces
- `Room`

# Exact method signatures
- `public void addDevice(Device device)`
- `public void removeDevice(String deviceId)`
- `public Device getDevice(String deviceId)`
- `public Enumeration<Device> devices()`

# Logic rules (must implement)
- Room stores devices internally (encapsulated collection).
- `devices()` must return `Enumeration<Device>`.
- Device lookup/removal is by `deviceId`.

# Dependencies from other parts
- Depends on base `Device` class.
- Used by JavaFX/facade to list room devices.

# Out of scope
- Observer persistence hooks.
- UI rendering logic.

# Acceptance checklist
- Adding 3 devices then iterating returns 3 items.
- Removing by ID removes only that device.
- No direct mutable collection leak.

