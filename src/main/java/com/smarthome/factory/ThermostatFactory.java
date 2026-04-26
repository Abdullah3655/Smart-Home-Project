package com.smarthome.factory;

import com.smarthome.devices.Thermostat;

public class ThermostatFactory extends DeviceFactory<Thermostat> {
    @Override
    public Thermostat create(String name) {
        return new Thermostat(newId(), name);
    }
}
