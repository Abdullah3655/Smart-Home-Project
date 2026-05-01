package com.smarthome.command;

import com.smarthome.devices.Thermostat;

import java.util.Objects;

/**
 * COMMAND PATTERN — ConcreteCommand.
 * Receiver: {@link Thermostat}.
 *
 * Captures the previous temperature so undo() restores it exactly,
 * regardless of how many other temperature changes happened in between
 * (history is replayed in reverse).
 */
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
