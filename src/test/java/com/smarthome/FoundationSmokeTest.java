package com.smarthome;

import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Camera;
import com.smarthome.devices.Device;
import com.smarthome.devices.Light;
import com.smarthome.devices.Lock;
import com.smarthome.devices.Thermostat;
import com.smarthome.devices.version1.Version1Light;
import com.smarthome.devices.version2.Version2Light;
import com.smarthome.factory.DeviceFactory;
import com.smarthome.factory.Version2DeviceFactory;
import com.smarthome.factory.Version1DeviceFactory;
import com.smarthome.strategy.AwayMode;
import com.smarthome.strategy.EcoMode;
import com.smarthome.strategy.SleepMode;
import org.junit.jupiter.api.Test;

import java.util.Enumeration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FoundationSmokeTest {

    @Test
    void singletonReturnsSameInstance() {
        assertSame(SmartHomeHub.getInstance(), SmartHomeHub.getInstance());
    }

    @Test
    void factoryCreatesDeviceWithUuid() {
        Device light = new Version2DeviceFactory().createLight("Kitchen Light");
        assertNotNull(light.getId());
        assertFalse(light.getId().isEmpty());
    }

    @Test
    void roomDevicesReturnsEnumeration() {
        Room room = new Room("r1", "Kitchen");
        DeviceFactory factory = new Version2DeviceFactory();
        room.addDevice(factory.createLight("L1"));
        room.addDevice(factory.createLight("L2"));

        Enumeration<Device> devices = room.devices();
        int count = 0;
        while (devices.hasMoreElements()) {
            devices.nextElement();
            count++;
        }

        assertEquals(2, count);
    }

    @Test
    void observerReceivesUpdateOnDeviceChange() {
        Light light = (Light) new Version2DeviceFactory().createLight("L1");
        StringBuilder captured = new StringBuilder();

        light.attach((device, event) -> captured.append(event));
        light.turnOn();

        assertEquals("TURNED_ON", captured.toString());
    }

    @Test
    void strategyModeSwitchChangesBehavior() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        hub.setAutomationMode(new EcoMode());
        assertEquals("ECO", hub.getAutomationMode().name());

        hub.setAutomationMode(new SleepMode());
        assertEquals("SLEEP", hub.getAutomationMode().name());
    }

    @Test
    void version2FactoryCreatesVersion2FamilyTypes() {
        DeviceFactory factory = new Version2DeviceFactory();
        assertTrue(factory.createLight("L") instanceof com.smarthome.devices.version2.Version2Light);
        assertTrue(factory.createThermostat("T") instanceof com.smarthome.devices.version2.Version2Thermostat);
        assertTrue(factory.createDoorLock("D") instanceof com.smarthome.devices.version2.Version2Lock);
        assertTrue(factory.createCamera("C") instanceof com.smarthome.devices.version2.Version2Camera);
    }

    @Test
    void version1FactoryCreatesVersion1FamilyTypes() {
        DeviceFactory factory = new Version1DeviceFactory();
        assertTrue(factory.createLight("L") instanceof com.smarthome.devices.version1.Version1Light);
        assertTrue(factory.createThermostat("T") instanceof com.smarthome.devices.version1.Version1Thermostat);
        assertTrue(factory.createDoorLock("D") instanceof com.smarthome.devices.version1.Version1Lock);
        assertTrue(factory.createCamera("C") instanceof com.smarthome.devices.version1.Version1Camera);
    }

    @Test
    void version1AndVersion2LightBehaveDifferently() {
        Version2Light version2Light = (Version2Light) new Version2DeviceFactory().createLight("Version2");
        Version1Light version1Light = (Version1Light) new Version1DeviceFactory().createLight("Version1");

        version2Light.setBrightness(61);
        version1Light.setBrightness(61);

        assertEquals(61, version2Light.getBrightness());
        assertEquals(50, version1Light.getBrightness());
    }

    @Test
    void ecoModeDimsLightsAndSetsModerateTemp() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        Room room = new Room("k1-" + UUID.randomUUID(), "Kitchen");
        Version2DeviceFactory factory = new Version2DeviceFactory();
        Light light = (Light) factory.createLight("Kitchen Light");
        Thermostat thermo = (Thermostat) factory.createThermostat("Kitchen Thermo");
        light.turnOn();
        light.setBrightness(100);
        room.addDevice(light);
        room.addDevice(thermo);
        hub.addRoom(room);

        new EcoMode().apply(hub);

        assertEquals(50, light.getBrightness());
        assertEquals(24.0, thermo.getTemperature(), 0.01);
    }

    @Test
    void awayModeLocksDoorsAndArmsCameras() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        Room room = new Room("s1-" + UUID.randomUUID(), "Security");
        Version2DeviceFactory factory = new Version2DeviceFactory();
        Lock doorLock = (Lock) factory.createDoorLock("Front Door");
        Camera camera = (Camera) factory.createCamera("Front Camera");
        room.addDevice(doorLock);
        room.addDevice(camera);
        hub.addRoom(room);

        new AwayMode().apply(hub);

        assertTrue(doorLock.isLocked());
        assertTrue(camera.isPoweredOn());
    }

    @Test
    void hubApplyAutomationModeDelegatesToConfiguredStrategy() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        Room room = new Room("ctx-" + UUID.randomUUID(), "Context Room");
        Version2DeviceFactory factory = new Version2DeviceFactory();
        Lock doorLock = (Lock) factory.createDoorLock("Door");
        room.addDevice(doorLock);
        hub.addRoom(room);

        hub.setAutomationMode(new AwayMode());
        hub.applyAutomationMode();   // Context delegates to Strategy (RG canonical shape)

        assertTrue(doorLock.isLocked());
    }
}

