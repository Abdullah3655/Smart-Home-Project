package com.smarthome.devices.newgen;

import com.smarthome.devices.Thermostat;

public class NewThermostat extends Thermostat {
    public NewThermostat(String id, String name) {
        super(id, name);
    }

    @Override
    public void setTemperature(double value) {
        double clamped = Math.max(5.0, Math.min(35.0, value));
        super.setTemperature(clamped);
    }
}
