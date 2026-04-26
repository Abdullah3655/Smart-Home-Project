# Prompt 02 - JavaFX Base (UI skeleton only)

Paste this into Claude/Cursor:

Create the JavaFX base UI for a smart-home app.

## Required output

- `ui` package with app launcher and controllers.
- `resources/fxml` with at least:
  - `dashboard.fxml`
  - `history.fxml`
  - `schedule.fxml`
- `resources/css/app.css`.

## UI responsibilities

- Display rooms/devices.
- Expose controls for actions (on/off, mode change, lock/unlock).
- No SQL code in controllers.
- No core business rules in controllers.

## Architectural rule

Controllers must call a facade service, not hub/DAO directly.

## Acceptance checks

- App launches and loads FXML without runtime errors.
- Buttons/events are wired to controller methods.
- Theme/CSS is applied.

