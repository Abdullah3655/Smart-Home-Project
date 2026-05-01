package com.smarthome.strategy;

import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Device;
import com.smarthome.devices.Light;
import com.smarthome.devices.Thermostat;

import java.util.Enumeration;
import java.util.Objects;

/**
 * STRATEGY PATTERN — Eco mode.
 * Minimises energy use while keeping the home livable: moderate
 * thermostat temperature and dimmed (but not off) lights.
 */
public class EcoMode implements AutomationMode {
    private static final double ECO_TEMPERATURE_C = 24.0;
    private static final int ECO_BRIGHTNESS = 50;

    @Override
    public String name() {
        return "ECO";
    }

    @Override
    public void apply(SmartHomeHub hub) {
        Objects.requireNonNull(hub, "hub must not be null");
        for (Room room : hub.getRooms()) {
            Enumeration<Device> devices = room.devices();
            while (devices.hasMoreElements()) {
                Device device = devices.nextElement();
                if (device instanceof Thermostat thermostat) {
                    thermostat.setTemperature(ECO_TEMPERATURE_C);
                } else if (device instanceof Light light && light.isPoweredOn()) {
                    light.setBrightness(ECO_BRIGHTNESS);
                }
            }
        }
    }
}
