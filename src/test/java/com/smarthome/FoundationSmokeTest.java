package com.smarthome;

import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Camera;
import com.smarthome.devices.Device;
import com.smarthome.devices.Light;
import com.smarthome.devices.Lock;
import com.smarthome.devices.Thermostat;
import com.smarthome.factory.CameraFactory;
import com.smarthome.factory.LightFactory;
import com.smarthome.factory.LockFactory;
import com.smarthome.factory.ThermostatFactory;
import com.smarthome.strategy.AwayMode;
import com.smarthome.strategy.EcoMode;
import com.smarthome.strategy.SleepMode;
import org.junit.jupiter.api.Test;

import java.util.Enumeration;

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
        Device light = new LightFactory().create("Kitchen Light");
        assertNotNull(light.getId());
        assertFalse(light.getId().isEmpty());
    }

    @Test
    void roomDevicesReturnsEnumeration() {
        Room room = new Room("r1", "Kitchen");
        room.addDevice(new LightFactory().create("L1"));
        room.addDevice(new LightFactory().create("L2"));

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
        Light light = new LightFactory().create("L1");
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
    void ecoModeDimsLightsAndSetsModerateTemperature() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        Room kitchen = new Room("eco-kitchen", "Eco Kitchen");
        Light light = new LightFactory().create("Eco Light");
        Thermostat thermostat = new ThermostatFactory().create("Eco Thermostat");
        light.turnOn();
        light.setBrightness(100);
        kitchen.addDevice(light);
        kitchen.addDevice(thermostat);
        hub.addRoom(kitchen);

        new EcoMode().apply(hub);

        assertEquals(50, light.getBrightness());
        assertEquals(24.0, thermostat.getTemperature(), 0.01);
    }

    @Test
    void sleepModeTurnsOffLightsAndLocksDoors() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        Room bedroom = new Room("sleep-bedroom", "Sleep Bedroom");
        Light light = new LightFactory().create("Bedside Light");
        Lock lock = new LockFactory().create("Bedroom Door");
        light.turnOn();
        lock.unlock();
        bedroom.addDevice(light);
        bedroom.addDevice(lock);
        hub.addRoom(bedroom);

        new SleepMode().apply(hub);

        assertFalse(light.isPoweredOn());
        assertTrue(lock.isLocked());
    }

    @Test
    void awayModeSecuresHouseAndArmsCameras() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        Room hall = new Room("away-hall", "Away Hallway");
        Light light = new LightFactory().create("Hall Light");
        Lock lock = new LockFactory().create("Front Door");
        Camera camera = new CameraFactory().create("Front Camera");
        light.turnOn();
        lock.unlock();
        hall.addDevice(light);
        hall.addDevice(lock);
        hall.addDevice(camera);
        hub.addRoom(hall);

        new AwayMode().apply(hub);

        assertFalse(light.isPoweredOn());
        assertTrue(lock.isLocked());
        assertTrue(camera.isPoweredOn());
    }

    @Test
    void brightnessChangeFiresObserverEvent() {
        Light light = new LightFactory().create("Observed Light");
        StringBuilder captured = new StringBuilder();

        light.attach((device, event) -> captured.append(event).append(","));
        light.setBrightness(50);
        light.setBrightness(50); // idempotent: should NOT fire again

        assertEquals("BRIGHTNESS_CHANGED,", captured.toString());
    }
}
