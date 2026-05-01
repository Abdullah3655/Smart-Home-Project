package com.smarthome.devices.legacy;

import com.smarthome.devices.Thermostat;

public class OldThermostat extends Thermostat {
    public OldThermostat(String id, String name) {
        super(id, name);
        setTemperature(20.0);
    }

    @Override
    public void setTemperature(double value) {
        double clamped = Math.max(10.0, Math.min(30.0, value));
        double rounded = Math.rint(clamped);
        super.setTemperature(rounded);
    }
}
