package com.smarthome.strategy;

import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Camera;
import com.smarthome.devices.Device;
import com.smarthome.devices.Light;
import com.smarthome.devices.Lock;
import com.smarthome.devices.Thermostat;

import java.util.Enumeration;
import java.util.Objects;

/**
 * STRATEGY PATTERN — Away mode.
 * Secures and minimises activity for an empty house: lights off,
 * doors locked, cameras armed, thermostat set to vacation temperature.
 */
public class AwayMode implements AutomationMode {
    private static final double AWAY_TEMPERATURE_C = 15.0;

    @Override
    public String name() {
        return "AWAY";
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
                } else if (device instanceof Camera camera) {
                    camera.turnOn();
                } else if (device instanceof Thermostat thermostat) {
                    thermostat.setTemperature(AWAY_TEMPERATURE_C);
                }
            }
        }
    }
}
