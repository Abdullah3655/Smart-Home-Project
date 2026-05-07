package com.smarthome.strategy;

import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Device;
import com.smarthome.devices.Light;
import com.smarthome.devices.Lock;
import com.smarthome.devices.Thermostat;

import java.util.Enumeration;
import java.util.Objects;


// Automation strategy that applies sleep rules to hub devices.
public class SleepMode implements AutomationMode {
    @Override
    public String name() {
        return "SLEEP";
    }

    @Override
    public void apply(SmartHomeHub hub) {
        Objects.requireNonNull(hub, "hub must not be null");
        for (Room room : hub.getRooms()) {
            Enumeration<Device> devices = room.devices();
            while (devices.hasMoreElements()) {
                Device device = devices.nextElement();
                if (device instanceof Light light) {
                    light.turnOff();
                }
                if (device instanceof Lock lock) {
                    lock.lock();
                }
                if (device instanceof Thermostat thermostat) {
                    thermostat.setTemperature(20.0);
                }
            }
        }
    }
}
