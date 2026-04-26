# Purpose
Define mandatory test coverage for architecture contracts and runtime flows.

# Exact files to create/update
- `src/test/java/...` test classes grouped by package

# Exact classes/interfaces
- Tests for `SmartHomeHub`, `Room`, Observer contracts, strategies, commands, DAOs, facade integration.

# Exact method signatures
- JUnit tests for:
  - singleton behavior
  - iterator enumeration
  - observer callback
  - strategy switching
  - command execute/undo
  - DAO persistence
  - end-to-end action flow

# Logic rules (must implement)
- Include at least one integration test for:
  `Facade -> Command -> Domain -> Observer -> DAO`.
- Validate command log fields are persisted correctly.

# Dependencies from other parts
- Depends on complete implementation in previous parts.

# Out of scope
- Performance benchmarking beyond course needs.

# Acceptance checklist
- Tests are repeatable and deterministic.
- Critical path tests pass before report freeze.

