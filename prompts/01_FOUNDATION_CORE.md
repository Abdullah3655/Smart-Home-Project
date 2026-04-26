# Prompt 01 - Foundation Core (Singleton + Iterator + Observer + Factory + Strategy)

Paste this into Claude/Cursor:

Build the foundation of a Java smart-home system using Java 17.

## Required packages

- `com.smarthome.core`
- `com.smarthome.devices`
- `com.smarthome.observer`
- `com.smarthome.factory`
- `com.smarthome.strategy`

## Implement now

1. **Singleton**
   - `core.SmartHomeHub` as thread-safe singleton.
2. **Iterator**
   - `core.Room.devices()` returns `Enumeration<Device>`.
3. **Observer**
   - `observer.Observer` with `update(Device d, String event)`.
   - `observer.Observable` with `attach`, `detach`, `notifyObservers`.
   - Device classes notify observers on state changes.
4. **Abstract Factory + Factory Methods**
   - `factory.DeviceFactory` + concrete factories for at least light, thermostat, lock/camera families.
5. **Strategy**
   - `strategy.AutomationMode` with `EcoMode`, `SleepMode`, `AwayMode`.

## Constraints

- No database code in this prompt.
- No JavaFX code in this prompt.
- Keep classes small and SOLID.

## Acceptance checks

- Can create rooms/devices through factory.
- Can iterate room devices via `Enumeration`.
- Device state updates trigger observer callbacks.
- Changing automation mode switches behavior without editing caller code.

