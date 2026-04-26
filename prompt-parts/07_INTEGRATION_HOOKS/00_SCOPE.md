# Purpose
Define strict cross-layer wiring and invariant contracts for final integration.

# Exact files to create/update
- `01_FLOW_ACTION.md`
- `02_FLOW_MODE_CHANGE.md`
- `03_HARD_CONTRACTS.md`

# Exact classes/interfaces
- `HomeController`, `CommandInvoker`, command classes, domain classes, observer contracts, DAOs.

# Exact method signatures
- Must honor signatures declared in foundation/facade/command/dao subparts.

# Logic rules (must implement)
- All cross-layer calls follow approved architecture flow.
- No shortcut calls violating layer boundaries.

# Dependencies from other parts
- Depends on completed foundation, facade, command, and DAO parts.

# Out of scope
- New feature additions outside contract.

# Acceptance checklist
- End-to-end action and mode flows work as specified.
- Global hard contracts are respected.

