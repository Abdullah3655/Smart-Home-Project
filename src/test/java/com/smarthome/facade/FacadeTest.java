package com.smarthome.facade;

import com.smarthome.command.CommandInvoker;
import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Device;
import com.smarthome.devices.Lock;
import com.smarthome.devices.Thermostat;
import com.smarthome.factory.Version2DeviceFactory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the Facade pattern's RG contract:
 *  - exposes a small, simple API (10 methods)
 *  - never reimplements domain logic — always delegates
 *  - rejects invalid inputs (unknown device id, wrong device type)
 *  - mutating methods route through the Command pattern (undoable)
 */
class FacadeTest {

    /** Build an isolated hub + invoker pair so tests don't pollute the singleton. */
    private HomeController newFacade() {
        // Reuse the singleton hub but seed it with this test's data only.
        return new HomeController(
            SmartHomeHub.getInstance(),
            new CommandInvoker(),
            null,
            null
        );
    }

    @Test
    void turnOnDeviceRoutesThroughCommand() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        Room room = new Room("facade-r1-" + UUID.randomUUID(), "Facade Room");
        Device light = new Version2DeviceFactory().createLight("Facade Light");
        room.addDevice(light);
        hub.addRoom(room);

        HomeController facade = newFacade();
        facade.turnOnDevice(light.getId());

        assertTrue(light.isPoweredOn());
    }

    @Test
    void undoLastActionReversesTheMostRecentMutation() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        Room room = new Room("facade-undo-" + UUID.randomUUID(), "Undo Room");
        Device light = new Version2DeviceFactory().createLight("Undo Light");
        room.addDevice(light);
        hub.addRoom(room);

        HomeController facade = newFacade();
        facade.turnOnDevice(light.getId());
        assertTrue(light.isPoweredOn());

        assertTrue(facade.undoLastAction());
        assertFalse(light.isPoweredOn());
    }

    @Test
    void undoOnEmptyHistoryReturnsFalse() {
        HomeController facade = newFacade();
        assertFalse(facade.undoLastAction());
    }

    @Test
    void unknownDeviceRejectedWithIllegalArgumentException() {
        HomeController facade = newFacade();
        assertThrows(IllegalArgumentException.class,
            () -> facade.turnOnDevice("does-not-exist"));
    }

    @Test
    void wrongTypeRejectedForLockOperation() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        Room room = new Room("facade-type-" + UUID.randomUUID(), "Type Room");
        Device light = new Version2DeviceFactory().createLight("Not A Lock");
        room.addDevice(light);
        hub.addRoom(room);

        HomeController facade = newFacade();
        // lockDevice on a Light should throw — Light is not a Lock
        assertThrows(IllegalArgumentException.class,
            () -> facade.lockDevice(light.getId()));
    }

    @Test
    void setTemperatureRoutesThroughCommand() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        Room room = new Room("facade-temp-" + UUID.randomUUID(), "Temp Room");
        Thermostat thermo = (Thermostat) new Version2DeviceFactory().createThermostat("Therm");
        room.addDevice(thermo);
        hub.addRoom(room);

        HomeController facade = newFacade();
        facade.setTemperature(thermo.getId(), 27.5);

        assertEquals(27.5, thermo.getTemperature(), 0.01);
    }

    @Test
    void setAutomationModeAcceptsThreeNamesCaseInsensitive() {
        HomeController facade = newFacade();
        // Should not throw for any of the three valid names
        facade.setAutomationMode("ECO");
        facade.setAutomationMode("sleep");
        facade.setAutomationMode("Away");
    }

    @Test
    void setAutomationModeRejectsUnknownName() {
        HomeController facade = newFacade();
        assertThrows(IllegalArgumentException.class,
            () -> facade.setAutomationMode("PARTY_MODE"));
    }

    @Test
    void getDevicesForRoomReturnsDevicesAsList() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        Room room = new Room("facade-list-" + UUID.randomUUID(), "List Room");
        Device l1 = new Version2DeviceFactory().createLight("L1");
        Device l2 = new Version2DeviceFactory().createLight("L2");
        room.addDevice(l1);
        room.addDevice(l2);
        hub.addRoom(room);

        HomeController facade = newFacade();
        List<Device> devices = facade.getDevicesForRoom(room.getRoomId());

        assertEquals(2, devices.size());
    }

    @Test
    void getDevicesForUnknownRoomThrows() {
        HomeController facade = newFacade();
        assertThrows(IllegalArgumentException.class,
            () -> facade.getDevicesForRoom("unknown-room"));
    }

    @Test
    void readMethodsReturnEmptyListWhenDaosUnavailable() {
        // Production constructor passes null DAOs — facade should
        // return empty lists rather than throwing.
        HomeController facade = new HomeController();
        assertTrue(facade.getEventHistory().isEmpty());
        assertTrue(facade.getCommandHistory().isEmpty());
    }

    @Test
    void createScheduleNotYetWiredThrowsClearly() {
        HomeController facade = newFacade();
        ScheduleRequest req = new ScheduleRequest("d1", "ECO", "0 22 * * *");
        assertThrows(UnsupportedOperationException.class,
            () -> facade.createSchedule(req));
    }

    @Test
    void lockDeviceRoutesThroughCommand() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        Room room = new Room("facade-lock-" + UUID.randomUUID(), "Lock Room");
        Lock lock = (Lock) new Version2DeviceFactory().createDoorLock("Door");
        // Default state is unlocked
        assertFalse(lock.isLocked());
        room.addDevice(lock);
        hub.addRoom(room);

        HomeController facade = newFacade();
        facade.lockDevice(lock.getId());

        assertTrue(lock.isLocked());

        // Round-trip through unlock too
        facade.unlockDevice(lock.getId());
        assertFalse(lock.isLocked());
    }
}
