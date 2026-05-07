package com.smarthome.command;

import com.smarthome.devices.Thermostat;

import java.util.Objects;


// Command object that applies SetTemperature to a target device and can undo it.
public class SetTemperatureCommand implements DeviceCommand {
    private final Thermostat receiver;
    private final double newTemperatureC;
    private double previousTemperatureC;

    public SetTemperatureCommand(Thermostat receiver, double newTemperatureC) {
        this.receiver = Objects.requireNonNull(receiver, "receiver must not be null");
        this.newTemperatureC = newTemperatureC;
    }

    @Override
    public void execute() {
        previousTemperatureC = receiver.getTemperature();
        receiver.setTemperature(newTemperatureC);
    }

    @Override
    public void undo() {
        receiver.setTemperature(previousTemperatureC);
    }

    @Override
    public String describe() {
        return "Set " + receiver.getName() + " to " + newTemperatureC + "°C";
    }
}
