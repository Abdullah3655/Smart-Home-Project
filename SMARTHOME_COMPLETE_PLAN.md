# Smart Home Automation — Complete Team Plan

**All-in-one document**: master plan, task graph, per-member briefs, and agent-ready prompts for Claude Code / Cursor / any coding agent.

---

## Table of Contents

1. [Master Plan](#1-master-plan)
2. [Task Dependency Graph](#2-task-dependency-graph)
3. Member Briefs
   - [M1 — Command + Decorator + Foundation + Testing](#m1-brief)
   - [M2 — Foundation + Database + DAO](#m2-brief)
   - [M3 — JavaFX GUI + Facade](#m3-brief)
   - [M4 — Security + Safety + Schedules + Integration Testing](#m4-brief)
   - [M5 — Documentation + Report + UML + Demo Video](#m5-brief)
4. Agent Prompts (run in numeric order)
   - Prompt 01 — Project Skeleton
   - Prompt 02 — Core Domain
   - Prompt 03 — Singleton Hub
   - Prompt 04 — Iterator Pattern Verification
   - Prompt 05 — Observer Pattern
   - Prompt 06 — Abstract Factory
   - Prompt 07 — Strategy Pattern
   - Prompt 08 — JavaFX Skeleton
   - Prompt 09 — Foundation Smoke Test (🚪 GATE)
   - Prompt 10 — Dashboard v1
   - Prompts 11–14 — Database + DAOs
   - Prompts 15–16 — Command Pattern
   - Prompt 17 — Schedule Executor
   - Prompt 18 — GUI Tabs
   - Prompt 19 — Facade
   - Prompts 20–23 — Security, Decorator, Safety, Tests

---

# 1. Master Plan

**Course:** CSE3202 / SE 491 — Software Component Design
**Deadline:** May 8, 2026
**Team:** 5 members (M1–M5)
**Starting state:** Empty repo, nothing built yet

---

## What We're Building

A Java-based Smart Home Automation System with:
- **JavaFX GUI** (FXML + Scene Builder + CSS) — modern, polished dashboard
- **SQLite database** — persists users, rooms, devices, events, commands, schedules
- **9 design patterns** — 4 required by assignment + 5 additional
- **Safety/automation rules** — motion detection, temperature alerts, automated modes
- **Undo functionality** — every user action can be reversed
- **Multi-user PIN system** — secure device access

**User Functionalities:**
- View all rooms and devices in the home
- Control individual devices (lights, thermostat, camera, door lock)
- Switch automation modes (Eco / Sleep / Away)
- Schedule automations (turn on Eco at 10 PM daily, etc.)
- View event history (what happened and when)
- View command history and undo actions
- Manage users and PINs
- Receive real-time notifications when devices change state

---

## Design Pattern Inventory

| # | Pattern | Where It Lives | Owner | Target Day |
|---|---|---|---|---|
| 1 | Singleton | `core.SmartHomeHub` | M1+M2 paired | Day 2 |
| 2 | Abstract Factory + Factory Methods | `factory.*` | M1+M2 paired | Day 3 |
| 3 | Observer | `observer.*` + `Device` | M1+M2 paired | Day 3 |
| 4 | Iterator | `core.Room.devices()` Enumeration | M1+M2 paired | Day 2 |
| 5 | Strategy | `strategy.AutomationMode` + modes | M1+M2 paired | Day 4 |
| 6 | DAO | `persistence.dao.*` | M2 | Day 9 |
| 7 | Command | `command.*` + CommandInvoker | M1 | Day 8 |
| 8 | Facade | `facade.HomeController` | M3 | Day 12 |
| 9 | Decorator | `devices.decorator.*` | M1 | Day 11 |

---

## Timeline

```
Day 1  (Apr 22) │ Everyone parallel prep + M1/M2 repo skeleton
Day 2  (Apr 23) │ FOUNDATION: Singleton + Iterator         [M1+M2 paired]
Day 3  (Apr 24) │ FOUNDATION: Observer + Abstract Factory  [M1+M2 paired]
Day 4  (Apr 25) │ FOUNDATION: Strategy + base GUI skeleton [M1+M2 paired]
                │ ← GATE: 5 core patterns working →
Day 5  (Apr 26) │ DB foundation (M2) + Commands start (M1) + Real GUI (M3)
Day 6  (Apr 27) │ Continue
Day 7  (Apr 28) │ SecurityContext (M4)
Day 8  (Apr 29) │ Commands done (M1) → CommandInvoker integration
Day 9  (Apr 30) │ DAOs done (M2) → domain integration
Day 10 (May 1)  │ DB-driven GUI tabs (M3)
Day 11 (May 2)  │ Decorator done (M1) + Safety rules (M4)
Day 12 (May 3)  │ CORE INTEGRATION DAY — all hands
                │ Facade (M3) + Schedule engine (M4)
Day 13 (May 4)  │ Report first draft (M5) + full system testing
Day 14 (May 5)  │ Report polish + screenshots + bug fixing
Day 15 (May 6)  │ Demo video recording
Day 16 (May 7)  │ Buffer — do not consume
     (May 8)    │ SUBMIT
```

---

## Critical Path

The critical path is the sequence of tasks that cannot be shortened. If any of these slip, the whole project slips:

```
Day 1-4: M1+M2 foundation
   ↓ (unblocks everyone else)
Day 5-9: M2 builds DAOs
   ↓ (unblocks M3 DB tabs + M1 Command-DB integration)
Day 5-8: M1 builds Commands
   ↓ (unblocks M3 Command History UI)
Day 7-11: M4 SecurityContext + Safety + Schedules
   ↓ (unblocks M1 Decorator integration)
Day 10-12: M3 builds all UI tabs + Facade
   ↓
Day 12: Integration Day
   ↓
Day 13-14: Testing + Report
   ↓
Day 15: Demo video
   ↓
Day 16: Buffer
```

**The single most important milestone: end of Day 4.** If the 5 core patterns aren't demonstrably working by end of Day 4, everything shifts and the extra patterns start getting cut.

---

## The Two Alternative Designs (for the report)

Required by the assignment. Use these two — already chosen for you:

### Alternative #1 — Observer Push vs Pull
- **Chosen:** Push (device sends state with notification)
- **Rejected:** Pull (observer must query device after notification)
- **Justification:** Real-time safety events cannot tolerate the extra round-trip delay of Pull

### Alternative #2 — DAO vs Active Record
- **Chosen:** DAO (separate persistence classes)
- **Rejected:** Active Record (domain classes do their own SQL)
- **Justification:** Assignment explicitly requires "modularity and ease of future expansion." DAO decouples domain from storage; Active Record couples them tightly.

Full writeups with trade-off tables in `alternative_designs_guide.md` (already provided).

---

## Tech Stack (Final)

| Layer | Technology | Why |
|---|---|---|
| Language | Java 17 LTS | Modern features, long-term support |
| GUI | JavaFX 21 with FXML + CSS | Modern look, Scene Builder support |
| Database | SQLite 3 (via xerial JDBC) | Zero-config, file-based, perfect for demo |
| Build | Maven | Standard, handles JavaFX classpath correctly |
| Testing | JUnit 5 | Standard |
| UML | draw.io or PlantUML | Free, exportable |

---

## Critical Risks

1. **JavaFX setup pain.** On Java 11+, JavaFX is not bundled. Every member must configure their IDE with JavaFX dependencies. Budget ~2 hours on Day 1 for this across the team.
2. **Day 4 foundation bottleneck.** If M1+M2 pair doesn't complete the 5 core patterns by Day 4, extras get cut.
3. **Observer pattern must be hand-rolled.** Do NOT use JavaFX's `ObservableValue` or `PropertyChangeListener` as your graded Observer. Write your own `Observer`/`Observable` interfaces or the professor may not recognize the pattern.
4. **Scope creep.** 9 patterns is already ambitious. Do NOT add a 10th without cutting something else.
5. **AI-generated code visibility.** Every team member should be able to explain any code committed under their name. Code review PRs before merging.

---

## What to Cut If Behind Schedule (in order)

1. `RateLimitedDevice` decorator — keep just SecureDevice + LoggedDevice
2. `ScheduleExecutor` background logic — Schedule Editor UI still exists, just doesn't auto-fire
3. Safety rules — list in report as "future work"
4. Decorator pattern entirely — drop to 8 patterns
5. Facade pattern — drop to 7 patterns, GUI talks to Hub directly
6. Command History UI — keep just Event History

**Minimum viable submission: 5 core patterns + SQLite + basic JavaFX GUI + report + UML.**

---

## How to Use the Prompts

All agent prompts are in `prompts/` folder, numbered in dependency order:
- `prompt_01_project_skeleton.md` — run first
- `prompt_02_core_domain.md` — run after 01
- ... and so on

Each prompt is designed for **Claude Code** (recommended) but works with Cursor or chat interfaces with minor adjustments.

Workflow:
1. Whoever owns the task opens the matching prompt
2. Paste into Claude Code / Cursor
3. Review the generated code before committing
4. Verify acceptance criteria listed at bottom of prompt
5. Commit with task ID in message

---

## What Happens Next

Today's 15-minute team meeting:
1. Confirm M1–M5 role assignments
2. Confirm everyone can push to the repo
3. Agree on daily 9 PM WhatsApp standup
4. M1 and M2 schedule their pair programming times for Days 2–4

See `briefs/` folder for per-member Day 1 kickoff instructions.

---

# 2. Task Dependency Graph

Every task in the project, its owner, its dependencies, and what it unblocks.
Read this top-to-bottom: tasks higher up must finish before tasks lower down can start.

---

## Task ID Scheme

Format: `T-<number>` — sequential IDs so commit messages can reference them.
Example: `git commit -m "T-07: add Observer interface"`

---

## Task Table

| ID | Task | Owner | Depends On | Unblocks | Prompt File | Day |
|----|------|-------|-----------|---------|-------------|-----|
| T-01 | Project skeleton + pom.xml + JavaFX setup | M1+M2 | — | Everything | `prompt_01_project_skeleton.md` | 1 |
| T-02 | Core domain: Device hierarchy + Room | M1+M2 | T-01 | T-03, T-04, T-05, T-09 | `prompt_02_core_domain.md` | 2 |
| T-03 | Singleton: SmartHomeHub | M1+M2 | T-02 | T-04, T-05, T-10, T-11 | `prompt_03_singleton_hub.md` | 2 |
| T-04 | Iterator: Room.devices() Enumeration | M1+M2 | T-02 | T-10 | `prompt_04_iterator.md` | 2 |
| T-05 | Observer: interfaces + User + Device hook | M1+M2 | T-02, T-03 | T-06, T-13, T-15, T-20 | `prompt_05_observer.md` | 3 |
| T-06 | Abstract Factory: DeviceFactory + 3 concretes | M1+M2 | T-02, T-05 | T-10, T-11 | `prompt_06_abstract_factory.md` | 3 |
| T-07 | Strategy: AutomationMode + Eco/Sleep/Away | M1+M2 | T-03, T-05 | T-10, T-17 | `prompt_07_strategy.md` | 4 |
| T-08 | Base JavaFX skeleton (Main + App + minimal FXML) | M1+M2 | T-01 | T-10 | `prompt_08_javafx_skeleton.md` | 4 |
| T-09 | Minimal CLI smoke test (prove foundation works) | M1+M2 | T-02, T-03, T-05, T-06, T-07 | T-10 (confidence gate) | `prompt_09_smoke_test.md` | 4 |
| — | **🚪 GATE: 5 core patterns working, end of Day 4** | — | — | — | — | 4 |
| T-10 | JavaFX Dashboard v1 (rooms, devices, toggle) | M3 | T-08, T-09 | T-12, T-18 | `prompt_10_dashboard_v1.md` | 5–7 |
| T-11 | SQLite schema + Database connection Singleton | M2 | T-01 | T-12, T-13, T-14 | `prompt_11_database.md` | 5–6 |
| T-12 | DAO base + UserDAO + RoomDAO + DeviceDAO | M2 | T-11 | T-13, T-18 | `prompt_12_daos_core.md` | 6–7 |
| T-13 | DeviceEventDAO + Observer→DB hook | M2 | T-05, T-12 | T-18 | `prompt_13_event_logging.md` | 7–8 |
| T-14 | ScheduleDAO + CommandsLogDAO | M2 | T-12 | T-15, T-17 | `prompt_14_daos_extra.md` | 8 |
| T-15 | Command pattern: interface + concretes + Invoker | M1 | T-02, T-14 | T-16, T-18 | `prompt_15_command.md` | 5–8 |
| T-16 | CommandInvoker → DB logging integration | M1 | T-15, T-14 | T-18 | `prompt_16_command_logging.md` | 8–9 |
| T-17 | ScheduleExecutor (background thread) | M4 | T-14, T-07 | — | `prompt_17_schedule_executor.md` | 9–11 |
| T-18 | JavaFX tabs: Event History + Command History + User Mgmt + Schedule Editor | M3 | T-12, T-13, T-15, T-16 | T-19 | `prompt_18_gui_tabs.md` | 8–11 |
| T-19 | Facade: HomeController + refactor GUI | M3 | T-18, T-03, T-15 | T-22 | `prompt_19_facade.md` | 11–12 |
| T-20 | SecurityContext: login + PIN + lockout | M4 | T-05 | T-21 | `prompt_20_security_context.md` | 5–7 |
| T-21 | Decorator: SecureDevice + LoggedDevice | M1 | T-02, T-20, T-13 | — | `prompt_21_decorator.md` | 9–11 |
| T-22 | Safety rules engine (motion+Away, temp>40°C) | M4 | T-05, T-03 | — | `prompt_22_safety_rules.md` | 8–11 |
| T-23 | Unit tests for all patterns | M1 + M4 | T-02 through T-22 | — | `prompt_23_tests.md` | ongoing |
| — | **🚪 GATE: Integration Day, Day 12** | — | — | — | — | 12 |
| T-24 | UML class diagram (v6 final) | M5 | T-01 through T-22 | T-25 | (no code prompt) | 1–13 |
| T-25 | Report sections §1–§5 | M5 | T-24 + each pattern | — | (no code prompt) | 1–14 |
| T-26 | README.md + demo script | M5 | T-18, T-19 | T-27 | (no code prompt) | 13–14 |
| T-27 | Demo video recording | M5 + all | T-26, T-19 | — | (no code prompt) | 15 |

**Total: 27 tasks, 24 with code prompts.**

---

## Dependency Diagram (Who Blocks Whom)

```
                         T-01 Skeleton
                              │
                              ▼
                         T-02 Domain
                              │
            ┌─────────────────┼─────────────────┐
            ▼                 ▼                 ▼
         T-03 Hub         T-04 Iterator      T-11 DB
            │                                    │
            ▼                                    ▼
         T-05 Observer                       T-12 DAOs core
            │                                    │
     ┌──────┼──────┐                   ┌─────────┼─────────┐
     ▼      ▼      ▼                   ▼         ▼         ▼
   T-06   T-20    T-13 ←──────────────T-13    T-14       T-20
  Factory Security Event log                 Extra DAOs
     │      │       │                          │
     ▼      ▼       │                          ▼
   T-07    T-21     │                       T-15 Command
  Strategy Decorator│                          │
     │      │       │                          ▼
     │      │       │                       T-16 Cmd logging
     │      │       │                          │
     │      │       └──────────┬───────────────┘
     │      │                  ▼
     │      │               T-17 Schedules
     │      │                  │
     │      └──────────────────┤
     │                         ▼
     └───────────────────► T-18 GUI Tabs
                              │
                              ▼
                           T-19 Facade
                              │
                              ▼
                           T-22 Safety rules
                              │
                              ▼
                      🚪 T-12 Integration Day
                              │
                      ┌───────┴───────┐
                      ▼               ▼
                   T-23 Tests     T-24 UML final
                                     │
                                     ▼
                                  T-25 Report
                                     │
                                     ▼
                                  T-26 README
                                     │
                                     ▼
                                  T-27 Demo video
                                     │
                                     ▼
                                  SUBMIT
```

---

## Who Can Work in Parallel on Which Day

### Day 1 — Prep Day (everyone different tracks)
- **M1+M2 together:** T-01 project skeleton
- **M3 alone:** JavaFX + Scene Builder tutorial, wireframes
- **M4 alone:** Set up git workflow, install SQLite CLI, draft schema
- **M5 alone:** UML v1 diagram, report outline

### Day 2–4 — Foundation (bottleneck!)
- **M1+M2 paired:** T-02, T-03, T-04, T-05, T-06, T-07, T-08, T-09
- **M3:** Finish JavaFX prep, build throwaway "Hello FX" with FXML
- **M4:** Finish schema draft, prep test data
- **M5:** Continue UML, draft report §1 + §2

### Day 5–7 — Parallel Explosion
- **M1:** T-15 Command pattern
- **M2:** T-11 Database + T-12 DAO core
- **M3:** T-10 Dashboard v1 (real GUI)
- **M4:** T-20 SecurityContext
- **M5:** Report §3 paragraphs as patterns commit

### Day 8–11 — Integration Work
- **M1:** T-16 Command logging + T-21 Decorator
- **M2:** T-13 Event logging + T-14 Extra DAOs (done Day 8)
- **M3:** T-18 GUI tabs
- **M4:** T-22 Safety rules + T-17 Schedule executor
- **M5:** Report §3 continued, §4 Alternative Designs

### Day 12 — Integration Day (all hands)
- **Everyone:** fix integration bugs, all-hands debugging

### Day 13–14 — Polish
- **M1:** T-23 Tests (backfill coverage)
- **M2:** Bug fixing, DB optimizations
- **M3:** GUI polish, final screenshots
- **M4:** Demo script runs (3×)
- **M5:** Report final polish, proofread

### Day 15 — Demo Video
- **Everyone:** rehearse, record, edit, submit by Day 16

---

## Blocker Matrix (who is holding whom)

If you're blocked, check who to poke:

| If you need... | Who's on the critical path? | Task ID |
|---|---|---|
| Device hierarchy | M1+M2 | T-02 |
| Hub (Singleton) | M1+M2 | T-03 |
| Observer working | M1+M2 | T-05 |
| Database ready | M2 | T-11 |
| DAOs available | M2 | T-12 |
| Event logging live | M2 | T-13 |
| Commands available | M1 | T-15 |
| SecurityContext | M4 | T-20 |
| GUI basic | M3 | T-10 |
| GUI tabs | M3 | T-18 |

---

## Integration Points (things to agree on early)

These interfaces/contracts should be settled Day 1 so everyone can mock them later:

### IP-1 — Device ID format
Devices need unique IDs that tie to DB rows AND to GUI elements.
**Decision:** UUID strings, generated by DeviceFactory. Stored as `id` attribute on Device, as PK in `devices` table.

### IP-2 — Observer event payload
When a Device notifies observers, what data does the observer receive?
**Decision:** `update(Device d, String event)` — pattern from the assignment PDF. `event` is a string like `"STATE_CHANGED"`, `"TURNED_ON"`, `"MOTION_DETECTED"`.

### IP-3 — Command-DB logging contract
Every Command execution is logged. What fields?
**Decision:** `(command_id, device_id, action, params_json, result, timestamp)`. M1 and M2 agree on this Day 5.

### IP-4 — SecurityContext API
What does SecureDevice call?
**Decision:** `SecurityContext.verifyPin(String deviceId)` → boolean. Current user tracked internally. M1 and M4 agree Day 5.

### IP-5 — Facade API surface
What methods does HomeController expose?
**Decision:** see `prompt_19_facade.md` — list finalized by M3 on Day 11.

---

## Commit Message Convention

Every commit references a task ID:
```
T-05: add Observer interface

- Observer.update(Device, String) matches assignment PDF
- Observable with attach/detach/notifyObservers
- Device now notifies on every setState call
```

This makes the git log match this task graph, and lets M5 trace what exists when writing the report.

---

# 3. Member Briefs

## M1 Brief

## Your Mission in One Line

Pair with M2 to build the 5 foundation patterns on Days 1–4, then own the **Command** and **Decorator** patterns plus comprehensive testing.

## Your Patterns
- **Command (#7)** — encapsulates every user action as a reversible object
- **Decorator (#9)** — wraps devices with security and logging features

## Your Tasks
- **T-01** to **T-09** — paired with M2 (foundation)
- **T-15** Command pattern + CommandInvoker
- **T-16** Command-to-DB logging integration
- **T-21** Decorator pattern
- **T-23** Unit tests for all patterns (shared with M4)

## Skills You Need
- Strong Java (interfaces, generics, polymorphism)
- JUnit 5 basics
- Understanding of OO design principles (Open-Closed, Single Responsibility)

## Who You Depend On
- **M2** — pairs with you on foundation; builds DAOs that your Commands will log to
- **M4** — builds SecurityContext that your SecureDevice decorator calls

## Who Depends On You
- **M3** — needs your Commands to wire the GUI buttons
- **M3** — needs your CommandInvoker to show Command History + Undo in UI

## Day-by-Day

### Day 1 (Apr 22) — Setup
- [ ] Install Java 17 + Maven + IntelliJ
- [ ] Install JavaFX SDK and Scene Builder (yes, even though you're backend — helps pair with M3)
- [ ] Pair with M2 to create the repo and run `prompt_01_project_skeleton.md`
- [ ] Verify `mvn compile` succeeds and a "Hello Smart Home" window opens
- [ ] Agree with M2 on pair programming schedule for Days 2–4

### Day 2 (Apr 23) — Foundation (paired with M2)
- [ ] Run `prompt_02_core_domain.md` — Device hierarchy
- [ ] Run `prompt_03_singleton_hub.md` — SmartHomeHub
- [ ] Run `prompt_04_iterator.md` — Room.devices() Enumeration
- [ ] Commit each as T-02, T-03, T-04
- [ ] End of day: 2 of 5 core patterns working

### Day 3 (Apr 24) — Foundation continued (paired with M2)
- [ ] Run `prompt_05_observer.md` — Observer pattern
- [ ] Run `prompt_06_abstract_factory.md` — DeviceFactory + 3 concretes
- [ ] End of day: 4 of 5 core patterns working

### Day 4 (Apr 25) — Finish foundation + gate (paired with M2)
- [ ] Run `prompt_07_strategy.md` — Strategy + 3 modes
- [ ] Run `prompt_08_javafx_skeleton.md` — minimal JavaFX shell
- [ ] Run `prompt_09_smoke_test.md` — CLI test verifying everything works
- [ ] 🚪 **GATE CHECK:** demonstrate all 5 patterns working to whole team
- [ ] If gate fails, pair extends to Day 5 — M3 and M4 should be told immediately

### Day 5–7 (Apr 26–28) — Command pattern
- [ ] Pair with M4 on Day 5 to lock in SecurityContext API (IP-4)
- [ ] Pair with M2 on Day 5 to lock in Command logging schema (IP-3)
- [ ] Run `prompt_15_command.md` — full Command pattern
- [ ] Self-test: can you execute a TurnOnCommand and then undo it?
- [ ] Commit as T-15

### Day 8–9 (Apr 29–30) — Command logging
- [ ] M2's `CommandsLogDAO` should be ready by Day 8 end
- [ ] Run `prompt_16_command_logging.md` — hook CommandInvoker to DAO
- [ ] Test: execute 3 commands, check the DB has 3 rows
- [ ] Commit as T-16

### Day 9–11 (Apr 30 – May 2) — Decorator pattern
- [ ] M4's SecurityContext should be ready by Day 7
- [ ] M2's DeviceEventDAO should be ready by Day 8
- [ ] Run `prompt_21_decorator.md` — SecureDevice + LoggedDevice + RateLimitedDevice
- [ ] Test: wrap a Light in SecureDevice — actions should require PIN
- [ ] Commit as T-21

### Day 12 (May 3) — Integration Day
- [ ] Full team on-site or on call
- [ ] Fix any integration bugs involving your code
- [ ] Help M3 wire Commands into GUI buttons

### Day 13–14 (May 4–5) — Testing
- [ ] Run `prompt_23_tests.md` — expand test coverage
- [ ] Target: ≥ 70% coverage on all your code
- [ ] Integration tests: full round trips (GUI click → Command → Device → Observer → DB)

### Day 15 (May 6) — Demo support
- [ ] Be available during video recording for demos of Command undo and Decorator security

### Day 16 (May 7) — Buffer

## Hard Stops
- **End of Day 4:** if foundation not working, pair extends — don't start Command yet
- **End of Day 8:** if Command basic execute/undo not working, drop `RateLimitedDevice` from Decorator scope
- **End of Day 11:** if Decorator not working, drop to just Command — 8 patterns still solid

## Common Mistakes to Avoid
- Making Commands mutable (they should be immutable value objects)
- Forgetting to push to undo stack after execute
- Using `instanceof` in CommandInvoker (should be polymorphic)
- Wrapping devices in decorators after they've been registered with Observer listeners — wrap first, then register
- Calling SecurityContext inside Observer callbacks (causes lockout issues)

## Files You'll Create

```
src/main/java/command/
├── DeviceCommand.java        (interface)
├── TurnOnCommand.java
├── TurnOffCommand.java
├── SetBrightnessCommand.java
├── UnlockCommand.java
├── LockCommand.java
├── SetTempCommand.java
├── SetAutomationModeCommand.java
└── CommandInvoker.java       (holds undo stack)

src/main/java/devices/decorator/
├── DeviceDecorator.java      (abstract)
├── SecureDevice.java
├── LoggedDevice.java
└── RateLimitedDevice.java    (if time)

src/test/java/
├── command/CommandInvokerTest.java
├── command/TurnOnCommandTest.java
├── ... (one test class per command)
├── devices/decorator/SecureDeviceTest.java
└── patterns/SingletonTest.java + others
```

---

## M2 Brief

## Your Mission in One Line

Pair with M1 to build the 5 foundation patterns on Days 1–4, then own the SQLite database and all 6 DAOs.

## Your Pattern
- **DAO (#6)** — clean separation between domain and persistence

## Your Tasks
- **T-01** to **T-09** — paired with M1 (foundation)
- **T-11** SQLite schema + Database connection Singleton
- **T-12** DAO base + UserDAO + RoomDAO + DeviceDAO
- **T-13** DeviceEventDAO + Observer→DB hook
- **T-14** ScheduleDAO + CommandsLogDAO

## Skills You Need
- Strong Java
- SQL basics (CREATE TABLE, INSERT, SELECT, JOIN)
- JDBC basics
- Awareness of thread safety (JDBC connections are not thread-safe)

## Who You Depend On
- **M1** — pairs with you on foundation; consumes your DAOs for command logging
- **M4** — safety rules will read event data from your DAOs

## Who Depends On You
- **M1** — needs CommandsLogDAO for command history
- **M3** — needs all DAOs for the GUI tabs (Event History, User Mgmt, etc.)
- **M4** — needs ScheduleDAO and DeviceEventDAO for safety rules and schedules

## Day-by-Day

### Day 1 (Apr 22) — Setup
- [ ] Install Java 17 + Maven + IntelliJ + SQLite CLI
- [ ] Install JavaFX SDK (same as M1, helps with pair programming)
- [ ] Pair with M1 to create the repo and run `prompt_01_project_skeleton.md`
- [ ] Sketch the SQLite schema on paper — 6 tables: users, rooms, devices, device_events, schedules, commands_log
- [ ] Share schema draft with team in WhatsApp for feedback
- [ ] Agree with M1 on pair programming schedule

### Day 2–4 — Foundation (paired with M1)
Same tasks as M1. You're driving/reviewing alternately.

### Day 5–6 (Apr 26–27) — Database foundation
- [ ] Run `prompt_11_database.md` — creates schema.sql, Database.java Singleton connection manager
- [ ] First-run behavior: if `smarthome.db` doesn't exist, create it and run schema.sql
- [ ] Thread safety: synchronize all DAO methods OR use a connection pool (simpler: synchronize)
- [ ] Test: delete smarthome.db, run app → new DB created automatically

### Day 6–7 (Apr 27–28) — DAO core
- [ ] Run `prompt_12_daos_core.md` — BaseDAO + UserDAO + RoomDAO + DeviceDAO
- [ ] Write tests that use in-memory SQLite: `jdbc:sqlite::memory:`
- [ ] Verify all CRUD operations work

### Day 7–8 (Apr 28–29) — Event logging DAO
- [ ] Agree with M1 on Command logging contract (IP-3) — Day 5 latest
- [ ] Run `prompt_13_event_logging.md` — DeviceEventDAO + auto-insert on Observer notifications
- [ ] Key integration: when `Device.notifyObservers()` fires, a row goes into `device_events` table
- [ ] Test: toggle a device, verify DB has a new row

### Day 8 (Apr 29) — Extra DAOs
- [ ] Run `prompt_14_daos_extra.md` — ScheduleDAO + CommandsLogDAO
- [ ] Notify M1 that CommandsLogDAO is ready → unblocks T-16

### Day 9–11 (Apr 30 – May 2) — Support + bug fixing
- [ ] Help M3 connect GUI tabs to DAOs
- [ ] Help M4 with schedule queries
- [ ] Performance check: can you insert 10,000 events and query recent 100 in under 200ms?

### Day 12 (May 3) — Integration Day
- [ ] Full team on-site or on call
- [ ] Debug DB threading issues if any arise

### Day 13–14 (May 4–5) — Testing + Polish
- [ ] Integration tests: DB survives app restart
- [ ] Backup feature (bonus): `smarthome.db.backup` file when schedule runs

### Day 15 (May 6) — Demo support

### Day 16 (May 7) — Buffer

## Hard Stops
- **End of Day 6:** if Database Singleton not connecting, pair with M3/M4 — this blocks everyone
- **End of Day 8:** if DAOs broken, M1 falls back to in-memory command history (no persistence) — acceptable but loses a feature
- **End of Day 11:** DB performance issues — add indexes on (`device_id`, `timestamp`) columns

## Common Mistakes to Avoid
- Forgetting `synchronized` on DAO methods → race conditions under concurrent GUI events
- Opening a new connection per DAO call → slow (reuse one connection)
- SQL injection via string concatenation → always use PreparedStatement
- Not closing ResultSet/Statement → resource leaks (use try-with-resources)
- Using `executeQuery()` for INSERT/UPDATE (should be `executeUpdate()`)

## Files You'll Create

```
src/main/resources/
├── schema.sql
└── seed.sql

src/main/java/persistence/
├── Database.java             (Singleton connection manager)
└── dao/
    ├── BaseDAO.java          (abstract, shared helpers)
    ├── UserDAO.java
    ├── RoomDAO.java
    ├── DeviceDAO.java
    ├── DeviceEventDAO.java
    ├── ScheduleDAO.java
    └── CommandsLogDAO.java

src/test/java/persistence/dao/
├── UserDAOTest.java
├── RoomDAOTest.java
├── ... (one test class per DAO)
```

## Schema Preview (for your reference)

```sql
CREATE TABLE users (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    name       TEXT NOT NULL UNIQUE,
    pin_hash   TEXT NOT NULL,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE rooms (
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE devices (
    id       TEXT PRIMARY KEY,         -- UUID
    name     TEXT NOT NULL,
    type     TEXT NOT NULL,            -- 'LIGHT', 'THERMOSTAT', ...
    room_id  INTEGER NOT NULL,
    config   TEXT,                     -- JSON blob
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

CREATE TABLE device_events (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id  TEXT NOT NULL,
    event_type TEXT NOT NULL,
    old_state  TEXT,
    new_state  TEXT,
    timestamp  TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES devices(id)
);
CREATE INDEX idx_events_device ON device_events(device_id);
CREATE INDEX idx_events_time   ON device_events(timestamp);

CREATE TABLE schedules (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    name          TEXT NOT NULL,
    cron_expr     TEXT NOT NULL,       -- 'HH:MM' or 'DAY HH:MM'
    mode_to_apply TEXT NOT NULL,       -- 'ECO', 'SLEEP', 'AWAY'
    enabled       INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE commands_log (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    command_id  TEXT NOT NULL,         -- UUID
    device_id   TEXT,
    action      TEXT NOT NULL,
    params_json TEXT,
    result      TEXT,
    timestamp   TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_cmdlog_time ON commands_log(timestamp);
```

---

## M3 Brief

## Your Mission in One Line

Own the entire user-facing experience: a modern JavaFX dashboard with FXML layouts, CSS styling, and a clean Facade that isolates the GUI from the backend complexity.

## Your Pattern
- **Facade (#8)** — single entry point that hides Hub + CommandInvoker + DAOs behind one controller

## Your Tasks
- **T-10** Dashboard v1 (rooms, devices, toggle buttons)
- **T-18** GUI tabs (Event History, Command History, User Management, Schedule Editor)
- **T-19** Facade HomeController + refactor GUI to use it

## Skills You Need
- Java basics
- **JavaFX** — FXML, Controllers, Scene Builder, CSS
- Willingness to learn fast if the team decided on JavaFX and you haven't used it heavily

## Who You Depend On
- **M1+M2** — foundation must exist (Day 4)
- **M2** — DAOs for data tabs (by Day 8)
- **M1** — CommandInvoker for Command History + Undo (by Day 8)

## Who Depends On You
- Nobody technically — but the team depends on you to make the demo look amazing

## Day-by-Day

### Day 1 (Apr 22) — JavaFX crash course + setup
- [ ] Install Java 17 + Maven + IntelliJ
- [ ] Install **JavaFX SDK 21** from gluonhq.com
- [ ] Install **Scene Builder** from gluonhq.com
- [ ] Configure IntelliJ: Run Configuration VM options:
  ```
  --module-path /path/to/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml
  ```
  Or if using Maven (recommended): add `javafx-maven-plugin`, IDE handles the rest
- [ ] Run any "Hello JavaFX" tutorial — verify a window opens
- [ ] Tutorial: https://docs.oracle.com/javafx/2/get_started/hello_world.htm
- [ ] Sketch wireframes on paper: main window, device cards, 4 tabs you'll build
- [ ] Share wireframes in WhatsApp

### Day 2–3 (Apr 23–24) — More JavaFX practice
- [ ] Build a throwaway FXML file in Scene Builder: a window with 2 tabs, each with a label and a button
- [ ] Wire up a Controller class that handles button clicks
- [ ] Apply a basic CSS file — change background, button colors
- [ ] Read the Day 2 and Day 3 commits from M1+M2 — understand the Device hierarchy and Observer

### Day 4 (Apr 25) — Prep for Day 5
- [ ] Review `prompt_10_dashboard_v1.md`
- [ ] M1+M2 should ship `prompt_08_javafx_skeleton.md` today — a minimal FXML + Controller the app boots into
- [ ] Pull latest, verify their skeleton runs
- [ ] Plan your Day 5 FXML files

### Day 5–7 (Apr 26–28) — Dashboard v1
- [ ] Run `prompt_10_dashboard_v1.md`
- [ ] Deliverables:
  - [ ] Main window with a left sidebar listing rooms
  - [ ] Click a room → center panel shows its devices
  - [ ] Each device = a card with icon, name, state label, toggle button
  - [ ] A bottom bar showing automation mode selector (Eco/Sleep/Away buttons)
  - [ ] Right panel with live notifications (subscribes to Observer)
- [ ] Styling: use CSS file with a "modern minimalist" look — rounded corners, subtle shadows
- [ ] Test end-to-end: click toggle → device changes → Observer fires → notification appears
- [ ] **Critical:** ALL long operations on background threads — use `Task<V>` and `Platform.runLater(...)` for UI updates

### Day 8–11 (Apr 29 – May 2) — GUI tabs
- [ ] M2's DAOs should be ready Day 8
- [ ] M1's CommandInvoker should be ready Day 8
- [ ] Run `prompt_18_gui_tabs.md` — builds 4 new tabs:
  - [ ] **Event History tab** — TableView, auto-refreshes every 2s from DeviceEventDAO
  - [ ] **Command History tab** — TableView showing CommandInvoker.history() with per-row Undo button
  - [ ] **User Management tab** — form to add/remove users, change PINs (via UserDAO)
  - [ ] **Schedule Editor tab** — form to create/delete schedules (via ScheduleDAO)

### Day 11–12 (May 2–3) — Facade pattern
- [ ] Day 11 morning: agree with team on HomeController API surface (IP-5)
- [ ] Run `prompt_19_facade.md`
- [ ] Create `facade/HomeController.java` that:
  - Holds references to Hub, CommandInvoker, all DAOs, SecurityContext
  - Exposes high-level methods: `toggleDevice(deviceId)`, `setMode(ModeName)`, `addUser(name, pin)`, `recentEvents()`, `undoLastCommand()`, `createSchedule(...)`
- [ ] **Refactor every GUI Controller to use HomeController ONLY**
  - No `SmartHomeHub.getInstance()` calls in GUI code
  - No direct DAO access in GUI code
  - No direct CommandInvoker access in GUI code
- [ ] This is the critical moment — grep for leaks

### Day 12 (May 3) — Integration Day
- [ ] Full team on-site
- [ ] Fix GUI integration bugs
- [ ] Ensure all pattern integrations visible in UI

### Day 13–14 (May 4–5) — Polish
- [ ] Final CSS pass — colors, spacing, typography, icons
- [ ] Icons: use FontAwesome for JavaFX (`org.kordamp.ikonli:ikonli-fontawesome5-pack`)
- [ ] Accessibility: tab order, keyboard shortcuts for primary actions
- [ ] Click every button, trigger every feature — no exceptions in console
- [ ] Screenshots for M5's report (high resolution)

### Day 15 (May 6) — Demo video
- [ ] Walk through every tab during recording

### Day 16 — Buffer

## Hard Stops
- **End of Day 3:** if JavaFX setup not working, ask for help — M1 or M2 can pair with you for an hour
- **End of Day 7:** if Dashboard v1 not showing rooms/devices, drop CSS styling to save time — ugly-but-working beats pretty-but-broken
- **End of Day 11:** if GUI tabs all behind schedule, drop Schedule Editor first (keep Event + Command + User Mgmt)
- **End of Day 12:** if HomeController facade incomplete, GUI keeps talking to Hub directly. Pattern #8 drops from report → you have 8 patterns, still solid

## Common Mistakes to Avoid
- **Long DB/DAO calls on the JavaFX Application Thread** — causes the whole GUI to freeze. Use `Task<T>`.
- **Updating UI from a background thread** — must use `Platform.runLater(() -> ...)` 
- **Mixing FXML-loaded controllers with manually-constructed ones** — pick one approach and stick
- **Using JavaFX's `ObservableValue` or `ChangeListener` as your "Observer pattern implementation"** — doesn't count for the assignment. You must wire into the project's own `Observer` interface.
- **CSS class selectors not matching** — JavaFX CSS is quirky, check with Scene Builder's "CSS Analyzer"
- **Loading FXML with wrong path** — always `getClass().getResource("/fxml/Dashboard.fxml")` with leading slash

## Files You'll Create

```
src/main/resources/fxml/
├── Dashboard.fxml
├── DeviceCard.fxml
├── EventHistoryTab.fxml
├── CommandHistoryTab.fxml
├── UserManagementTab.fxml
└── ScheduleEditorTab.fxml

src/main/resources/css/
└── smarthome.css

src/main/java/gui/
├── DashboardController.java
├── DeviceCardController.java
├── EventHistoryController.java
├── CommandHistoryController.java
├── UserManagementController.java
└── ScheduleEditorController.java

src/main/java/facade/
└── HomeController.java
```

## JavaFX Observer Integration — Important

The project's Observer pattern (graded) must use YOUR `Observer`/`Observable` interfaces from the `observer/` package. JavaFX has its own observer system but **do NOT** claim that as your implementation of the pattern.

In a GUI controller, the correct pattern:

```java
public class DashboardController implements Observer {

    @Override
    public void update(Device d, String event) {
        // This method is called on whatever thread fired the notification
        // Must bounce to JavaFX Application Thread before touching UI:
        Platform.runLater(() -> {
            notificationList.getItems().add(d.getName() + ": " + event);
        });
    }

    @FXML
    public void initialize() {
        // Register self as observer for all devices
        homeController.allDevices().forEach(device -> device.attach(this));
    }
}
```

This way the Observer pattern is cleanly implemented by YOUR interfaces, and the JavaFX thread safety is handled correctly.

---

## M4 Brief

## Your Mission in One Line

Build the behind-the-scenes logic that makes the system SAFE: PIN-protected access, motion/temperature safety rules, scheduled automations, and the integration testing that ties it all together.

## Your Pattern
- No new pattern — you're the integration glue and safety layer

## Your Tasks
- **T-20** SecurityContext (login + PIN + lockout)
- **T-22** Safety rules engine
- **T-17** ScheduleExecutor (background thread)
- **T-23** Unit tests for all patterns (shared with M1)

## Skills You Need
- Defensive Java programming
- JUnit 5
- Threads basics (`ScheduledExecutorService`)
- Mindset of "what breaks this?"

## Who You Depend On
- **M1+M2 foundation** — must exist (Day 4)
- **M2** — DeviceEventDAO and ScheduleDAO for your rules + schedule engine

## Who Depends On You
- **M1** — SecureDevice decorator needs your SecurityContext (IP-4)
- Test infrastructure — everyone benefits from your tests

## Day-by-Day

### Day 1 (Apr 22) — Team infrastructure
- [ ] Install Java 17 + Maven + IntelliJ
- [ ] Install Docker Desktop (not strictly needed for this project anymore, but useful)
- [ ] Install SQLite CLI (`sqlite3` on Linux/Mac, `.exe` on Windows)
- [ ] Take ownership of Git workflow:
  - [ ] Verify all 5 members have push access
  - [ ] Create branch per member: `m1`, `m2`, `m3`, `m4`, `m5`
  - [ ] Write `.gitignore` covering `target/`, `.idea/`, `*.db`, `*.class`
  - [ ] Write minimal `CONTRIBUTING.md` — commit convention, branch naming, PR rule
- [ ] Sketch safety rules on paper:
  - [ ] Rule 1: motion + Away mode → lock + lights on
  - [ ] Rule 2: temperature > 40°C → red alert
  - [ ] Rule 3: door unlocked > 5 min → warning
- [ ] Share rule list in WhatsApp

### Day 2–4 — Prep (can't code yet, foundation not done)
- [ ] Read each foundation commit as M1+M2 push
- [ ] Read the Observer pattern carefully — your safety rules will SUBSCRIBE to device events
- [ ] Draft test plans for each safety rule
- [ ] Help M5 with the UML diagram if you have spare time

### Day 5 (Apr 26) — Agree on API with M1
- [ ] Pair with M1 briefly to lock in SecurityContext API surface (IP-4):
  - `boolean login(String username, String pin)`
  - `void logout()`
  - `User currentUser()`
  - `boolean verifyPinForAction()` — returns true if a user is logged in AND not locked out
  - 3 wrong PIN attempts → 30-second lockout (timestamp-based)
- [ ] Document in shared doc or WhatsApp pin

### Day 5–7 (Apr 26–28) — SecurityContext
- [ ] Run `prompt_20_security_context.md`
- [ ] PIN storage: hash with SHA-256 (simple, sufficient for this project; NOT bcrypt — avoid extra deps)
- [ ] Lockout: after 3 failed attempts within 60 seconds, lock user for 30 seconds
- [ ] Singleton or regular class? → regular class, held by HomeController
- [ ] Unit tests:
  - Correct PIN → login succeeds
  - Wrong PIN → login fails
  - 3 wrong → locked for 30s
  - After 30s → can try again
- [ ] Notify M1 you're done — unblocks SecureDevice decorator

### Day 7–11 (Apr 28 – May 2) — Safety rules engine
- [ ] Run `prompt_22_safety_rules.md`
- [ ] Pattern: `SafetyRulesEngine` implements `Observer`, subscribes to all devices
- [ ] On each `update(Device, String)` call, evaluate every rule
- [ ] Each rule is a pluggable class (can disable/enable per rule)
- [ ] Rule 1: PIR motion detected AND Hub in Away mode → auto-lock all doors, all lights ON
- [ ] Rule 2: Thermostat reads > 40°C → trigger red alert notification
- [ ] Rule 3: DoorLock unlocked > 5 minutes → warning
- [ ] Unit tests: simulate each scenario, verify correct behavior
- [ ] Do NOT register rules if `config.properties` has `safety.enabled=false`

### Day 9–11 (Apr 30 – May 2) — Schedule executor
- [ ] Run `prompt_17_schedule_executor.md`
- [ ] Uses `ScheduledExecutorService` with 1-minute tick interval
- [ ] Every tick: query `ScheduleDAO.findEnabled()`, match current time against schedule's cron expression
- [ ] Simple cron: `HH:MM` (daily) or `MON 22:00` (weekly)
- [ ] When a schedule matches, apply the associated AutomationMode
- [ ] Log each execution to `device_events` table
- [ ] Important: prevent double-firing — track last-fired timestamp per schedule

### Day 11–12 (May 2–3) — Integration testing
- [ ] Write the full demo script (click-by-click steps for the video)
- [ ] Run the demo script 3 times — each must succeed
- [ ] Test app restart persistence (kill app, restart, verify state)
- [ ] Test multi-user PIN flow
- [ ] Test each safety rule manually
- [ ] Test Undo end-to-end (command → undo → state restored, DB row updated)

### Day 12 (May 3) — Integration Day (all hands)
- [ ] You are the "integration QA lead" today
- [ ] Keep the bug list, priority-ordered
- [ ] Pair with whoever has blockers

### Day 13–14 (May 4–5) — Test coverage + polish
- [ ] Run `prompt_23_tests.md` (shared with M1)
- [ ] Ensure existing patterns (Singleton, Factory, etc.) have unit tests — they're graded
- [ ] Target: ≥ 60% project-wide coverage (some UI code is hard to test — that's fine)

### Day 15 (May 6) — Demo support

### Day 16 (May 7) — Buffer

## Hard Stops
- **End of Day 7:** if SecurityContext not done, M1's SecureDevice can't work. Pair with M1 immediately.
- **End of Day 11:** if safety rules not done, list as "future work" in report. Feature is bonus.
- **End of Day 11:** if ScheduleExecutor not done, Schedule Editor in GUI becomes a "configuration saved but not executed" feature. Still shows DAO integration.
- **End of Day 14:** if any demo dry run fails, that's the ONLY priority Day 15.

## Common Mistakes to Avoid
- PIN comparison using `==` instead of `.equals()` / `MessageDigest.isEqual()`
- Not resetting the failed-attempt counter on successful login
- Safety rules causing infinite loops (a rule triggers a device change which triggers the rule again)
  - **Fix:** rules should check "is this change already the rule's intended state?" before acting
- Schedule firing twice in the same minute
  - **Fix:** track last fired timestamp per schedule, require ≥ 55-second gap
- ScheduledExecutorService not shutting down on app exit → JVM won't exit
  - **Fix:** daemon thread factory OR explicit shutdown hook

## Files You'll Create

```
src/main/java/security/
└── SecurityContext.java

src/main/java/safety/
├── SafetyRulesEngine.java
├── SafetyRule.java           (interface)
├── MotionInAwayModeRule.java
├── HighTemperatureRule.java
└── DoorUnlockedTooLongRule.java

src/main/java/schedule/
└── ScheduleExecutor.java

src/test/java/
├── security/SecurityContextTest.java
├── safety/*Test.java
└── patterns/  (singleton, factory, etc — test the graded patterns)
```

## Git Workflow You Own

You're the unofficial team lead for git discipline. Quick rules to enforce:
- `main` branch protected — PR required
- PRs need at least one reviewer
- Commit messages start with task ID: `T-20: add SecurityContext`
- Do not push broken code to `main`
- Tag after each major milestone: `git tag -a v0.foundation -m "5 core patterns working"`

Flag anyone violating these in the daily standup. Be polite, be firm.

---

## M5 Brief

## Your Mission in One Line

Own everything the professor actually grades on paper: UML, 5-page report, README, and the demo video. You don't code; you make sure the team's work is legibly presented.

## Your Patterns
- None directly — but you document all 9

## Your Tasks
- **T-24** UML class diagram (v1 through v6)
- **T-25** Report sections §1 through §5
- **T-26** README.md + demo script
- **T-27** Demo video recording

## Skills You Need
- Good written English
- draw.io or PlantUML (both free)
- Basic video editing (any free tool: DaVinci Resolve, OpenShot, iMovie, CapCut)
- Project management instinct (track what's done so you can write about it)

## Who You Depend On
- **Every member** — you write about what they build
- Each member should tell you the day they commit a pattern, so you can write about it that day

## Who Depends On You
- Nobody technical — but the team depends on you to submit the professionally-presented final product

## Day-by-Day

### Day 1 (Apr 22) — Setup + UML v1
- [ ] Install draw.io Desktop (or use diagrams.net online — same tool)
- [ ] Alternative: if you prefer code-based UML, install PlantUML plugin for VS Code
- [ ] Read the assignment PDF carefully. Read the Airport example in it.
- [ ] Read `01_MASTER_PLAN.md` and `02_TASK_GRAPH.md` in full
- [ ] Create `docs/` folder in repo for all your outputs
- [ ] Start UML v1: just the 5 core patterns (what M1+M2 will build Days 2–4):
  - Device (abstract), Light, Thermostat, SecurityCamera, DoorLock
  - Room with `devices()` method ← mark `<<Iterator>>`
  - SmartHomeHub with `getInstance()` ← mark `<<Singleton>>`
  - Observer interface + User implementing ← mark `<<Observer>>`
  - DeviceFactory + 3 concretes ← mark `<<AbstractFactory>>`
  - AutomationMode + 3 modes ← mark `<<Strategy>>`
- [ ] Save as `docs/uml_v1.png`

### Day 2 (Apr 23) — Report outline + §1
- [ ] Create `docs/report.md` (we'll convert to PDF at the end)
- [ ] Structure:
  ```
  §1 System Description                    (½ page)
  §2 Class Diagram & Component Explanation (1 page)
  §3 Design Patterns                       (2–2.5 pages, 9 patterns × ~270 words)
  §4 Alternative Designs                   (1 page)
  §5 Justification                         (½ page)
  ```
- [ ] Write §1 first. Template:
  - What the system does (1 paragraph)
  - User functionalities (bulleted list — see example on page 3 of assignment PDF)
  - Constraints addressed (modularity, safety, UX)

### Day 3 (Apr 24) — §2 + UML v2
- [ ] Write §2 based on UML v1 — explain each class's role and how it meets assignment constraints
- [ ] Update UML as M1+M2 land Observer and Abstract Factory today
- [ ] Save as `docs/uml_v2.png`

### Day 4 (Apr 25) — UML v3
- [ ] M1+M2 complete Strategy today
- [ ] Update UML to include Strategy classes
- [ ] Save as `docs/uml_v3.png`

### Day 5–9 (Apr 26–30) — Report §3 as patterns land
- [ ] Write each pattern's paragraph the day it's committed
- [ ] Template (~270 words per pattern):
  1. What the pattern is (brief, textbook definition)
  2. Where it lives in our code (class names, methods)
  3. Why we chose it (tie to assignment constraint)
  4. Example of usage (1-2 sentences)
- [ ] Patterns land in this order typically:
  - Day 2: Singleton, Iterator
  - Day 3: Observer, Abstract Factory
  - Day 4: Strategy
  - Day 6: Database (DAO)
  - Day 8: Command
  - Day 9: Extra DAOs finalized
  - Day 11: Decorator
  - Day 12: Facade
- [ ] Update UML to include persistence, command, decorator, facade packages (v4, v5, v6)

### Day 10–11 (May 1–2) — §4 Alternative Designs
- [ ] Use `alternative_designs_guide.md` as starting content
- [ ] Alternative A: Observer Push vs Pull
  - 2 paragraphs explaining
  - 4×2 trade-off table (performance, extensibility, cost, maintainability)
  - Justification paragraph
- [ ] Alternative B: DAO vs Active Record
  - Same structure
- [ ] Total: ~500 words, ~1 page

### Day 12 (May 3) — §5 + Integration Day
- [ ] Write §5 Justification — summary paragraph stitching Alternatives + key constraints
- [ ] Present at Integration Day: walk team through the report draft
- [ ] Collect feedback, incorporate

### Day 13 (May 4) — Full report polish
- [ ] Proofread, proofread, proofread
- [ ] Trim to exactly 5 pages
- [ ] Font: Times New Roman 11pt or Calibri 11pt, 1.15 line spacing
- [ ] Convert to PDF (LibreOffice or Word)
- [ ] Save as `docs/report_v1.pdf`

### Day 14 (May 5) — README + demo script
- [ ] Write README.md with:
  - What the project is (1 paragraph)
  - Requirements (Java 17, Maven, etc.)
  - Build instructions (`mvn clean package`)
  - Run instructions (`mvn javafx:run` or `java -jar ...`)
  - First-run behavior (DB auto-created, default user PIN 1234)
  - Pattern inventory table (pointing to file paths)
- [ ] Write demo script with M4's help:
  - 30s: open app, show main dashboard, list rooms/devices
  - 30s: toggle some devices, show notifications, show Event History populating
  - 30s: show Command History, click Undo
  - 30s: switch to Eco mode, show devices reacting
  - 20s: show User Management, PIN entry on secure device
  - 10s: closing summary
- [ ] Total: ~2 minutes
- [ ] Collect final screenshots from M3 (high resolution)

### Day 15 (May 6) — Demo video
- [ ] Setup: screen recorder (OBS is free, cross-platform)
- [ ] Record with M1/M3/M4 present for cues
- [ ] Multiple takes — pick the best
- [ ] Edit: titles, transitions, smooth cuts
- [ ] Add background music (royalty-free, low volume — from YouTube Audio Library)
- [ ] Export as MP4, 1080p, under 100MB if possible
- [ ] Save as `docs/demo_video.mp4`

### Day 16 (May 7) — Final check + submit
- [ ] Final submission package:
  - [ ] Source code ZIP
  - [ ] `report.pdf` (5 pages exactly)
  - [ ] `uml_final.png` (high resolution)
  - [ ] `demo_video.mp4`
  - [ ] `README.md`
- [ ] Verify with team everything works by pulling fresh from main and running
- [ ] Submit by May 8 deadline (don't wait for midnight)

## Hard Stops
- **End of Day 4:** if §1 + §2 not drafted, you're behind. Ask M4 for an hour of help.
- **End of Day 11:** if report first draft not done, cut Alternative B from §4, use only Observer Push vs Pull.
- **End of Day 14:** if video not recorded, submit with annotated screenshots instead. Flag this to the team on Day 13 as a risk.

## Common Mistakes to Avoid
- Writing §3 patterns based on what you THINK the code does — always check the actual commits
- UML diagram with too much detail (private methods, internal helpers) — show the design, not every line
- Trade-off tables that all favor the chosen design → looks rigged. Include at least one dimension where the rejected option wins.
- Adding a 6th section to the report "for completeness" → exceeds the 5-page limit, professor deducts points
- Report in past tense only ("we did this") — mix with present tense for architecture ("the Hub provides...")
- Demo video with audio issues → use a headset mic, not laptop mic

## UML Conventions to Follow

| Relationship | UML notation | Example |
|---|---|---|
| Inheritance | Solid line, empty triangle arrowhead | Light extends Device |
| Implementation | Dashed line, empty triangle arrowhead | User implements Observer |
| Composition | Solid line, filled diamond | Hub composes Rooms (Room can't exist without Hub) |
| Aggregation | Solid line, empty diamond | Room aggregates Devices |
| Association | Simple solid line | User knows about Device |
| Dependency | Dashed line with arrow | Controller uses HomeController |

Stereotype tags go above the class name, like:
```
«Singleton»
SmartHomeHub
```

## Files You'll Produce

```
docs/
├── uml_v1.png     (Day 1)
├── uml_v2.png     (Day 3)
├── uml_v3.png     (Day 4)
├── uml_v4.png     (Day 6, after DAO)
├── uml_v5.png     (Day 8, after Command)
├── uml_v6.png     (Day 12, FINAL — all 9 patterns + DB)
├── uml_final.png  (copy of v6, this is what ships)
├── report.md      (source)
├── report.pdf     (FINAL submission)
├── demo_script.md
└── demo_video.mp4
```

## Templates & Starting Content

- Alternative Designs section is already 90% written in `alternative_designs_guide.md` — copy-paste as base, tweak wording
- Pattern descriptions (for §3) — ask each member for a short paragraph the day they commit; you then polish
- System description — use Airport example from assignment PDF as structural template

## Collaboration Tips

- **Ask for help writing about code you don't fully understand.** Each member should give you 2-3 sentences on their pattern — you polish.
- **Keep a "pattern landed" checklist.** Every time someone commits a major pattern, check it off and write the paragraph that day.
- **Run the report draft past the team on Day 12.** Better to catch errors before final polish.

---

# 4. Agent Prompts

Run in order. Each prompt is ready to paste into Claude Code (recommended), Cursor, or any coding agent.

---

# Prompt 01 — Project Skeleton

**Task ID:** T-01
**Owner:** M1 + M2 (paired)
**Day:** 1
**Estimated time:** 1–2 hours
**Prerequisites:** None — this is the first task

---

## Paste this into Claude Code

```
You are helping me set up a Java 17 + JavaFX 21 + Maven project skeleton for a university assignment. Nothing exists yet.

PROJECT: Smart Home Automation System
GOAL OF THIS TASK: Get `mvn compile` and `mvn javafx:run` working, with the app opening a minimal "Smart Home — Starting" window and closing cleanly.

Create the following file structure:

smarthome/
├── pom.xml
├── .gitignore
├── README.md                (minimal, 1 paragraph)
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── Main.java
    │   │   ├── core/            (empty — placeholder)
    │   │   ├── devices/         (empty)
    │   │   ├── factory/         (empty)
    │   │   ├── observer/        (empty)
    │   │   ├── strategy/        (empty)
    │   │   ├── command/         (empty)
    │   │   ├── facade/          (empty)
    │   │   ├── persistence/
    │   │   │   └── dao/         (empty)
    │   │   ├── security/        (empty)
    │   │   ├── safety/          (empty)
    │   │   ├── schedule/        (empty)
    │   │   └── gui/             (empty)
    │   └── resources/
    │       ├── fxml/
    │       │   └── MainWindow.fxml
    │       └── css/
    │           └── smarthome.css
    └── test/
        └── java/
            └── (empty — populated later)

REQUIREMENTS:

1. pom.xml:
   - Group: smarthome, Artifact: smart-home, Version: 1.0.0-SNAPSHOT
   - Java 17
   - JavaFX 21 dependencies: javafx-controls, javafx-fxml
   - Use org.openjfx:javafx-maven-plugin so `mvn javafx:run` works
   - Main class pointed at `Main`
   - UTF-8 source encoding

2. Main.java:
   - Extends javafx.application.Application
   - start(Stage) loads /fxml/MainWindow.fxml
   - Applies /css/smarthome.css stylesheet
   - Shows window titled "Smart Home" sized 1000x700
   - Main method calls launch(args)
   - Include a top-of-file comment: "// ENTRY POINT — JavaFX application bootstrap"

3. MainWindow.fxml:
   - BorderPane as root
   - Center: a Label "Smart Home — Starting" with id #statusLabel
   - Specify fx:controller="gui.MainWindowController" (but DO NOT create the controller yet — we'll stub it next)

4. MainWindowController.java (create this too under gui/):
   - Empty controller class with @FXML private Label statusLabel
   - @FXML public void initialize() method that sets statusLabel.setText("Smart Home — Ready")

5. smarthome.css:
   - One sample style: .root { -fx-background-color: #f0f4f8; }
   - Label style: -fx-font-size: 18px; -fx-text-fill: #1a365d;

6. .gitignore:
   - target/
   - .idea/
   - *.iml
   - .vscode/
   - *.db
   - *.class
   - config.properties

7. README.md:
   - Project name, 1-sentence description
   - "Build: mvn clean package"
   - "Run: mvn javafx:run"
   - "Requires: Java 17, Maven 3.8+"

AFTER CREATING FILES:
- Run `mvn compile` and verify it succeeds
- Report the command output
- DO NOT run `mvn javafx:run` yourself — the user will do that to see the window

Use idiomatic Java 17 syntax (var, text blocks where appropriate). Comment every file with a brief purpose statement at the top.
```

---

## Acceptance Criteria

Before committing, verify:
- [ ] `mvn compile` passes with zero errors and zero warnings
- [ ] `mvn javafx:run` opens a window showing "Smart Home — Ready"
- [ ] Closing the window terminates the JVM cleanly (no hanging threads)
- [ ] `git status` shows only the expected files (no IDE junk leaking through)

## Commit Message
```
T-01: project skeleton + JavaFX 21 + Maven + minimal FXML shell
```

## What This Unblocks

Every other task. Specifically:
- T-02 (core domain) can now be created in `devices/`
- M3 now has a working FXML to reference
- Everyone can clone the repo and get a working app

---

# Prompt 02 — Core Domain: Device Hierarchy + Room

**Task ID:** T-02
**Owner:** M1 + M2 (paired)
**Day:** 2
**Estimated time:** 1.5 hours
**Prerequisites:** T-01 (project skeleton exists)

---

## Paste this into Claude Code

```
We have a Java 17 + JavaFX Maven project skeleton. I need you to create the core domain model for a Smart Home Automation System.

Context: this is a Software Component Design course project. Design patterns are graded. Comments in code must explicitly mark pattern implementations.

CREATE THESE FILES:

1. src/main/java/devices/Device.java
   - Abstract class
   - Private fields: id (String, UUID), name (String), room (Room reference), state (String — e.g. "ON", "OFF")
   - Constructor: takes name and Room — generates UUID for id
   - Getters for all fields
   - setState(String newState) — updates state AND triggers notifyObservers("STATE_CHANGED") — but the Observer part will be added in a later task. For now, just include a `protected void onStateChanged()` hook method that subclasses can override and that we'll wire later.
   - abstract String getType() — returns "LIGHT", "THERMOSTAT", etc.
   - Override toString() to show id, name, state
   - Class-level comment: "// DOMAIN: abstract base for all smart devices"

2. src/main/java/devices/Light.java
   - extends Device
   - Additional field: int brightness (0-100, default 100)
   - Methods: turnOn(), turnOff(), setBrightness(int level)
   - turnOn sets state="ON", turnOff sets state="OFF"
   - getType() returns "LIGHT"

3. src/main/java/devices/Thermostat.java
   - extends Device
   - Additional fields: double currentTemp (default 22.0), double setpoint (default 22.0)
   - Methods: setSetpoint(double celsius), updateReading(double temp)
   - State values: "ON" (heating/cooling) or "OFF" (idle)
   - getType() returns "THERMOSTAT"

4. src/main/java/devices/SecurityCamera.java
   - extends Device
   - Additional field: boolean motionDetected (default false)
   - Methods: triggerMotion() — sets motionDetected=true, state="MOTION_DETECTED"
   - clearMotion() — sets motionDetected=false, state="MOTION_CLEAR"
   - arm(), disarm()
   - getType() returns "SECURITY_CAMERA"

5. src/main/java/devices/DoorLock.java
   - extends Device
   - Additional field: String pin (default "1234" — this is a stub, will be replaced by SecurityContext later)
   - Methods: lock(), unlock(String providedPin)
   - unlock returns true/false; if success, state="UNLOCKED"
   - getType() returns "DOOR_LOCK"
   - TODO comment: "// PIN check stub — replace with SecurityContext in T-20"

6. src/main/java/core/Room.java
   - Private fields: id (int — simple counter, or UUID as String for consistency), name (String), devices (List<Device>)
   - Constructor: takes name, assigns id
   - Methods: addDevice(Device), removeDevice(Device), getName(), getId()
   - Method: `public java.util.Enumeration<Device> devices()` — returns an Enumeration over the devices list
     ★ THIS IS THE ITERATOR PATTERN — mark with a comment: "// ITERATOR PATTERN: returns Enumeration for iteration without exposing the underlying list"
   - Use java.util.Collections.enumeration(list) to create the Enumeration
   - Override toString()

IMPORTANT CONSTRAINTS:
- Do not import or use anything from packages that don't exist yet (observer, factory, etc.)
- Do not add Observer registration code yet — that comes in T-05
- Do not touch Main.java or any FXML files yet
- Use java.util.UUID.randomUUID().toString() for Device IDs
- All classes in the same package they already sit in
- Match exact package names from the existing structure

AFTER CREATING FILES:
- Run `mvn compile` and report the output
- Do not run tests yet — we'll add those later

Make sure every file has a purpose comment at the top, pattern tags where applicable, and JavaDoc on public methods.
```

---

## Acceptance Criteria

- [ ] `mvn compile` passes cleanly
- [ ] `Room.devices()` returns `java.util.Enumeration<Device>` (not `Iterator` — the assignment explicitly requires Enumeration)
- [ ] Each device has a clear `getType()` returning a distinct string constant
- [ ] No references to packages that don't exist yet (observer, factory, etc.) — we haven't created them
- [ ] Comments clearly mark the Iterator pattern location

## Manual Quick Test

Temporarily in Main.java, add this in `start()` BEFORE loading FXML:

```java
Room livingRoom = new Room("Living Room");
Light l1 = new Light("Ceiling Light", livingRoom);
Light l2 = new Light("Floor Lamp", livingRoom);
livingRoom.addDevice(l1);
livingRoom.addDevice(l2);

java.util.Enumeration<Device> e = livingRoom.devices();
while (e.hasMoreElements()) {
    Device d = e.nextElement();
    System.out.println(d);
}
```

Run. Verify console shows both lights. Then **revert the changes to Main.java** — this was just a sanity check.

## Commit Message
```
T-02: core domain — Device hierarchy + Room with Iterator pattern
```

## What This Unblocks

- T-03 Singleton Hub (needs Room)
- T-04 Iterator is already partially done in Room (marked separately for clarity)
- T-05 Observer (needs Device to hook into)
- T-06 Abstract Factory (creates these concrete devices)

---

# Prompt 03 — Singleton: SmartHomeHub

**Task ID:** T-03
**Owner:** M1 + M2 (paired)
**Day:** 2
**Prerequisites:** T-02

---

## Paste this into Claude Code

```
Create the SmartHomeHub class — the Singleton that manages the whole home.

Context: Smart Home Java project. The Device, Room classes already exist under `devices/` and `core/`. No other packages are implemented yet.

CREATE: src/main/java/core/SmartHomeHub.java

Requirements:
- SINGLETON PATTERN — must be clearly marked
- Private constructor (no subclassing, no outside instantiation)
- Static `getInstance()` method — thread-safe lazy initialization (double-checked locking OR synchronized method, pick one and comment why)
- Fields:
   - private final List<Room> rooms = new CopyOnWriteArrayList<>(); (thread-safe for concurrent access from GUI + background)
- Methods:
   - public void addRoom(Room r)
   - public boolean removeRoom(Room r)
   - public Enumeration<Room> rooms() — Iterator pattern on rooms too
   - public Room findRoomByName(String name)
   - public Device findDeviceById(String id) — iterates all rooms, all devices
   - public List<Device> allDevices() — flat list across rooms
   - public void shutdown() — placeholder for future cleanup (DB close, etc.)

PATTERN TAGS:
- Top of class: "// SINGLETON PATTERN: one central hub for the entire house"
- Above getInstance: "// Singleton access point — thread-safe lazy init"
- Above private constructor: "// Private constructor prevents external instantiation"

Also create a minimal unit test:
src/test/java/core/SmartHomeHubTest.java
- Test 1: getInstance() returns non-null
- Test 2: two calls to getInstance() return the SAME instance (==)
- Test 3: constructor is not accessible from outside (use reflection: getDeclaredConstructor, verify Modifier.isPrivate)
- Use JUnit 5 (org.junit.jupiter.api)

UPDATE pom.xml to add JUnit 5 dependency if not present:
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
</dependency>

AFTER CREATING:
- Run `mvn compile` — must pass
- Run `mvn test` — 3 Singleton tests must pass
- Report output
```

---

## Acceptance Criteria

- [ ] `mvn compile` passes
- [ ] `mvn test` → 3 Singleton tests pass
- [ ] `SmartHomeHub` constructor is private (verified by reflection test)
- [ ] `getInstance()` returns same instance always
- [ ] Pattern comments clearly visible

## Commit Message
```
T-03: SmartHomeHub Singleton pattern with unit tests
```

## What This Unblocks
T-05 Observer (uses Hub), T-06 Abstract Factory (Hub holds created devices), T-10 GUI (reads from Hub)

---

# Prompt 04 — Iterator Pattern Verification

**Task ID:** T-04
**Owner:** M1 + M2 (paired)
**Day:** 2
**Prerequisites:** T-02, T-03

Note: the Iterator is already implemented inside `Room.devices()` (T-02) and `SmartHomeHub.rooms()` (T-03). This task is about verifying it works correctly and writing unit tests that specifically prove the Iterator pattern is implemented.

---

## Paste this into Claude Code

```
The Iterator pattern is already implemented via `java.util.Enumeration` in Room and SmartHomeHub. I need you to write unit tests that prove this pattern works correctly.

CREATE: src/test/java/patterns/IteratorPatternTest.java

Requirements:
- Test class named IteratorPatternTest (mark the pattern in the class name for grading visibility)
- JUnit 5 tests
- Test 1: Room.devices() returns a java.util.Enumeration (check the type)
- Test 2: Enumeration traverses all added devices in order (add 3 Light objects, enumerate, verify count and identity)
- Test 3: Enumeration correctly reports hasMoreElements() == false after last element
- Test 4: Calling devices() twice returns FRESH enumerations (not exhausted from prior use)
- Test 5: SmartHomeHub.rooms() returns an Enumeration (same requirements as above)
- Test 6: Client code calling devices() cannot modify the underlying list via the Enumeration (verifies encapsulation — use reflection or check that Enumeration doesn't expose remove())

Top of file comment: "// ITERATOR PATTERN TESTS — verifying Enumeration-based iteration"

AFTER:
- Run `mvn test`
- Report output
- Total test count across all test classes so far should be: 3 (Singleton) + 6 (Iterator) = 9
```

---

## Acceptance Criteria

- [ ] 6 iterator tests pass
- [ ] Tests specifically check for `java.util.Enumeration` type (the assignment asks for Enumeration, not Iterator)

## Commit Message
```
T-04: Iterator pattern unit tests (Enumeration-based)
```

## What This Unblocks
Confidence that foundation is working. M3 can later iterate rooms/devices safely.

---

# Prompt 05 — Observer Pattern

**Task ID:** T-05
**Owner:** M1 + M2 (paired)
**Day:** 3
**Prerequisites:** T-02, T-03

---

## Paste this into Claude Code

```
Implement the Observer pattern from scratch. This is a graded requirement — the professor wants to see YOUR Observer/Observable interfaces, not JavaFX's or Java's built-in ones.

CREATE these files:

1. src/main/java/observer/Observer.java
   - Interface with one method: void update(devices.Device device, String event);
   - JavaDoc: "Notified when a Device changes state. event is a short string like 'STATE_CHANGED', 'TURNED_ON', 'MOTION_DETECTED'."
   - Top comment: "// OBSERVER PATTERN: observer side"

2. src/main/java/observer/Observable.java
   - Interface with three methods:
     - void attach(Observer o);
     - void detach(Observer o);
     - void notifyObservers(String event);
   - Top comment: "// OBSERVER PATTERN: subject side"

3. src/main/java/observer/User.java
   - Implements Observer
   - Fields: private final String name; private final List<String> eventLog = new ArrayList<>();
   - Constructor(String name)
   - update(Device, String event) method: logs "{name} received: {device.name} — {event}" to both eventLog and System.out
   - Methods: getName(), getEventLog() (defensive copy)
   - Top comment: "// CONCRETE OBSERVER"

4. MODIFY src/main/java/devices/Device.java
   - Device now implements Observable
   - Add private field: List<Observer> observers = new CopyOnWriteArrayList<>();
   - Implement attach/detach/notifyObservers
   - Modify setState(String newState): AFTER changing the state field, call notifyObservers("STATE_CHANGED")
   - Also add: notifyObservers method has a specific overload or the setState should include new state info
   - Design decision: pass the Device itself in update() call — PUSH MODEL (matches our Alternative Design #1)

CREATE tests:

5. src/test/java/observer/ObserverPatternTest.java
   - Test 1: attach(observer) then trigger state change — observer receives update
   - Test 2: detach(observer) — observer no longer receives updates
   - Test 3: multiple observers all receive the same event
   - Test 4: one observer listening to multiple devices — receives events from both
   - Test 5: PUSH MODEL verification — the update(Device, String) signature delivers the Device reference so observer doesn't need to query back

Top comment on test: "// OBSERVER PATTERN: Push model tests"

Also verify: running existing tests still passes (Singleton + Iterator tests should be unaffected).

AFTER:
- Run `mvn test`
- Confirm all 14+ tests pass (3 Singleton + 6 Iterator + 5 Observer = 14)
- Report output
```

---

## Acceptance Criteria

- [ ] Observer and Observable interfaces in `observer/` package
- [ ] `User` class implements Observer correctly
- [ ] `Device.setState()` triggers notifications
- [ ] PUSH model: observers receive Device reference + event string
- [ ] All 14 tests pass

## Commit Message
```
T-05: Observer pattern (push model) with Device integration
```

## What This Unblocks
- T-06 Abstract Factory (factories create devices, observers attach to them)
- T-13 Event logging (DeviceEventDAO subscribes as an observer)
- T-22 Safety rules (engine is an observer on all devices)
- T-10 GUI (Dashboard subscribes for live notifications)

## Important Gotcha

When modifying Device.java, be careful to NOT break any subclasses. Make sure Light, Thermostat, SecurityCamera, DoorLock still compile after Device now implements Observable. The Observable interface adds 3 methods — Device provides them, subclasses inherit. No subclass changes needed.

---

# Prompt 06 — Abstract Factory + Factory Methods

**Task ID:** T-06
**Owner:** M1 + M2 (paired)
**Day:** 3
**Prerequisites:** T-02, T-05

---

## Paste this into Claude Code

```
Implement the Abstract Factory pattern with factory methods for creating device families.

Context: Device hierarchy exists (Light, Thermostat, SecurityCamera, DoorLock). SmartHomeHub Singleton exists. Observer pattern exists.

Design decision: we group devices into FAMILIES:
- Lighting family → Light
- Climate family → Thermostat
- Security family → SecurityCamera, DoorLock

(In a real smart home, each family might come from different vendor ecosystems — Philips Hue for lighting, Nest for climate, Ring for security. Our factories mirror that.)

CREATE these files:

1. src/main/java/factory/DeviceFactory.java
   - Abstract class
   - Abstract methods (FACTORY METHODS):
     - public abstract Light createLight(String name, Room room);
     - public abstract Thermostat createThermostat(String name, Room room);
     - public abstract SecurityCamera createSecurityCamera(String name, Room room);
     - public abstract DoorLock createDoorLock(String name, Room room);
   - Concrete method: public final Device createDevice(String type, String name, Room room) — switch on type, delegate to the right abstract method
   - Top comment: "// ABSTRACT FACTORY PATTERN: creates families of related Devices"
   - Note: each subclass will implement only the methods relevant to its family (unsupported types throw UnsupportedOperationException)

2. src/main/java/factory/LightingFactory.java
   - extends DeviceFactory
   - Implements createLight — returns `new Light(name, room)` + adds device to room + throws UnsupportedOperationException for non-Light creation methods (with clear message: "LightingFactory only creates Lights")
   - Top comment: "// CONCRETE FACTORY: Lighting family"

3. src/main/java/factory/ClimateFactory.java
   - extends DeviceFactory
   - Implements createThermostat — returns new Thermostat + adds to room
   - Other methods throw UnsupportedOperationException
   - Top comment: "// CONCRETE FACTORY: Climate family"

4. src/main/java/factory/SecurityFactory.java
   - extends DeviceFactory
   - Implements createSecurityCamera AND createDoorLock — both return new instances added to room
   - createLight, createThermostat throw UnsupportedOperationException
   - Top comment: "// CONCRETE FACTORY: Security family (cameras + door locks)"

DESIGN NOTES:
- Factories also auto-add the device to the room — reduces caller boilerplate
- Returning the concrete type (Light, not Device) is intentional — callers often want the subtype methods
- The `createDevice(String type, ...)` on DeviceFactory lets code switch on type string (useful for loading from DB later)

CREATE tests:

5. src/test/java/factory/AbstractFactoryPatternTest.java
   - Test 1: LightingFactory.createLight() returns a non-null Light, correctly added to the room's device list
   - Test 2: LightingFactory.createThermostat() throws UnsupportedOperationException
   - Test 3: SecurityFactory creates BOTH cameras and locks
   - Test 4: DeviceFactory.createDevice("LIGHT", ...) on a LightingFactory returns a Light
   - Test 5: Created Light can be attached to an Observer and fires notifications (integration check with T-05)

Top comment: "// ABSTRACT FACTORY PATTERN tests"

AFTER:
- `mvn compile` + `mvn test`
- Confirm all tests pass (~19 total now)
- Report output
```

---

## Acceptance Criteria

- [ ] 3 concrete factories, each handling its own family
- [ ] Creating a device via a factory auto-adds it to the target Room
- [ ] 5 new tests pass
- [ ] Pattern comments clearly mark "ABSTRACT FACTORY" and "FACTORY METHOD" locations

## Commit Message
```
T-06: Abstract Factory + Factory Methods (Lighting/Climate/Security families)
```

## What This Unblocks
- T-10 GUI (Dashboard uses factories to create devices in demo data)
- T-11 Database (DeviceDAO will use DeviceFactory.createDevice(type, ...) when loading from DB)

## Why This Design (for M5's report)

Abstract Factory over Simple Factory: each concrete factory handles one family. Adding a new family (e.g., Entertainment devices) means creating one new factory class, NOT editing a giant switch statement. This directly serves the assignment's "modularity and ease of future expansion" constraint.

---

# Prompt 07 — Strategy Pattern (AutomationMode)

**Task ID:** T-07
**Owner:** M1 + M2 (paired)
**Day:** 4
**Prerequisites:** T-03 (Hub), T-05 (Observer)

---

## Paste this into Claude Code

```
Implement the Strategy pattern for automation modes. Hub can switch between Eco, Sleep, Away strategies at runtime.

CREATE:

1. src/main/java/strategy/AutomationMode.java
   - Interface with:
     - void apply(core.SmartHomeHub hub);
     - String getName();
   - Top comment: "// STRATEGY PATTERN: pluggable automation behavior"

2. src/main/java/strategy/EcoMode.java
   - implements AutomationMode
   - apply(hub): iterate all devices. Lights → setBrightness(40). Thermostats → setSetpoint(20).
   - getName() returns "ECO"

3. src/main/java/strategy/SleepMode.java
   - apply: Lights → turnOff(). DoorLocks → lock(). Cameras → arm().
   - getName() returns "SLEEP"

4. src/main/java/strategy/AwayMode.java
   - apply: Lights → turnOff(). DoorLocks → lock(). Cameras → arm(). Thermostats → setSetpoint(18).
   - getName() returns "AWAY"

5. MODIFY src/main/java/core/SmartHomeHub.java
   - Add field: private AutomationMode currentMode;
   - Add method: public void setMode(AutomationMode mode) — stores, calls mode.apply(this), then notifies observers via a hub-level event (see below)
   - Add method: public AutomationMode getCurrentMode()
   - Hub-level event: for simplicity, just log to System.out "Hub: mode changed to {mode.getName()}"
   - Default mode on construction: null (no automation applied)

DESIGN NOTES:
- Strategies iterate via hub.allDevices() → use instanceof sparingly (only for type-specific actions like lights vs locks)
- Modes don't keep state; they're pure functions applied on demand
- Adding a new mode = one new class implementing AutomationMode (Open-Closed Principle)

CREATE tests:

6. src/test/java/strategy/StrategyPatternTest.java
   - Test: EcoMode on a hub with 2 lights → both lights have brightness 40
   - Test: SleepMode → all lights off, locks locked
   - Test: AwayMode → thermostat setpoint is 18
   - Test: Hub.setMode() calls the strategy's apply method
   - Test: switching strategies works (set Eco, then Sleep, each takes effect)

AFTER:
- mvn test
- All tests pass (~24 total now)
```

---

## Acceptance Criteria
- [ ] 3 concrete modes, each applying distinct rules
- [ ] Hub.setMode() wires the strategy through
- [ ] 5 new tests pass

## Commit Message
```
T-07: Strategy pattern — AutomationMode with Eco/Sleep/Away
```

## What This Unblocks
- T-17 ScheduleExecutor (scheduled mode changes)
- T-10 GUI (mode selector bar)

---

# Prompt 08 — JavaFX Skeleton (minimal boot into main window)

**Task ID:** T-08
**Owner:** M1 + M2 (paired)
**Day:** 4
**Prerequisites:** T-01 through T-07

---

## Paste this into Claude Code

```
We have a working domain model (Device, Room, Hub, Observer, Factory, Strategy). Now we need a JavaFX skeleton that boots into a visible dashboard wired to the Hub Singleton. This is a MINIMAL scaffold — M3 will build the real UI on top.

MODIFY/CREATE:

1. src/main/java/Main.java (update)
   - Before launching JavaFX, populate demo data:
     - Get SmartHomeHub.getInstance()
     - Create 2 Rooms: "Living Room", "Bedroom"
     - Use factories: LightingFactory.createLight(), etc.
     - Add ~4-6 devices across rooms
   - Then launch JavaFX
   - This is a "dev seed" — will be replaced by DB loading in T-11

2. src/main/resources/fxml/MainWindow.fxml (replace)
   - BorderPane root
   - Top: a label "Smart Home Dashboard" with styling
   - Left: VBox #roomList (will be populated with buttons — one per Room)
   - Center: VBox #deviceList (populated when a room is clicked — shows device cards)
   - Bottom: HBox with mode buttons: "Eco", "Sleep", "Away"
   - fx:controller = "gui.MainWindowController"

3. src/main/java/gui/MainWindowController.java (replace)
   - @FXML fields matching the FXML ids
   - initialize() method:
     - Get Hub.getInstance()
     - Enumerate rooms, create one Button per room, add to #roomList
     - Each room button has an onAction that calls showRoom(room)
     - Mode buttons call hub.setMode(new EcoMode()) / SleepMode / AwayMode
   - showRoom(Room r):
     - Clear #deviceList
     - Enumerate r.devices()
     - For each device, create an HBox with: Label (device name + state), Button "Toggle"
     - Toggle button's onAction: calls device.setState(...) toggling ON/OFF
   - Register this controller as an Observer on every Device via Device.attach(this)
     - MainWindowController must implement observer.Observer
     - update(Device, String event) pushes to a simple text area OR console for now (M3 will make this prettier)
   - IMPORTANT: use Platform.runLater(...) inside update() — Observer callbacks might come from any thread

4. src/main/resources/css/smarthome.css (expand)
   - .root { -fx-background-color: #f8fafc; -fx-font-family: "System"; }
   - .button { -fx-background-radius: 8; -fx-background-color: #3182ce; -fx-text-fill: white; -fx-padding: 8 16; }
   - .button:hover { -fx-background-color: #2c5282; }
   - #roomList { -fx-padding: 12; -fx-spacing: 8; -fx-background-color: #edf2f7; }
   - #deviceList { -fx-padding: 16; -fx-spacing: 8; }

REQUIREMENTS:
- App boots, window shows 2 rooms on the left
- Click a room → center shows its devices
- Click Toggle → device state flips, observer log updates (console is fine for now)
- Mode buttons work — clicking "Eco" applies EcoMode
- No crashes, no exceptions

AFTER:
- mvn javafx:run
- User will manually verify the window works
- Report success
```

---

## Acceptance Criteria
- [ ] App launches a real window
- [ ] Room list populated from Hub
- [ ] Clicking a room shows devices
- [ ] Toggle button changes device state
- [ ] Mode buttons apply AutomationMode strategies
- [ ] Console shows Observer notifications

## Commit Message
```
T-08: JavaFX dashboard skeleton wired to Hub + Observer + Strategy
```

## What This Unblocks
- T-10 Dashboard v1 (M3 replaces this with polished version)
- T-09 Smoke test (proves foundation integration)

---

# Prompt 09 — Smoke Test (foundation integration check)

**Task ID:** T-09
**Owner:** M1 + M2 (paired)
**Day:** 4
**Prerequisites:** T-01 through T-08

---

## Paste this into Claude Code

```
Create a CLI smoke test that exercises ALL 5 foundation patterns working together. Running this single test proves the foundation is ready for the next phase.

CREATE: src/test/java/integration/FoundationSmokeTest.java

This is a JUnit 5 test that does the full flow:

1. Get Hub via Singleton
2. Create a Room
3. Use LightingFactory to create 2 Lights
4. Use SecurityFactory to create a DoorLock and SecurityCamera
5. Create a User (Observer), attach to all 4 devices
6. Iterate the room's devices using Enumeration — verify count == 4
7. Toggle devices — verify User received notifications (check eventLog)
8. Apply EcoMode — verify lights are dimmed to 40
9. Apply AwayMode — verify locks are locked and cameras armed
10. Verify Hub.findDeviceById() works for all 4 devices

Assertions at each step. Clear @DisplayName on the test method: "Foundation: Singleton + Factory + Observer + Iterator + Strategy all work together"

Top comment on class:
// INTEGRATION SMOKE TEST
// Purpose: prove all 5 foundation patterns collaborate correctly.
// If this test passes, the foundation phase (Days 1-4) is GREEN.
// Owner: M1 + M2

AFTER:
- mvn test
- Confirm this specific test PASSES
- Report overall test count and any failures
```

---

## Acceptance Criteria

- [ ] FoundationSmokeTest passes
- [ ] Test exercises all 5 foundation patterns in one test
- [ ] All prior tests (~24) still pass
- [ ] **🚪 GATE CHECK: show team the passing output. This is the foundation sign-off.**

## Commit Message
```
T-09: foundation smoke test — all 5 core patterns integrate correctly
```

## What This Unblocks

EVERYTHING. Tag the commit as `v0.foundation` and notify the team:
```bash
git tag -a v0.foundation -m "Foundation gate passed: Singleton+Factory+Observer+Iterator+Strategy"
git push --tags
```

After this, M3 starts the real GUI, M2 starts the database, M4 starts SecurityContext, M1 starts Command pattern. They've all been waiting.

---

# Prompt 10 — Dashboard v1 (real JavaFX GUI)

**Task ID:** T-10
**Owner:** M3
**Days:** 5–7
**Prerequisites:** T-08, T-09 (gate passed)

---

## Paste this into Claude Code

```
Replace the minimal JavaFX skeleton with a polished dashboard. Keep the wiring to Hub and Observer — just make it real.

MODIFY/CREATE files:

1. src/main/resources/fxml/Dashboard.fxml (renaming from MainWindow)
   - BorderPane root with the modern dashboard structure:
     - Top: HBox toolbar with app title, clock, mode indicator
     - Left: VBox with header "Rooms" + list of room buttons
     - Center: ScrollPane containing VBox #deviceList (device cards)
     - Right: VBox "Notifications" with a ListView #notificationList
     - Bottom: HBox mode selector (Eco / Sleep / Away buttons + "Manual" to clear mode)
   - Use padding, spacing, proper sizes (1100x750 preferred)
   - Apply CSS classes to everything for styling hooks

2. src/main/resources/fxml/DeviceCard.fxml
   - A reusable component for one device
   - HBox containing: icon (Label or ImageView), VBox (name + state label), HBox (action buttons per device type)
   - fx:controller = "gui.DeviceCardController"

3. src/main/java/gui/DashboardController.java
   - Replace MainWindowController
   - implements observer.Observer
   - @FXML fields: roomList, deviceList, notificationList, modeIndicator, clockLabel
   - initialize():
     - Build room buttons from hub.rooms() Enumeration
     - Update clock every second using a Timeline
     - Subscribe as Observer to ALL devices
   - onRoomSelected(Room r): clears deviceList, loads DeviceCard FXML for each device in room
   - onModeSelected(String modeName): calls hub.setMode(new EcoMode() / SleepMode / AwayMode)
   - update(Device d, String event): Platform.runLater — add to notificationList, trim to most recent 50

4. src/main/java/gui/DeviceCardController.java
   - Fields: nameLabel, stateLabel, actionsBox
   - setDevice(Device d): displays info, creates appropriate buttons (Toggle for Light, +/- for Thermostat, Record for Camera, Unlock for DoorLock)
   - Each button calls the right Device method directly (we'll refactor to use Commands in T-15)

5. src/main/resources/css/smarthome.css (major upgrade)
   - Use color palette: primary #3182ce, background #f7fafc, card bg white, text #2d3748, accent green #38a169
   - .device-card { -fx-background-color: white; -fx-padding: 16; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2); }
   - .device-card:hover { -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 4); }
   - .room-button { -fx-min-width: 180; -fx-alignment: CENTER-LEFT; }
   - .room-button.selected { -fx-background-color: #3182ce; -fx-text-fill: white; }
   - .mode-button.active { -fx-background-color: #38a169; }
   - .notification-item { -fx-padding: 8; -fx-border-color: transparent transparent #e2e8f0 transparent; }
   - .clock { -fx-font-size: 14; -fx-text-fill: #718096; }

6. Main.java — update the FXML load path to /fxml/Dashboard.fxml and ensure the CSS is applied

THREADING:
- All hub/device access from controllers must be fine-grained — CopyOnWriteArrayList in Device.observers already handles that
- Every UI update triggered from Observer.update() wrapped in Platform.runLater()

AFTER:
- mvn javafx:run
- Confirm: window opens, room list shows, clicking room shows devices, toggle works, notifications appear in the right panel, mode buttons work
- Report success and any issues
```

---

## Acceptance Criteria
- [ ] Modern-looking dashboard with rooms, devices, notifications panel
- [ ] Clock updates every second
- [ ] Click a room → devices visible in center
- [ ] Toggle a device → notification appears on the right
- [ ] Mode buttons switch automation mode
- [ ] No exceptions in console during normal use

## Commit Message
```
T-10: Dashboard v1 — polished JavaFX UI with Observer wiring
```

## What This Unblocks
T-18 (tabs built on top), T-19 (Facade refactor)

---

# Prompt 11 — Database Foundation (SQLite connection Singleton)

**Task ID:** T-11
**Owner:** M2
**Days:** 5–6
**Prerequisites:** T-01

---

## Paste this into Claude Code

```
Create the SQLite persistence foundation: schema, seed data, and a thread-safe connection manager.

CREATE:

1. src/main/resources/schema.sql
   Tables: users, rooms, devices, device_events, schedules, commands_log.
   Schema (use this exactly):

   CREATE TABLE IF NOT EXISTS users (
       id INTEGER PRIMARY KEY AUTOINCREMENT,
       name TEXT NOT NULL UNIQUE,
       pin_hash TEXT NOT NULL,
       created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
   );

   CREATE TABLE IF NOT EXISTS rooms (
       id INTEGER PRIMARY KEY AUTOINCREMENT,
       name TEXT NOT NULL UNIQUE
   );

   CREATE TABLE IF NOT EXISTS devices (
       id TEXT PRIMARY KEY,
       name TEXT NOT NULL,
       type TEXT NOT NULL,
       room_id INTEGER NOT NULL,
       config TEXT,
       FOREIGN KEY (room_id) REFERENCES rooms(id)
   );

   CREATE TABLE IF NOT EXISTS device_events (
       id INTEGER PRIMARY KEY AUTOINCREMENT,
       device_id TEXT NOT NULL,
       event_type TEXT NOT NULL,
       old_state TEXT,
       new_state TEXT,
       timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
       FOREIGN KEY (device_id) REFERENCES devices(id)
   );
   CREATE INDEX IF NOT EXISTS idx_events_device ON device_events(device_id);
   CREATE INDEX IF NOT EXISTS idx_events_time ON device_events(timestamp);

   CREATE TABLE IF NOT EXISTS schedules (
       id INTEGER PRIMARY KEY AUTOINCREMENT,
       name TEXT NOT NULL,
       cron_expr TEXT NOT NULL,
       mode_to_apply TEXT NOT NULL,
       enabled INTEGER NOT NULL DEFAULT 1
   );

   CREATE TABLE IF NOT EXISTS commands_log (
       id INTEGER PRIMARY KEY AUTOINCREMENT,
       command_id TEXT NOT NULL,
       device_id TEXT,
       action TEXT NOT NULL,
       params_json TEXT,
       result TEXT,
       timestamp TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
   );
   CREATE INDEX IF NOT EXISTS idx_cmdlog_time ON commands_log(timestamp);

2. src/main/resources/seed.sql
   - Insert default rooms: 'Living Room', 'Bedroom', 'Kitchen', 'Entrance'
   - Insert one default user: name='admin', pin_hash=<SHA-256 of "1234">
   - Calculate that hash and hardcode: 03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4

3. src/main/java/persistence/Database.java
   - SINGLETON pattern (same style as SmartHomeHub)
   - Constants: DB_FILE = "smarthome.db", JDBC_URL = "jdbc:sqlite:" + DB_FILE
   - Private constructor:
     - If DB file doesn't exist, create it AND execute schema.sql AND execute seed.sql
     - Open persistent Connection (SQLite connections are single-threaded but we'll synchronize DAOs)
     - Enable foreign keys: execute "PRAGMA foreign_keys = ON"
   - getInstance() thread-safe
   - Methods: getConnection(), close()
   - Top comment: "// PERSISTENCE: SQLite connection manager (Singleton)"
   - Helper: runScript(String resourcePath) — reads SQL file from resources and executes each statement split by ';'

4. Add dependency to pom.xml:
   <dependency>
       <groupId>org.xerial</groupId>
       <artifactId>sqlite-jdbc</artifactId>
       <version>3.45.3.0</version>
   </dependency>

5. src/test/java/persistence/DatabaseTest.java
   - Test 1: getInstance() returns non-null, same instance twice
   - Test 2: connection is open and valid
   - Test 3: after init, rooms table has 4 rows (seeded)
   - Test 4: after init, users table has 1 row
   - USE a temp DB file for each test (delete `smarthome.db` in @BeforeEach)

AFTER:
- mvn test
- Verify smarthome.db is created in project root (ignored by .gitignore)
- Report output
```

---

## Acceptance Criteria
- [ ] Running the app first time creates `smarthome.db` with schema + seed data
- [ ] Database is Singleton
- [ ] Tests pass

## Commit Message
```
T-11: SQLite schema + Database singleton connection manager
```

## What This Unblocks
T-12, T-13, T-14 — all DAOs need this.

---

# Prompt 12 — Core DAOs (User, Room, Device)

**Task ID:** T-12
**Owner:** M2
**Days:** 6–7
**Prerequisites:** T-11

---

## Paste this into Claude Code

```
Create the base DAO class and the 3 core DAOs: User, Room, Device.

CREATE:

1. src/main/java/persistence/dao/BaseDAO.java
   - Abstract class
   - Protected helper: Connection conn() — returns Database.getInstance().getConnection()
   - Protected helpers for try-with-resources patterns (optional sugar)
   - Top comment: "// DAO PATTERN: base class with shared helpers"

2. src/main/java/persistence/dao/UserDAO.java
   - extends BaseDAO
   - Methods:
     - int insert(String name, String pinHash) — returns generated ID
     - User findByName(String name) — returns null if not found
     - boolean verifyPin(String name, String pinHash) — returns true if user exists AND hash matches
     - List<User> findAll()
     - void delete(int id)
   - ALL methods synchronized
   - Use PreparedStatement always — NEVER string concatenation in SQL
   - Top comment: "// DAO PATTERN: User persistence"

3. src/main/java/persistence/dao/RoomDAO.java
   - Methods: insert(String name) → int id, findAll() → List<Room>, findByName(String) → Room, findById(int) → Room
   - Note: loading Rooms doesn't load devices — DeviceDAO handles that

4. src/main/java/persistence/dao/DeviceDAO.java
   - Methods:
     - void insert(Device d, int roomId)
     - List<Device> findByRoomId(int roomId) — reconstructs Device objects via DeviceFactory based on type
     - void updateConfig(String deviceId, String configJson)
     - void delete(String deviceId)
   - Reconstruction: if type="LIGHT", create new Light(name, room); similar for others
   - Top comment: "// DAO PATTERN: Device persistence with type-based reconstruction"

5. Also create a POJO: src/main/java/persistence/User.java
   - Simple holder: id, name, pinHash, createdAt
   - DO NOT put this in observer/User — that's the domain User (Observer). They represent different things (observer/User = runtime listener; persistence/User = DB record)
   - Alternative: rename one to avoid confusion. Suggest renaming persistence one to UserAccount.

ACTUALLY: use `UserAccount` as the POJO name to avoid clash with observer.User.

6. Tests: src/test/java/persistence/dao/UserDAOTest.java, RoomDAOTest.java, DeviceDAOTest.java
   - Use an in-memory DB for tests: override JDBC_URL to "jdbc:sqlite::memory:" for test scope
   - Each DAO: test insert, find, update/delete as applicable
   - Clean up after each test (DROP tables + recreate) — helper method

AFTER:
- mvn test
- Report total test count
```

---

## Acceptance Criteria
- [ ] 3 DAOs with CRUD
- [ ] All use PreparedStatement
- [ ] Synchronized methods (thread-safe)
- [ ] In-memory SQLite works for tests

## Commit Message
```
T-12: Core DAOs — User, Room, Device (DAO pattern)
```

---

# Prompt 13 — Event Logging DAO + Observer hook

**Task ID:** T-13
**Owner:** M2
**Days:** 7–8
**Prerequisites:** T-05, T-12

---

## Paste this into Claude Code

```
Create DeviceEventDAO and hook it into the Observer system so EVERY state change gets logged to the database automatically.

CREATE:

1. src/main/java/persistence/DeviceEvent.java (POJO)
   - Fields: id (int), deviceId (String), eventType (String), oldState (String), newState (String), timestamp (String)
   - Constructors, getters

2. src/main/java/persistence/dao/DeviceEventDAO.java
   - extends BaseDAO
   - Methods:
     - void insert(String deviceId, String eventType, String oldState, String newState)
     - List<DeviceEvent> recentEvents(int limit)
     - List<DeviceEvent> eventsByDevice(String deviceId, int limit)
     - List<DeviceEvent> eventsInRange(String fromIso, String toIso)
   - Synchronized

3. src/main/java/persistence/EventLogger.java
   - implements observer.Observer
   - In update(Device d, String event): inserts a row via DeviceEventDAO
   - eventType is the string passed in (e.g. "STATE_CHANGED")
   - newState comes from d.getState()
   - oldState: tricky because Observer callback doesn't currently include it — for now pass null, or add an overload to Observable interface
     → DO NOT modify Observable interface (breaks other observers). Instead, pass null for oldState.
   - Attach to ALL devices in Hub:
     - Static method attachToAllDevices(): iterate hub.allDevices(), call device.attach(this)
   - Singleton or instance? → regular class, held by Hub

4. MODIFY core/SmartHomeHub.java
   - Add field: private EventLogger eventLogger;
   - In getInstance/constructor: initialize eventLogger = new EventLogger()
   - Expose method: public void registerEventLogger(EventLogger el) that attaches it to all existing + future devices
   - When a new device is added to any room, auto-attach eventLogger
   - Simplest implementation: Hub.addRoom() eventually → Room.addDevice() should notify Hub. Keep it simple: after creating a factory-made device, factory also calls eventLogger.attachTo(device).
   - ACTUALLY simplest: modify DeviceFactory to auto-attach EventLogger after creating each device

5. MODIFY factory/DeviceFactory.java (createDevice concrete method)
   - After creating device + adding to room, call Hub.getInstance().getEventLogger().attachTo(device)
   - OR: static reference EventLogger.getInstance().attachTo(device)
   - Pick one; EventLogger as Singleton via Hub is cleaner

6. Tests: src/test/java/persistence/dao/DeviceEventDAOTest.java
   - Insert event → retrievable by device id
   - Insert 10 events → recentEvents(5) returns 5 newest
   - eventsInRange with specific timestamps

AFTER:
- mvn test
- Also: add an integration test — Create device, toggle state, query DeviceEventDAO.recentEvents(1) → verify the event is there
```

---

## Acceptance Criteria
- [ ] Device state change → row in device_events table
- [ ] No performance issues — test with 100 toggles

## Commit Message
```
T-13: DeviceEventDAO + Observer-based auto-logging
```

---

# Prompt 14 — Schedule + Commands Log DAOs

**Task ID:** T-14
**Owner:** M2
**Day:** 8
**Prerequisites:** T-12

---

## Paste this into Claude Code

```
Create the final two DAOs: ScheduleDAO and CommandsLogDAO.

CREATE:

1. src/main/java/persistence/Schedule.java (POJO)
   - Fields: id (int), name (String), cronExpr (String), modeToApply (String), enabled (boolean)

2. src/main/java/persistence/dao/ScheduleDAO.java
   - Methods: insert(Schedule), findAll(), findEnabled(), updateEnabled(int id, boolean), delete(int id)

3. src/main/java/persistence/CommandsLogEntry.java (POJO)
   - Fields: id (int), commandId (String UUID), deviceId (String), action (String), paramsJson (String), result (String), timestamp (String)

4. src/main/java/persistence/dao/CommandsLogDAO.java
   - Methods:
     - void insert(String commandId, String deviceId, String action, String paramsJson, String result)
     - List<CommandsLogEntry> recent(int limit)
     - List<CommandsLogEntry> byDevice(String deviceId, int limit)

5. Tests for both DAOs

AFTER:
- mvn test
- Notify M1 that CommandsLogDAO is ready (for T-16)
- Notify M4 that ScheduleDAO is ready (for T-17)
```

---

## Acceptance Criteria
- [ ] Both DAOs working with tests

## Commit Message
```
T-14: Schedule + CommandsLog DAOs (DAO pattern complete, 6 DAOs total)
```

## What This Unblocks
T-16 (Command logging), T-17 (Schedule executor), T-18 GUI tabs that display this data

---

# Prompt 15 — Command Pattern (DeviceCommand + Invoker)

**Task ID:** T-15
**Owner:** M1
**Days:** 5–8
**Prerequisites:** T-02, T-14 (for future T-16 integration)

---

## Paste this into Claude Code

```
Implement the Command pattern: every user action becomes a Command object with execute() + undo() + describe().

CREATE:

1. src/main/java/command/DeviceCommand.java
   - Interface:
     - void execute();
     - void undo();
     - String describe();
     - String getCommandId();      // UUID
     - String getDeviceId();       // for DB logging
     - String getAction();         // "TURN_ON", "SET_BRIGHTNESS", etc.
     - String getParamsJson();     // "{}" or serialized params
   - Top comment: "// COMMAND PATTERN: encapsulates actions as reversible objects"

2. src/main/java/command/AbstractCommand.java
   - Abstract class implementing DeviceCommand
   - Generates commandId via UUID.randomUUID() in constructor
   - Stores deviceId (set in constructor)
   - describe() default implementation: "Command(action) on deviceId"
   - Subclasses override execute/undo

3. src/main/java/command/TurnOnCommand.java (extends AbstractCommand)
   - Constructor: takes a Light
   - Field: previousState (to enable undo)
   - execute(): records previousState = light.getState(), calls light.turnOn()
   - undo(): if previousState was OFF, call light.turnOff(); else no-op
   - getAction() returns "TURN_ON"

4. src/main/java/command/TurnOffCommand.java — mirror of TurnOn

5. src/main/java/command/SetBrightnessCommand.java
   - Takes Light + int newLevel
   - execute(): stores oldLevel, calls setBrightness(newLevel)
   - undo(): setBrightness(oldLevel)
   - getParamsJson(): returns {"level":NEW} — use simple string concat, no Jackson

6. src/main/java/command/UnlockCommand.java (for DoorLock)
   - Takes DoorLock + String providedPin
   - execute(): calls unlock(pin)
   - undo(): lock()
   - Important: if unlock fails (bad pin), store failure state; undo is no-op

7. src/main/java/command/LockCommand.java

8. src/main/java/command/SetTempCommand.java (Thermostat)

9. src/main/java/command/SetAutomationModeCommand.java
   - Takes SmartHomeHub + new AutomationMode
   - execute(): stores previous mode, hub.setMode(new)
   - undo(): hub.setMode(previous)
   - deviceId = "__hub__"

10. src/main/java/command/CommandInvoker.java
    - Fields:
      - private final Deque<DeviceCommand> history = new ArrayDeque<>();
      - private static final int MAX_HISTORY = 20;
    - Methods:
      - public void execute(DeviceCommand cmd) — calls cmd.execute(), pushes to history, trims to MAX_HISTORY
      - public boolean canUndo() — returns !history.isEmpty()
      - public void undo() — pops and calls cmd.undo()
      - public List<DeviceCommand> history() — defensive copy, newest first
    - Thread-safe: all methods synchronized
    - Top comment: "// COMMAND PATTERN: Invoker with bounded undo stack"

11. Tests: src/test/java/command/
    - CommandInvokerTest: execute + undo round trips, stack limits at 20, history() returns defensive copy
    - TurnOnCommandTest: Light off → execute → on. Undo → off again.
    - SetBrightnessCommandTest: 50 → 80 → undo → back to 50
    - UnlockCommandTest: wrong PIN doesn't unlock and undo is safe

DO NOT wire into DB yet — that's T-16. Just make the pattern work standalone.
DO NOT touch GUI — M3 wires buttons to this in T-18.

AFTER:
- mvn test
- Report passing count
```

---

## Acceptance Criteria
- [ ] 7+ concrete Command classes
- [ ] CommandInvoker with bounded undo stack
- [ ] Tests prove undo works correctly for each command type

## Commit Message
```
T-15: Command pattern — interface, concretes, Invoker with undo stack
```

## What This Unblocks
T-16 (DB logging), T-18 (GUI uses commands), T-19 (Facade exposes command execution)

---

# Prompt 16 — Command → DB Logging Integration

**Task ID:** T-16
**Owner:** M1
**Days:** 8–9
**Prerequisites:** T-15, T-14

---

## Paste this into Claude Code

```
Hook the CommandInvoker to the CommandsLogDAO so every execute() writes a row to the database.

MODIFY: src/main/java/command/CommandInvoker.java

Changes:
- Inject a CommandsLogDAO reference (constructor parameter, or lazy static — use constructor injection for cleanliness)
- In execute():
  - Before calling cmd.execute(), log a "PENDING" row? No — too much overhead.
  - After execute() succeeds, log a row via commandsLogDAO.insert(...)
  - If execute() throws, catch, log row with result="ERROR: " + exception.getMessage(), rethrow
- In undo():
  - After undo() succeeds, log a new row with action="UNDO:"+original action, result="OK"

Also MODIFY: the AbstractCommand.getParamsJson() to return a proper JSON string for each command type
- TurnOnCommand: "{}"
- SetBrightnessCommand: "{\"level\":" + newLevel + "}"
- SetTempCommand: "{\"celsius\":" + setpoint + "}"
- UnlockCommand: "{\"pin_provided\":true}"  (never log the actual PIN!)

Tests: src/test/java/command/CommandLoggingIntegrationTest.java
- Create a real Light
- Create a CommandInvoker wired to a REAL CommandsLogDAO (using in-memory SQLite)
- Execute 3 commands
- Query commandsLogDAO.recent(10) → expect 3 rows with correct actions
- Execute undo → expect a 4th row with action="UNDO:TURN_ON"

AFTER:
- mvn test
- Report output
```

---

## Acceptance Criteria
- [ ] Every execute() produces a DB row
- [ ] Undo also produces a log row
- [ ] PIN never appears in log (security)

## Commit Message
```
T-16: CommandInvoker logs every execution and undo to commands_log
```

---

# Prompt 17 — ScheduleExecutor (background thread)

**Task ID:** T-17
**Owner:** M4
**Days:** 9–11
**Prerequisites:** T-14 (ScheduleDAO), T-07 (AutomationMode)

---

## Paste this into Claude Code

```
Create a background thread that reads enabled schedules every minute and applies the matching automation mode when time matches.

CREATE: src/main/java/schedule/ScheduleExecutor.java

Requirements:
- Uses java.util.concurrent.ScheduledExecutorService
- Single daemon thread (so JVM exits cleanly)
- Tick interval: 60 seconds, aligned to the top of the minute
- On each tick:
  - Query ScheduleDAO.findEnabled()
  - For each schedule, evaluate if it should fire NOW
  - Cron format to support:
    - "HH:MM" — fires daily at that time (e.g. "22:00")
    - "MON HH:MM" — fires weekly on that day + time (e.g. "MON 07:00")
    - Days: SUN MON TUE WED THU FRI SAT
  - If match, apply the mode:
    - Parse mode_to_apply string ("ECO" / "SLEEP" / "AWAY")
    - Create corresponding AutomationMode object
    - Call SmartHomeHub.getInstance().setMode(mode)
  - Log the execution to device_events via DeviceEventDAO (eventType="SCHEDULE_FIRED")

Methods:
- public void start() — starts the scheduler
- public void stop() — shuts it down cleanly
- private boolean shouldFire(Schedule s, LocalDateTime now) — evaluator
- private AutomationMode modeByName(String name) — factory helper

IDEMPOTENCY:
- Track last-fired timestamp per schedule in a Map<Integer, LocalDateTime>
- Don't re-fire the same schedule within 55 seconds (handles drift)

Top comment: "// SCHEDULE ENGINE: applies AutomationMode strategies on a schedule"

Register ScheduleExecutor to start on app boot — modify Main.java to call executor.start() after JavaFX launch, and install a shutdown hook that calls stop().

Tests: src/test/java/schedule/ScheduleExecutorTest.java
- shouldFire("22:00", LocalDateTime.of(2026, 5, 4, 22, 0)) → true
- shouldFire("22:00", LocalDateTime.of(2026, 5, 4, 21, 59)) → false
- shouldFire("MON 07:00", LocalDateTime.of(2026, 5, 4, 7, 0)) [May 4 2026 is a Monday] → true
- shouldFire called twice in same minute → fires only once (idempotency)

AFTER:
- mvn test
- Report
```

---

## Acceptance Criteria
- [ ] Daemon thread so JVM exits cleanly on window close
- [ ] Tests verify HH:MM and DAY HH:MM formats
- [ ] Same schedule doesn't fire twice in same minute

## Commit Message
```
T-17: ScheduleExecutor — background thread applying AutomationMode on schedule
```

---

# Prompt 18 — GUI Tabs (Event History, Command History, User Mgmt, Schedule Editor)

**Task ID:** T-18
**Owner:** M3
**Days:** 8–11
**Prerequisites:** T-10 (Dashboard v1), T-12 (DAOs), T-15 (Commands)

---

## Paste this into Claude Code

```
Extend the Dashboard with 4 new tabs, each backed by DAOs.

MODIFY src/main/resources/fxml/Dashboard.fxml:
- Wrap the current center content in a TabPane
- Add 5 tabs:
  - "Home" (the existing Dashboard view with rooms + devices + notifications)
  - "Events" (#eventHistoryTab) loads EventHistoryTab.fxml
  - "Commands" (#commandHistoryTab) loads CommandHistoryTab.fxml
  - "Users" (#usersTab) loads UserManagementTab.fxml
  - "Schedules" (#schedulesTab) loads ScheduleEditorTab.fxml

CREATE:

1. src/main/resources/fxml/EventHistoryTab.fxml + EventHistoryController.java
   - Layout: TableView with columns [Timestamp, Device, Event, New State]
   - Filter controls above: ComboBox for room, ComboBox for event type, text search
   - Controller uses DeviceEventDAO.recentEvents(100)
   - Auto-refresh: Timeline that calls refresh() every 2 seconds
   - Refresh MUST run on background thread (Task<ObservableList<DeviceEvent>>) and updateItems() on Platform.runLater
   - Methods: refresh(), applyFilter(), exportToCsv() (bonus)

2. src/main/resources/fxml/CommandHistoryTab.fxml + CommandHistoryController.java
   - TableView columns: [Timestamp, Action, Device, Params, Result, Undo]
   - Undo column has a button per row
   - Undo button clicks CommandInvoker.undo() for commands still in the live invoker (not for old DB-only entries)
     - Simplification: Undo button only works for the MOST RECENT command in the live invoker
     - Other buttons are disabled with tooltip "Can only undo most recent"
   - Controller uses CommandsLogDAO.recent(50)
   - Also auto-refresh every 2s

3. src/main/resources/fxml/UserManagementTab.fxml + UserManagementController.java
   - TableView of users
   - Form: name input, PIN input (PasswordField), "Add User" button, "Delete Selected" button, "Change PIN" button
   - Uses UserDAO
   - PIN hash: SHA-256 before storing (use java.security.MessageDigest)
   - Helper: src/main/java/util/PinHasher.java with `public static String hash(String pin)` using SHA-256 returning hex string

4. src/main/resources/fxml/ScheduleEditorTab.fxml + ScheduleEditorController.java
   - TableView of schedules
   - Form: name, cron expression (HH:MM or DAY HH:MM), mode dropdown (ECO/SLEEP/AWAY), enabled checkbox, Save button
   - Uses ScheduleDAO

CRITICAL PATTERNS:
- Every DAO call must be on Task<T> background thread — NEVER on JavaFX Application Thread
- After Task completes, update UI on Platform.runLater
- Show a loading spinner (ProgressIndicator) while tasks run

CSS: add styles for .tab-pane, .table-view, .filter-row, .form-row to smarthome.css

Tests: 
- Hard to unit test GUI logic, but DO test PinHasher:
  - src/test/java/util/PinHasherTest.java
  - hash("1234") returns expected hex
  - hash("1234") is idempotent (same output for same input)
  - hash("1234") != hash("12345")

AFTER:
- mvn javafx:run
- Navigate to each tab — verify data loads, no UI freezes
- Report
```

---

## Acceptance Criteria
- [ ] 4 new tabs functional
- [ ] No UI freezes (background threads for DAO calls)
- [ ] Undo button works for most recent live command
- [ ] PIN hashing verified

## Commit Message
```
T-18: GUI tabs — Events, Commands, Users, Schedules (background-threaded)
```

---

# Prompt 19 — Facade Pattern (HomeController)

**Task ID:** T-19
**Owner:** M3
**Days:** 11–12
**Prerequisites:** T-18 (GUI working), all DAOs, Commands, SecurityContext

---

## Paste this into Claude Code

```
Refactor: the GUI currently depends on SmartHomeHub, multiple DAOs, CommandInvoker, SecurityContext directly. That's 5+ subsystems coupled to GUI code. Wrap them in a Facade so GUI depends on ONE class.

CREATE: src/main/java/facade/HomeController.java

Pattern: FACADE — one unified interface hiding a complex subsystem.

Fields (all private final):
- SmartHomeHub hub
- CommandInvoker invoker
- UserDAO userDAO
- RoomDAO roomDAO
- DeviceDAO deviceDAO
- DeviceEventDAO eventDAO
- ScheduleDAO scheduleDAO
- CommandsLogDAO commandsLogDAO
- SecurityContext security
- ScheduleExecutor scheduler

Constructor: initialize all of the above.

Singleton or regular? → Singleton is fine for simplicity.

Methods to expose (ALL GUI operations go through these):

// === Rooms/Devices ===
- List<Room> allRooms()
- List<Device> devicesInRoom(Room r)
- Device deviceById(String id)

// === Commands (GUI buttons route here) ===
- void executeCommand(DeviceCommand cmd) — synchronized
- boolean canUndo()
- void undoLastCommand()
- List<CommandsLogEntry> recentCommands(int limit)

// === Modes ===
- void setMode(String modeName) — takes "ECO"/"SLEEP"/"AWAY", creates + applies the right strategy
- String currentModeName()

// === Events ===
- List<DeviceEvent> recentEvents(int limit)

// === Users ===
- boolean login(String name, String pin)
- void logout()
- UserAccount currentUser()
- void addUser(String name, String pin) — hashes PIN via PinHasher
- void deleteUser(int id)
- List<UserAccount> allUsers()

// === Schedules ===
- List<Schedule> allSchedules()
- void createSchedule(String name, String cronExpr, String modeName)
- void toggleSchedule(int id, boolean enabled)
- void deleteSchedule(int id)

Top comment: "// FACADE PATTERN: single entry point for all GUI operations, hides 9 subsystems"

REFACTOR every GUI controller:
- DashboardController: replace SmartHomeHub.getInstance() calls with homeController
- EventHistoryController: use homeController.recentEvents() instead of direct DAO
- CommandHistoryController: use homeController.recentCommands() + homeController.undoLastCommand()
- UserManagementController: use homeController.allUsers() / addUser / deleteUser
- ScheduleEditorController: use homeController.allSchedules() etc.

HOW TO GET HomeController into controllers:
- FXML controllers are instantiated by FXMLLoader — you can't pass constructor args
- Option A (recommended): HomeController is a Singleton → HomeController.getInstance() from @FXML initialize()
- Option B: expose a setter that Main calls after load — more complex, skip for this project

VERIFICATION after refactor:
- grep the gui/ package for "SmartHomeHub" → should only appear in HomeController import list (zero elsewhere)
- grep for "DAO" imports in gui/ → should be zero
- grep for "CommandInvoker" in gui/ → should be zero
- App still works exactly as before from the user's perspective

Tests: src/test/java/facade/HomeControllerTest.java
- homeController.allRooms() returns expected count
- homeController.setMode("ECO") applies EcoMode
- homeController.executeCommand(new TurnOnCommand(light)) + canUndo() → true
- homeController.undoLastCommand() reverses the command

AFTER:
- mvn test
- mvn javafx:run
- Verify app works unchanged
- Report grep results proving GUI only depends on HomeController
```

---

## Acceptance Criteria
- [ ] HomeController exposes high-level methods only
- [ ] Zero Hub/DAO/Invoker imports in gui/ package
- [ ] App still works end-to-end
- [ ] Tests pass

## Commit Message
```
T-19: Facade pattern — HomeController wraps all subsystems for GUI
```

---

# Prompt 20 — SecurityContext

**Task ID:** T-20
**Owner:** M4
**Days:** 5–7
**Prerequisites:** T-12 (UserDAO)

---

## Paste this into Claude Code

```
Create the SecurityContext: manages current user, PIN verification, and lockout after 3 failed attempts.

CREATE: src/main/java/security/SecurityContext.java

Pattern: regular class, NOT a Singleton (tested more easily, held by HomeController).

Fields:
- private final UserDAO userDAO
- private UserAccount currentUser (null when not logged in)
- private int failedAttempts = 0
- private long lockoutUntilMillis = 0

Methods:
- constructor(UserDAO)
- public boolean login(String name, String pin):
  - If now < lockoutUntilMillis, return false immediately (locked out)
  - Hash the pin (SHA-256 via PinHasher)
  - Query userDAO.verifyPin(name, hash)
  - If match: currentUser = userDAO.findByName(name), reset failedAttempts, return true
  - If no match: failedAttempts++; if failedAttempts >= 3: set lockoutUntilMillis = now + 30_000 (30s lockout), reset failedAttempts; return false
- public void logout(): currentUser = null
- public UserAccount currentUser(): defensive (returns null if not logged in)
- public boolean hasCurrentUser(): currentUser != null
- public boolean verifyPinForAction(String pin): requires current user logged in AND correct pin for a specific action; used by SecureDevice decorator
- public boolean isLockedOut(): returns now < lockoutUntilMillis

Thread safety: all methods synchronized.

Top comment: "// SECURITY: login/logout, PIN verification, failed-attempt lockout"

Tests: src/test/java/security/SecurityContextTest.java
- login with correct PIN → succeeds, currentUser set
- login with wrong PIN once → fails, no lockout yet
- login with wrong PIN 3 times → locked
- After lockout, even correct PIN → fails
- After 30s + correct PIN → works
- logout → currentUser null
- Use an in-memory SQLite + seed a test user

AFTER:
- mvn test
- Notify M1: SecurityContext is ready for SecureDevice decorator (T-21)
- Notify M3: verifyPinForAction is the method to call for protected buttons
```

---

## Acceptance Criteria
- [ ] Lockout after exactly 3 failures within the window
- [ ] Correct PIN logs in; wrong PIN increments counter
- [ ] Tests cover timing (use `Thread.sleep` for lockout expiry OR inject a clock for testability)

## Commit Message
```
T-20: SecurityContext — login, PIN verification, lockout after 3 failures
```

---

# Prompt 21 — Decorator Pattern (SecureDevice, LoggedDevice)

**Task ID:** T-21
**Owner:** M1
**Days:** 9–11
**Prerequisites:** T-02, T-20, T-13

---

## Paste this into Claude Code

```
Implement the Decorator pattern: wrap any Device to add behavior without modifying the Device class.

CREATE:

1. src/main/java/devices/decorator/DeviceDecorator.java
   - Abstract class extending Device
   - Holds a reference: protected final Device wrapped;
   - Constructor: takes Device to wrap, passes wrapped.getName() + wrapped.getRoom() to super
   - Delegates ALL methods to wrapped by default: getId, getName, getState, setState, getType
   - setState delegates but decorators override for special behavior
   - Top comment: "// DECORATOR PATTERN: base class wrapping a Device"

2. src/main/java/devices/decorator/SecureDevice.java
   - extends DeviceDecorator
   - Constructor: takes Device + SecurityContext + requiredPinAction (String — e.g. "UNLOCK_DOOR")
   - Override setState(String newState):
     - Check securityContext.verifyPinForAction(pin)... 
     - WAIT: this needs the pin provided at call time. Problem: setState doesn't accept pin.
   - REVISED DESIGN: SecureDevice checks securityContext.hasCurrentUser() && !securityContext.isLockedOut()
     - If both true: allow the action, delegate to wrapped.setState(newState)
     - Otherwise: throw SecurityException("PIN required for this action")
   - Top comment: "// DECORATOR: requires logged-in user for state changes"

3. src/main/java/devices/decorator/LoggedDevice.java
   - extends DeviceDecorator
   - Holds a reference to DeviceEventDAO (constructor injection)
   - Override setState(String newState):
     - oldState = wrapped.getState()
     - wrapped.setState(newState)
     - eventDAO.insert(wrapped.getId(), "DECORATOR_LOG", oldState, newState)
   - Top comment: "// DECORATOR: auto-logs every state change"

4. src/main/java/devices/decorator/RateLimitedDevice.java (bonus — if time)
   - extends DeviceDecorator
   - Tracks lastActionMillis
   - Override setState: if now - lastActionMillis < 1000, throw IllegalStateException("Too fast")
   - Top comment: "// DECORATOR: enforces min 1 second between actions"

5. Tests: src/test/java/devices/decorator/
   - SecureDeviceTest: without login → setState throws. After login → works.
   - LoggedDeviceTest: setState on wrapped → DB row appears
   - RateLimitedDeviceTest: 2 setStates in quick succession → second throws

DEMONSTRATE STACKING in a test:
- Wrap: new LoggedDevice(new SecureDevice(light, sec, "any"), eventDAO)
- Set state via the outermost → both decorators fire in order

AFTER:
- mvn test
- Report
```

---

## Acceptance Criteria
- [ ] Decorators stack correctly
- [ ] Security check blocks unauthorized actions
- [ ] LoggedDevice logs to DB

## Commit Message
```
T-21: Decorator pattern — SecureDevice + LoggedDevice (+ RateLimitedDevice)
```

---

# Prompt 22 — Safety Rules Engine

**Task ID:** T-22
**Owner:** M4
**Days:** 8–11
**Prerequisites:** T-05 (Observer), T-03 (Hub with mode)

---

## Paste this into Claude Code

```
Create the safety rules engine — observes all devices and fires rules when dangerous patterns occur.

CREATE:

1. src/main/java/safety/SafetyRule.java
   - Interface:
     - boolean applies(Device d, String event, SmartHomeHub hub);
     - void fire(Device d, SmartHomeHub hub);
     - String description();
   - Top comment: "// SAFETY: pluggable rule interface"

2. src/main/java/safety/MotionInAwayModeRule.java
   - applies(): event equals "STATE_CHANGED" AND device is SecurityCamera in MOTION_DETECTED state AND hub.currentMode is AwayMode
   - fire(): iterate hub.allDevices() — lock all DoorLocks, set all Lights ON, log via System.err
   - description() = "Motion detected while Away — locking doors, lights on"

3. src/main/java/safety/HighTemperatureRule.java
   - applies(): device is Thermostat AND thermostat.getCurrentTemp() > 40.0
   - fire(): log red alert (for now: System.err)
   - description() = "Temperature exceeds 40°C — potential fire"

4. src/main/java/safety/DoorUnlockedTooLongRule.java
   - Tracks state: Map<String(deviceId), Long(unlockTimeMs)>
   - applies(): device is DoorLock AND its state is UNLOCKED
     - If first unlock → record timestamp, applies=false
     - If already recorded AND (now - timestamp) > 5*60*1000 → applies=true
     - On LOCKED state → remove from map
   - fire(): warning alert
   - description() = "Door unlocked > 5 minutes"

5. src/main/java/safety/SafetyRulesEngine.java
   - Implements Observer
   - Fields: List<SafetyRule> rules, SmartHomeHub hub reference, boolean enabled=true
   - register() method — attach self as observer to all devices in hub; auto-attach when new devices added
   - update(Device, String event):
     - If !enabled: return
     - For each rule: if rule.applies(d, event, hub): rule.fire(d, hub)
     - IMPORTANT: prevent infinite loops — rules that fire device commands will trigger another update; guard by checking "is this my own rule's change?" via a thread-local flag
   - Register this on Hub startup

Add integration in HomeController / Main:
- After Hub init + devices loaded, create SafetyRulesEngine with default 3 rules, call register()
- SafetyRulesEngine can be disabled via config (optional)

6. Tests: src/test/java/safety/
   - MotionInAwayModeRuleTest: setup hub in Away mode, trigger camera motion, verify doors lock
   - HighTemperatureRuleTest
   - DoorUnlockedTooLongRuleTest

AFTER:
- mvn test
- Report
```

---

## Acceptance Criteria
- [ ] 3 safety rules implemented
- [ ] Engine observes all devices without modifying Device class
- [ ] Rules don't cause infinite loops
- [ ] Tests verify each rule triggers correctly

## Commit Message
```
T-22: Safety rules engine (motion+Away, high temp, door left open)
```

---

# Prompt 23 — Pattern Coverage Tests

**Task ID:** T-23
**Owner:** M1 + M4
**Days:** 13–14
**Prerequisites:** All patterns implemented

---

## Paste this into Claude Code

```
Create tests that specifically prove each of the 9 design patterns is correctly implemented. The professor will grade these — make the pattern identity OBVIOUS.

Create a top-level test class per pattern if not already done:

src/test/java/patterns/
├── SingletonPatternTest.java     (may already exist from T-03)
├── IteratorPatternTest.java      (may already exist from T-04)
├── ObserverPatternTest.java      (may already exist from T-05)
├── AbstractFactoryPatternTest.java (may already exist from T-06)
├── StrategyPatternTest.java      (may already exist from T-07)
├── DAOPatternTest.java           (new — demonstrates DAO pattern end-to-end)
├── CommandPatternTest.java       (new — demonstrates Command incl. undo)
├── FacadePatternTest.java        (new — demonstrates HomeController simplifies access)
└── DecoratorPatternTest.java     (new — demonstrates stacking decorators)

For each, @DisplayName clearly identifies the pattern:
@DisplayName("Singleton pattern: SmartHomeHub has a single instance accessible via getInstance()")

Each test class should have 3-5 tests that prove ESSENTIAL pattern properties:
- Singleton: one instance, private constructor
- Iterator: Enumeration returned, traversal works, encapsulation preserved
- Observer: attach/detach/notify flow
- Abstract Factory: concrete factories produce specific families
- Strategy: runtime strategy swap changes behavior
- DAO: separation between domain and SQL; in-memory DB CRUD round trip
- Command: execute + undo round trip works; history is bounded
- Facade: GUI depends on one class; HomeController method count > some threshold
- Decorator: wrapping a Device adds behavior; wrapped reference preserved

AFTER:
- mvn test
- Count total tests. Target: 60+ tests across the whole project.
- Report any gaps
```

---

## Acceptance Criteria
- [ ] Pattern-specific test classes named for visibility
- [ ] Each pattern has at least 3 tests
- [ ] 60+ tests total

## Commit Message
```
T-23: comprehensive pattern coverage tests
```

---

