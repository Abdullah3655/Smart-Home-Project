package com.smarthome.persistence;

import com.smarthome.core.Room;
import com.smarthome.persistence.dao.CommandLog;
import com.smarthome.persistence.dao.CommandsLogDAO;
import com.smarthome.persistence.dao.DeviceEvent;
import com.smarthome.persistence.dao.DeviceEventDAO;
import com.smarthome.persistence.dao.RoomDAO;
import com.smarthome.persistence.dao.User;
import com.smarthome.persistence.dao.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests use {@code Database.forUrl("jdbc:sqlite::memory:")} so each test
 * gets an isolated in-memory SQLite — the real {@code smarthome.db} file
 * is never touched.
 */
class PersistenceTest {

    private Database db;

    @BeforeEach
    void setUp() {
        // Each test gets a fresh in-memory database with the full schema.
        db = Database.forUrl("jdbc:sqlite::memory:");
    }

    @Test
    void schemaCreatesAllSixTables() throws Exception {
        java.util.Set<String> tables = new java.util.HashSet<>();
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT name FROM sqlite_master WHERE type='table'")) {
            while (rs.next()) {
                tables.add(rs.getString("name"));
            }
        }
        assertTrue(tables.contains("users"),         "users table missing");
        assertTrue(tables.contains("rooms"),         "rooms table missing");
        assertTrue(tables.contains("devices"),       "devices table missing");
        assertTrue(tables.contains("device_events"), "device_events table missing");
        assertTrue(tables.contains("schedules"),     "schedules table missing");
        assertTrue(tables.contains("commands_log"),  "commands_log table missing");
    }

    @Test
    void userDaoRoundTrip() {
        UserDAO dao = new UserDAO(db.getConnection());
        User alice = new User("u1", "Alice", "1234", "ADMIN");
        dao.insert(alice);

        User loaded = dao.findById("u1");
        assertNotNull(loaded);
        assertEquals("Alice", loaded.name());
        assertEquals("ADMIN", loaded.role());
    }

    @Test
    void verifyPinAcceptsCorrectAndRejectsWrong() {
        UserDAO dao = new UserDAO(db.getConnection());
        dao.insert(new User("u2", "Bob", "0000", "USER"));

        assertTrue(dao.verifyPin("u2", "0000"));
        assertFalse(dao.verifyPin("u2", "9999"));
        assertFalse(dao.verifyPin("does-not-exist", "0000"));
    }

    @Test
    void roomDaoFindAllReturnsInsertedRooms() {
        RoomDAO dao = new RoomDAO(db.getConnection());
        dao.insert(new Room("r1", "Kitchen"));
        dao.insert(new Room("r2", "Bedroom"));

        List<Room> all = dao.findAll();
        assertEquals(2, all.size());
        // findAll orders by name, so Bedroom first
        assertEquals("Bedroom", all.get(0).getName());
        assertEquals("Kitchen", all.get(1).getName());
    }

    @Test
    void roomDeleteRemovesRow() {
        RoomDAO dao = new RoomDAO(db.getConnection());
        dao.insert(new Room("r1", "Kitchen"));
        assertNotNull(dao.findById("r1"));

        dao.delete("r1");
        assertNull(dao.findById("r1"));
    }

    @Test
    void deviceEventDaoStoresAndRetrievesEvents() {
        DeviceEventDAO dao = new DeviceEventDAO(db.getConnection());
        String deviceId = UUID.randomUUID().toString();

        dao.insert(deviceId, "TURNED_ON");
        dao.insert(deviceId, "TURNED_OFF");
        dao.insert("other-device", "LOCKED");

        List<DeviceEvent> recent = dao.findRecent(10);
        assertEquals(3, recent.size());
        // Most recent first
        assertEquals("LOCKED", recent.get(0).eventType());

        List<DeviceEvent> forDevice = dao.findByDevice(deviceId, 10);
        assertEquals(2, forDevice.size());
    }

    @Test
    void commandsLogDaoStoresAuditTrail() {
        CommandsLogDAO dao = new CommandsLogDAO(db.getConnection());

        dao.insert("cmd-1", "dev-1", "TURN_ON", "{}", "OK");
        dao.insert("cmd-2", "dev-1", "TURN_OFF", "{}", "OK");
        dao.insert("cmd-3", "dev-2", "LOCK", "{}", "OK");

        List<CommandLog> all = dao.findRecent(10);
        assertEquals(3, all.size());

        List<CommandLog> dev1Only = dao.findByDevice("dev-1", 10);
        assertEquals(2, dev1Only.size());
        assertTrue(dev1Only.stream().allMatch(cl -> cl.deviceId().equals("dev-1")));
    }

    @Test
    void deviceDaoRoundTripsLightAcrossSubtypeAndFamily() {
        // Seed a room first (FK constraint)
        new RoomDAO(db.getConnection()).insert(new com.smarthome.core.Room("kitchen", "Kitchen"));

        com.smarthome.devices.Light original = (com.smarthome.devices.Light)
            new com.smarthome.factory.Version2DeviceFactory().createLight("Kitchen Light");
        original.turnOn();
        original.setBrightness(75);

        com.smarthome.persistence.dao.DeviceDAO dao =
            new com.smarthome.persistence.dao.DeviceDAO(db.getConnection());
        dao.insert(original, "kitchen");

        com.smarthome.devices.Device loaded = dao.findById(original.getId());
        assertNotNull(loaded);
        assertEquals(original.getId(), loaded.getId());
        assertEquals("Kitchen Light", loaded.getName());
        assertTrue(loaded.isPoweredOn());
        // Reconstructed as a Version2Light because the row stored family=VERSION2
        assertTrue(loaded instanceof com.smarthome.devices.version2.Version2Light);
        assertEquals(75, ((com.smarthome.devices.Light) loaded).getBrightness());
    }

    @Test
    void deviceDaoRoundTripsAcrossDeviceTypes() {
        new RoomDAO(db.getConnection()).insert(new com.smarthome.core.Room("home", "Home"));
        com.smarthome.persistence.dao.DeviceDAO dao =
            new com.smarthome.persistence.dao.DeviceDAO(db.getConnection());

        com.smarthome.factory.DeviceFactory factory = new com.smarthome.factory.Version2DeviceFactory();
        com.smarthome.devices.Device thermo = factory.createThermostat("Living Thermo");
        com.smarthome.devices.Device lock   = factory.createDoorLock("Front Door");
        com.smarthome.devices.Device camera = factory.createCamera("Hall Cam");

        ((com.smarthome.devices.Thermostat) thermo).setTemperature(19.5);
        ((com.smarthome.devices.Lock) lock).lock();
        camera.turnOn();

        dao.insert(thermo, "home");
        dao.insert(lock, "home");
        dao.insert(camera, "home");

        List<com.smarthome.devices.Device> loaded = dao.findByRoom("home");
        assertEquals(3, loaded.size());
    }

    @Test
    void deviceDaoPreservesVersion1FamilyOnReload() {
        new RoomDAO(db.getConnection()).insert(new com.smarthome.core.Room("legacy-room", "Legacy"));
        com.smarthome.persistence.dao.DeviceDAO dao =
            new com.smarthome.persistence.dao.DeviceDAO(db.getConnection());

        com.smarthome.devices.Device legacyLight =
            new com.smarthome.factory.Version1DeviceFactory().createLight("Old Lamp");
        dao.insert(legacyLight, "legacy-room");

        com.smarthome.devices.Device loaded = dao.findById(legacyLight.getId());
        // Round-trip preserves the family — Version1 stays Version1
        assertTrue(loaded instanceof com.smarthome.devices.version1.Version1Light);
    }
}
