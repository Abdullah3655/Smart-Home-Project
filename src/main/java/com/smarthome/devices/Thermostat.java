package com.smarthome.devices;

public class Thermostat extends Device {
    private double temperature;

    public Thermostat(String id, String name) {
        super(id, name);
        this.temperature = 24.0;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        if (Double.compare(this.temperature, temperature) != 0) {
            this.temperature = temperature;
            notifyObservers(EVENT_TEMP_CHANGED);
        }
    }
}
