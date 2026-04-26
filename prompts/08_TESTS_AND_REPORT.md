# Prompt 08 - Tests + Report Inputs (Alternatives and SOLID)

Paste this into Claude/Cursor:

Add tests and reporting artifacts to validate architecture and assignment requirements.

## Testing scope

- Unit tests for:
  - singleton behavior
  - iterator enumeration behavior
  - strategy switching
  - command execute/undo
  - DAO persistence for key entities
- Integration tests for:
  - `GUI/Fascade action -> command -> domain -> observer -> DAO`

## Report-ready outputs

Produce concise markdown notes for:

1. **Alternative design #1: Observer Push vs Pull**
   - explain chosen push model and reason
2. **Alternative design #2: DAO vs Active Record**
   - explain chosen DAO model and reason
3. **SOLID mapping** across your packages/classes
4. **Pattern-to-class mapping** for all 9 patterns

## Constraints

- Keep explanations concrete and linked to actual class names.
- No hardware/ESP32 scope in core deliverables.

