# Purpose
Define the JavaFX UI base that consumes facade services only.

# Exact files to create/update
- `01_APP_BOOTSTRAP.md`
- `02_FXML_LAYOUTS.md`
- `03_CONTROLLERS.md`
- `04_STYLING.md`

# Exact classes/interfaces
- `ui.MainApp`
- `ui` controller classes
- FXML and CSS resources

# Exact method signatures
- Enforced in subpart files for launcher/controllers.

# Logic rules (must implement)
- JavaFX is presentation-only.
- UI must not contain SQL or domain business logic.
- Controllers call facade only.

# Dependencies from other parts
- Depends on foundation contracts and facade method set.

# Out of scope
- DAO SQL implementation.
- Command internals.

# Acceptance checklist
- App boots and loads dashboard scene.
- Controllers are wired to FXML.
- Styling is applied.

