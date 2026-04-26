# Smart Home Prompt Pack - Run Order

Use these files in order so all teammates generate code with the same structure.

1. `01_FOUNDATION_CORE.md`
2. `02_JAVAFX_BASE.md`
3. `03_FACADE_LAYER.md`
4. `04_COMMAND_LAYER.md`
5. `05_DATABASE_SQLITE.md`
6. `06_DAO_LAYER.md`
7. `07_INTEGRATION_HOOKS.md`
8. `08_TESTS_AND_REPORT.md`

## Rules for every prompt run

- Keep package root as `com.smarthome`.
- Use Java 17, Maven, JavaFX 21, SQLite JDBC, JUnit 5.
- Do not rename public classes from previous prompts.
- If a class exists, extend/update it; do not duplicate it.
- Keep Observer contract as `update(Device d, String event)`.
- Keep command log schema as:
  `(command_id, device_id, action, params_json, result, timestamp)`.

