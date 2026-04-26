package com.smarthome;

import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Device;
import com.smarthome.devices.Light;
import com.smarthome.factory.LightFactory;
import com.smarthome.strategy.EcoMode;
import com.smarthome.strategy.SleepMode;
import org.junit.jupiter.api.Test;

import java.util.Enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

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
}
