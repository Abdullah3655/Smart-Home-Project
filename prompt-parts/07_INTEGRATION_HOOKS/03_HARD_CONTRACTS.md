# Purpose
Lock integration invariants that no teammate is allowed to change.

# Exact files to create/update
- All part files consuming these contracts.

# Exact classes/interfaces
- `Observer`, `Room`, `DeviceFactory`, `HomeController`, logging DAOs.

# Exact method signatures
- Observer: `update(Device d, String event)`
- Iterator: `Room.devices()` returns `Enumeration<Device>`

# Logic rules (must implement)
- Device IDs are UUID strings from factory.
- Command log fields are exactly:
  `(command_id, device_id, action, params_json, result, timestamp)`.
- Controllers call facade only.
- Required alternatives remain:
  Observer Push vs Pull, DAO vs Active Record.

# Dependencies from other parts
- Applies globally to all parts.

# Out of scope
- Contract renaming or signature drift.

# Acceptance checklist
- Contract check across all rewritten files passes.
- No conflicting method signatures or field names.

