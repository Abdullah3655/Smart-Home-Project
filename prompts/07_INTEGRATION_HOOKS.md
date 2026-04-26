# Prompt 07 - Integration Hooks (Observer + Facade + DAO)

Paste this into Claude/Cursor:

Integrate all layers so runtime flows match architecture.

## Required integration points

1. GUI controller -> `facade.HomeController`
2. Facade -> `CommandInvoker`
3. Command -> domain (`SmartHomeHub`, devices)
4. Device -> observer notifications (`update(Device d, String event)`)
5. Observer hook -> `DeviceEventDAO`
6. Command execution -> `CommandsLogDAO`

## Also integrate

- Strategy mode change via facade and command path.
- Iterator usage in UI/domain list rendering where needed.

## Constraints

- No direct controller -> DAO calls.
- Keep layering clean.

## Acceptance checks

- Full flow works:
  `GUI -> Facade -> Command -> Domain -> Observer -> DAO -> SQLite`
- Mode switch updates behavior and logs correctly.

