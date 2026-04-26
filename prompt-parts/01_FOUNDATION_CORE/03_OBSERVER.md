# Purpose
Implement the required hand-rolled Observer pattern for device events.

# Exact files to create/update
- `src/main/java/com/smarthome/observer/Observer.java`
- `src/main/java/com/smarthome/observer/Observable.java`
- `src/main/java/com/smarthome/devices/Device.java`

# Exact classes/interfaces
- `Observer`
- `Observable`
- `Device` (implements `Observable`)

# Exact method signatures
- `void update(Device d, String event)` in `Observer`
- `void attach(Observer observer)` in `Observable`
- `void detach(Observer observer)` in `Observable`
- `void notifyObservers(String event)` in `Observable`

# Logic rules (must implement)
- Keep observer list in `Device`.
- On state change, call `notifyObservers(event)`.
- Forward current device instance and event string to every observer.
- Use event strings like `TURNED_ON`, `TURNED_OFF`, `TEMP_CHANGED`, `LOCKED`, `UNLOCKED`.

# Dependencies from other parts
- Used by DAO logging (`DeviceEventDAO`) in integration phase.
- Used by GUI refresh logic via facade/application layer.

# Out of scope
- JavaFX built-in observer classes.
- SQL logic in observer interfaces.

# Acceptance checklist
- Attach observer then trigger state change => `update` called.
- Detach observer => no callback.
- Callback includes same device and correct event string.

