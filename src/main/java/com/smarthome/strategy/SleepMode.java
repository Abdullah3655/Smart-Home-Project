package com.smarthome.strategy;

import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Device;
import com.smarthome.devices.Light;
import com.smarthome.devices.Lock;
import com.smarthome.devices.Thermostat;

import java.util.Enumeration;
import java.util.Objects;

/**
 * STRATEGY PATTERN — Sleep mode.
 * Prepares the home for night: lights off, doors locked, thermostat
 * lowered to a comfortable sleeping temperature.
 */
public class SleepMode implements AutomationMode {
    private static final double SLEEP_TEMPERATURE_C = 20.0;

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
                } else if (device instanceof Lock lock) {
                    lock.lock();
                } else if (device instanceof Thermostat thermostat) {
                    thermostat.setTemperature(SLEEP_TEMPERATURE_C);
                }
            }
        }
    }
}
