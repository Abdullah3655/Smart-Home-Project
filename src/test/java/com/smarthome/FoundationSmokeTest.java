package com.smarthome;

import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Camera;
import com.smarthome.devices.Device;
import com.smarthome.devices.Light;
import com.smarthome.devices.Lock;
import com.smarthome.devices.Thermostat;
import com.smarthome.devices.legacy.OldLight;
import com.smarthome.devices.newgen.NewLight;
import com.smarthome.factory.DeviceFactory;
import com.smarthome.factory.NewDeviceFactory;
import com.smarthome.factory.OldDeviceFactory;
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
        Device light = new NewDeviceFactory().createLight("Kitchen Light");
        assertNotNull(light.getId());
        assertFalse(light.getId().isEmpty());
    }

    @Test
    void roomDevicesReturnsEnumeration() {
        Room room = new Room("r1", "Kitchen");
        DeviceFactory factory = new NewDeviceFactory();
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
        Light light = (Light) new NewDeviceFactory().createLight("L1");
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
    void newFactoryCreatesNewFamilyTypes() {
        DeviceFactory factory = new NewDeviceFactory();
        assertTrue(factory.createLight("L") instanceof com.smarthome.devices.newgen.NewLight);
        assertTrue(factory.createThermostat("T") instanceof com.smarthome.devices.newgen.NewThermostat);
        assertTrue(factory.createDoorLock("D") instanceof com.smarthome.devices.newgen.NewLock);
        assertTrue(factory.createCamera("C") instanceof com.smarthome.devices.newgen.NewCamera);
    }

    @Test
    void oldFactoryCreatesOldFamilyTypes() {
        DeviceFactory factory = new OldDeviceFactory();
        assertTrue(factory.createLight("L") instanceof com.smarthome.devices.legacy.OldLight);
        assertTrue(factory.createThermostat("T") instanceof com.smarthome.devices.legacy.OldThermostat);
        assertTrue(factory.createDoorLock("D") instanceof com.smarthome.devices.legacy.OldLock);
        assertTrue(factory.createCamera("C") instanceof com.smarthome.devices.legacy.OldCamera);
    }

    @Test
    void oldAndNewLightBehaveDifferently() {
        NewLight newLight = (NewLight) new NewDeviceFactory().createLight("New");
        OldLight oldLight = (OldLight) new OldDeviceFactory().createLight("Old");

        newLight.setBrightness(61);
        oldLight.setBrightness(61);

        assertEquals(61, newLight.getBrightness());
        assertEquals(50, oldLight.getBrightness());
    }

    @Test
    void ecoModeDimsLightsAndSetsModerateTemp() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        Room room = new Room("k1-" + UUID.randomUUID(), "Kitchen");
        NewDeviceFactory factory = new NewDeviceFactory();
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
        NewDeviceFactory factory = new NewDeviceFactory();
        Lock doorLock = (Lock) factory.createDoorLock("Front Door");
        Camera camera = (Camera) factory.createCamera("Front Camera");
        room.addDevice(doorLock);
        room.addDevice(camera);
        hub.addRoom(room);

        new AwayMode().apply(hub);

        assertTrue(doorLock.isLocked());
        assertTrue(camera.isPoweredOn());
    }
}
