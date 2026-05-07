# Report screenshots

Place GUI screenshots here as PNG. The report (`../report.md`) references
each image by exact filename — keep names matching.

## How to capture each shot

Run `./mvnw javafx:run` from the project root, then for each image:

| Filename | What to capture | How to reproduce |
|---|---|---|
| `home.png` | The Home screen with at least 2 rooms and their device cards visible | Default landing screen on app launch |
| `mode-confirm.png` | The ECO/SLEEP/AWAY confirmation dialog open on top of the Home screen | Tap ECO/SLEEP/AWAY in the top bar — the confirm dialog appears |
| `history.png` | The History tab showing several event rows | Tap something on Home (e.g. turn a light on/off a few times), then tap 📋 History in the bottom nav |
| `decorator.png` | The Decorator tab with a device wrapped and captured log entries visible | Tap 🎁 Demo, pick a device, tap "Wrap with Logging", then "Turn On (wrapped)" once or twice |
| `add-device.png` | The Add Device modal with type and family choice boxes visible | Tap "+ Add device to {Room}" at the bottom of any room's device list |

## Capturing on Windows

`Win + Shift + S` → drag a rectangle around the app window → screenshot
copies to clipboard → paste into Paint or directly save. Save each PNG
under this folder with the filename listed above.

## After capturing

Re-export the report (`docs/report.md` → `report.pdf`) and the images
will appear inline in the **Screenshots — GUI in action** section.
