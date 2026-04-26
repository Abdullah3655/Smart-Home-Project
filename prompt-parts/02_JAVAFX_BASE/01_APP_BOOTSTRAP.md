# Purpose
Create a stable JavaFX app entry point.

# Exact files to create/update
- `src/main/java/com/smarthome/ui/MainApp.java`
- `src/main/java/com/smarthome/ui/AppLauncher.java` (if needed)

# Exact classes/interfaces
- `MainApp extends javafx.application.Application`

# Exact method signatures
- `public void start(Stage primaryStage) throws Exception`
- `public static void main(String[] args)`

# Logic rules (must implement)
- Load `resources/fxml/dashboard.fxml`.
- Set scene title and size.
- Apply `resources/css/app.css`.
- Exit cleanly on close.

# Dependencies from other parts
- Depends on FXML/CSS resources.
- Uses facade-injected controllers in later steps.

# Out of scope
- Domain mutations in bootstrap class.

# Acceptance checklist
- Running main launches UI without errors.
- Dashboard view appears.
- CSS rules are visible.

