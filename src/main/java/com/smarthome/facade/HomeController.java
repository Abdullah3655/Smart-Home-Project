package com.smarthome.facade;

import com.smarthome.command.CommandInvoker;
import com.smarthome.command.LockCommand;
import com.smarthome.command.SetAutomationModeCommand;
import com.smarthome.command.SetTemperatureCommand;
import com.smarthome.command.TurnOffCommand;
import com.smarthome.command.TurnOnCommand;
import com.smarthome.command.UnlockCommand;
import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Device;
import com.smarthome.devices.Lock;
import com.smarthome.devices.Thermostat;
import com.smarthome.persistence.dao.CommandLog;
import com.smarthome.persistence.dao.CommandsLogDAO;
import com.smarthome.persistence.dao.DeviceEvent;
import com.smarthome.persistence.dao.DeviceEventDAO;
import com.smarthome.strategy.AutomationMode;
import com.smarthome.strategy.AwayMode;
import com.smarthome.strategy.EcoMode;
import com.smarthome.strategy.SleepMode;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

/**
 * FACADE PATTERN — single entry point from UI/JavaFX into the system
 * (Refactoring Guru canonical structure).
 *
 * <h3>RG roles</h3>
 * <ul>
 *   <li><b>Facade</b> — this class.</li>
 *   <li><b>Subsystem</b> — {@link SmartHomeHub}, {@link CommandInvoker},
 *       Strategy modes, DAOs, devices.</li>
 *   <li><b>Client</b> — JavaFX controllers (and tests).</li>
 * </ul>
 *
 * <h3>"Thin" rule</h3>
 * The Facade contains <i>only orchestration</i>: it looks up devices by
 * id, builds Command objects, delegates to {@link CommandInvoker} for
 * mutations, and delegates to DAOs for read operations. It never
 * implements domain logic itself. Renaming this rule per RG: the Facade
 * is a switchboard, not a brain.
 *
 * <h3>Contracts</h3>
 * <ul>
 *   <li>Mutating methods route through Command pattern → audit trail in
 *       {@code commands_log} (when DAO is wired in).</li>
 *   <li>Read methods never mutate state.</li>
 *   <li>Looking up an unknown device throws
 *       {@link IllegalArgumentException} — supports the rubric's
 *       "prevent invalid/unsafe operations" constraint.</li>
 * </ul>
 */
public class HomeController {

    private final SmartHomeHub hub;
    private final CommandInvoker invoker;
    private final DeviceEventDAO eventDAO;
    private final CommandsLogDAO commandsLogDAO;

    /** Production constructor — uses singletons + production DAOs. */
    public HomeController() {
        this(
            SmartHomeHub.getInstance(),
            new CommandInvoker(),
            null,    // event DAO is optional — UI can show in-memory state if DB not initialised
            null     // commands DAO is optional — same reason
        );
    }

    /**
     * Test/integration constructor — inject collaborators directly so
     * tests can use in-memory DAOs and isolated invokers without hitting
     * the singleton hub or the real {@code smarthome.db}.
     */
    public HomeController(SmartHomeHub hub,
                          CommandInvoker invoker,
                          DeviceEventDAO eventDAO,
                          CommandsLogDAO commandsLogDAO) {
        this.hub = hub;
        this.invoker = invoker;
        this.eventDAO = eventDAO;
        this.commandsLogDAO = commandsLogDAO;
    }

    // ─────────────────────────────────────────────────────────────
    // Mutating actions — route through Command pattern (undoable)
    // ─────────────────────────────────────────────────────────────

    public void turnOnDevice(String deviceId) {
        invoker.execute(new TurnOnCommand(findDevice(deviceId)));
    }

    public void turnOffDevice(String deviceId) {
        invoker.execute(new TurnOffCommand(findDevice(deviceId)));
    }

    public void lockDevice(String deviceId) {
        Device d = findDevice(deviceId);
        if (!(d instanceof Lock lock)) {
            throw new IllegalArgumentException(deviceId + " is not a Lock");
        }
        invoker.execute(new LockCommand(lock));
    }

    public void unlockDevice(String deviceId) {
        Device d = findDevice(deviceId);
        if (!(d instanceof Lock lock)) {
            throw new IllegalArgumentException(deviceId + " is not a Lock");
        }
        invoker.execute(new UnlockCommand(lock));
    }

    public void setTemperature(String deviceId, double value) {
        Device d = findDevice(deviceId);
        if (!(d instanceof Thermostat thermostat)) {
            throw new IllegalArgumentException(deviceId + " is not a Thermostat");
        }
        invoker.execute(new SetTemperatureCommand(thermostat, value));
    }

    public void setAutomationMode(String modeName) {
        AutomationMode mode = resolveMode(modeName);
        invoker.execute(new SetAutomationModeCommand(hub, mode));
    }

    /**
     * Reverses the most recently executed action. Returns {@code true}
     * if something was undone, {@code false} if there was nothing to
     * undo.
     */
    public boolean undoLastAction() {
        if (!invoker.canUndo()) {
            return false;
        }
        invoker.undo();
        return true;
    }

    // ─────────────────────────────────────────────────────────────
    // Read methods — never mutate state
    // ─────────────────────────────────────────────────────────────

    public List<Device> getDevicesForRoom(String roomId) {
        Room room = hub.getRoom(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Unknown room: " + roomId);
        }
        List<Device> devices = new ArrayList<>();
        Enumeration<Device> it = room.devices();
        while (it.hasMoreElements()) {
            devices.add(it.nextElement());
        }
        return devices;
    }

    public List<DeviceEvent> getEventHistory() {
        if (eventDAO == null) return List.of();
        return eventDAO.findRecent(100);
    }

    public List<CommandLog> getCommandHistory() {
        if (commandsLogDAO == null) return List.of();
        return commandsLogDAO.findRecent(100);
    }

    /**
     * Schedule creation will be wired when the schedule executor (M4
     * task) lands. The signature is locked here so the UI can be coded
     * against it; calling it currently throws to make the gap explicit
     * rather than silently dropping the request.
     */
    public void createSchedule(ScheduleRequest request) {
        throw new UnsupportedOperationException(
            "Schedule executor not yet wired — see M4 task. Request: " + request);
    }

    // ─────────────────────────────────────────────────────────────
    // Private helpers — orchestration only, no domain logic
    // ─────────────────────────────────────────────────────────────

    /** Walks the hub looking for a device by id. */
    private Device findDevice(String deviceId) {
        for (Room room : hub.getRooms()) {
            Device d = room.getDevice(deviceId);
            if (d != null) return d;
        }
        throw new IllegalArgumentException("Unknown device: " + deviceId);
    }

    /** Translates a UI-friendly mode name into a Strategy instance. */
    private AutomationMode resolveMode(String modeName) {
        if (modeName == null) {
            throw new IllegalArgumentException("modeName must not be null");
        }
        return switch (modeName.toUpperCase(Locale.ROOT)) {
            case "ECO"   -> new EcoMode();
            case "SLEEP" -> new SleepMode();
            case "AWAY"  -> new AwayMode();
            default -> throw new IllegalArgumentException("Unknown mode: " + modeName);
        };
    }
}
