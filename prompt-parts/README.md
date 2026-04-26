# Prompt Parts (Strict Contracts)

This folder contains strict implementation contracts for the whole project.

## Run order

1. `01_FOUNDATION_CORE`
2. `02_JAVAFX_BASE`
3. `03_FACADE_LAYER`
4. `04_COMMAND_LAYER`
5. `05_DATABASE_SQLITE`
6. `06_DAO_LAYER`
7. `07_INTEGRATION_HOOKS`
8. `08_TESTS_AND_REPORT`

## Required format in every subpart file

1. Purpose
2. Exact files to create/update
3. Exact classes/interfaces
4. Exact method signatures
5. Logic rules (must implement)
6. Dependencies from other parts
7. Out of scope
8. Acceptance checklist

## Global contracts (must stay consistent)

- Patterns: Singleton, Abstract Factory, Observer, Iterator, Strategy, DAO, Command, Facade, Decorator.
- Observer signature: `update(Device d, String event)`.
- Iterator contract: `Room.devices()` returns `Enumeration<Device>`.
- Device ID source: UUID from factory.
- Command log fields: `(command_id, device_id, action, params_json, result, timestamp)`.
- UI layering: JavaFX controllers call facade only.
- Required alternatives in report: Observer Push vs Pull, DAO vs Active Record.

