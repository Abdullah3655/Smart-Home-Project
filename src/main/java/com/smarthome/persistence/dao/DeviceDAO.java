package com.smarthome.persistence.dao;

import com.smarthome.devices.Camera;
import com.smarthome.devices.Device;
import com.smarthome.devices.Light;
import com.smarthome.devices.Lock;
import com.smarthome.devices.Thermostat;
import com.smarthome.devices.version1.Version1Camera;
import com.smarthome.devices.version1.Version1Light;
import com.smarthome.devices.version1.Version1Lock;
import com.smarthome.devices.version1.Version1Thermostat;
import com.smarthome.devices.version2.Version2Camera;
import com.smarthome.devices.version2.Version2Light;
import com.smarthome.devices.version2.Version2Lock;
import com.smarthome.devices.version2.Version2Thermostat;
import com.smarthome.persistence.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * DAO PATTERN — Device table accessor.
 *
 * <p>Round-trips concrete {@link Device} subclasses through a single SQL
 * row. The schema captures (type, family, powered_on, state_blob); on
 * read, we use the saved (type, family) pair to pick the right concrete
 * constructor, building the same Version1/Version2 variant the device
 * was created as.</p>
 *
 * <p>This is the runtime expression of the Abstract Factory pattern that
 * the {@link com.smarthome.factory.DeviceFactory} hierarchy declares.
 * Per Refactoring Guru: <i>"work with various families of related
 * products without depending on their concrete classes."</i> The DAO
 * exposes only {@link Device}; the database row decides which family
 * variant gets reconstructed.</p>
 *
 * <p>Type-specific state lives in a {@code key=value;key=value} blob to
 * avoid pulling in a JSON dependency. Light writes brightness, Thermostat
 * writes temp, Lock writes locked. State is restored via the public
 * setters — but the {@link com.smarthome.ui.App} startup sequence loads
 * devices BEFORE attaching the {@link com.smarthome.ui.DaoEventBridge}
 * observer, so the restoration setters fire silently (no phantom events
 * in {@code device_events}).</p>
 */
public class DeviceDAO {
    private final Connection conn;

    public DeviceDAO() {
        this(Database.getInstance().getConnection());
    }

    public DeviceDAO(Connection conn) {
        this.conn = Objects.requireNonNull(conn, "conn must not be null");
    }

    // ─────────────────────────────────────────────────────────────
    // Insert / update (idempotent — used by seed and Add Device flow)
    // ─────────────────────────────────────────────────────────────

    public void insert(Device device, String roomId) {
        Objects.requireNonNull(device, "device must not be null");
        Objects.requireNonNull(roomId, "roomId must not be null");

        if (findById(device.getId()) != null) {
            updateAll(device, roomId);
            return;
        }

        String sql = "INSERT INTO devices "
                   + "(device_id, name, type, family, room_id, powered_on, state_blob) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, device.getId());
            stmt.setString(2, device.getName());
            stmt.setString(3, classifyType(device));
            stmt.setString(4, classifyFamily(device));
            stmt.setString(5, roomId);
            stmt.setInt(6, device.isPoweredOn() ? 1 : 0);
            stmt.setString(7, serializeState(device));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert device " + device.getId(), e);
        }
    }

    public void updateAll(Device device, String roomId) {
        String sql = "UPDATE devices "
                   + "SET name = ?, type = ?, family = ?, room_id = ?, "
                   + "    powered_on = ?, state_blob = ? "
                   + "WHERE device_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, device.getName());
            stmt.setString(2, classifyType(device));
            stmt.setString(3, classifyFamily(device));
            stmt.setString(4, roomId);
            stmt.setInt(5, device.isPoweredOn() ? 1 : 0);
            stmt.setString(6, serializeState(device));
            stmt.setString(7, device.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update device " + device.getId(), e);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Read
    // ─────────────────────────────────────────────────────────────

    public Device findById(String deviceId) {
        String sql = "SELECT device_id, name, type, family, powered_on, state_blob "
                   + "FROM devices WHERE device_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, deviceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return deserialize(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find device " + deviceId, e);
        }
    }

    public List<Device> findByRoom(String roomId) {
        List<Device> devices = new ArrayList<>();
        String sql = "SELECT device_id, name, type, family, powered_on, state_blob "
                   + "FROM devices WHERE room_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    devices.add(deserialize(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load devices for room " + roomId, e);
        }
        return devices;
    }

    public void delete(String deviceId) {
        String sql = "DELETE FROM devices WHERE device_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, deviceId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete device " + deviceId, e);
        }
    }

    /**
     * Updates only the volatile columns (name, powered_on, state_blob)
     * for an existing device — used by the {@link com.smarthome.ui.DaoEventBridge}
     * to keep the persisted state in sync after every Observer event.
     * Silently no-ops if the device isn't in the table (the row is created
     * by {@link #insert} on first save).
     */
    public void updateState(Device device) {
        String sql = "UPDATE devices "
                   + "SET name = ?, powered_on = ?, state_blob = ? "
                   + "WHERE device_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, device.getName());
            stmt.setInt(2, device.isPoweredOn() ? 1 : 0);
            stmt.setString(3, serializeState(device));
            stmt.setString(4, device.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update device state " + device.getId(), e);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Reconstruction — Abstract Factory at runtime
    // ─────────────────────────────────────────────────────────────

    private Device deserialize(ResultSet rs) throws SQLException {
        String id        = rs.getString("device_id");
        String name      = rs.getString("name");
        String type      = rs.getString("type");
        String family    = rs.getString("family");
        boolean poweredOn = rs.getInt("powered_on") != 0;
        String state     = rs.getString("state_blob");

        Device device = constructByTypeAndFamily(id, name, type, family);

        if (poweredOn) {
            device.turnOn();
        }
        applyState(device, state);

        return device;
    }

    /**
     * Constructs the right concrete Device variant directly. Bypasses the
     * factory's UUID generator so the saved id is preserved across
     * restarts — keeps room indexing and audit-log linking consistent.
     */
    private Device constructByTypeAndFamily(String id, String name, String type, String family) {
        boolean v2 = !"VERSION1".equalsIgnoreCase(family);
        return switch (type.toUpperCase(Locale.ROOT)) {
            case "LIGHT"      -> v2 ? new Version2Light(id, name)      : new Version1Light(id, name);
            case "THERMOSTAT" -> v2 ? new Version2Thermostat(id, name) : new Version1Thermostat(id, name);
            case "LOCK"       -> v2 ? new Version2Lock(id, name)       : new Version1Lock(id, name);
            case "CAMERA"     -> v2 ? new Version2Camera(id, name)     : new Version1Camera(id, name);
            default -> throw new IllegalArgumentException("Unknown device type: " + type);
        };
    }

    /**
     * Restores type-specific state via public setters. Safe because
     * App.start() loads devices BEFORE attaching DaoEventBridge — the
     * setters fire notifyObservers, but no observer is listening yet, so
     * no audit-log spam.
     */
    private void applyState(Device device, String state) {
        Map<String, String> kv = parseState(state);
        if (device instanceof Light light && kv.containsKey("brightness")) {
            try {
                light.setBrightness(Integer.parseInt(kv.get("brightness")));
            } catch (NumberFormatException ignored) {}
        } else if (device instanceof Thermostat thermo && kv.containsKey("temp")) {
            try {
                thermo.setTemperature(Double.parseDouble(kv.get("temp")));
            } catch (NumberFormatException ignored) {}
        } else if (device instanceof Lock lock && kv.containsKey("locked")) {
            if (Boolean.parseBoolean(kv.get("locked"))) {
                lock.lock();
            } else {
                lock.unlock();
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Serialization helpers
    // ─────────────────────────────────────────────────────────────

    private String classifyType(Device d) {
        if (d instanceof Light)      return "LIGHT";
        if (d instanceof Thermostat) return "THERMOSTAT";
        if (d instanceof Lock)       return "LOCK";
        if (d instanceof Camera)     return "CAMERA";
        return "UNKNOWN";
    }

    private String classifyFamily(Device d) {
        String pkg = d.getClass().getPackageName();
        if (pkg.contains("version1")) return "VERSION1";
        return "VERSION2";  // default — base classes treated as current generation
    }

    private String serializeState(Device d) {
        if (d instanceof Light light) {
            return "brightness=" + light.getBrightness();
        }
        if (d instanceof Thermostat thermo) {
            return "temp=" + thermo.getTemperature();
        }
        if (d instanceof Lock lock) {
            return "locked=" + lock.isLocked();
        }
        return "";
    }

    private Map<String, String> parseState(String blob) {
        Map<String, String> kv = new HashMap<>();
        if (blob == null || blob.isBlank()) return kv;
        for (String pair : blob.split(";")) {
            int eq = pair.indexOf('=');
            if (eq > 0 && eq < pair.length() - 1) {
                kv.put(pair.substring(0, eq).trim(), pair.substring(eq + 1).trim());
            }
        }
        return kv;
    }
}
