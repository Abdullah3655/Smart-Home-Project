package com.smarthome.command;

import com.smarthome.devices.Device;

import java.util.Objects;

/**
 * COMMAND PATTERN — ConcreteCommand.
 * Receiver: {@link Device}.
 *
 * Captures the device's pre-execute power state so undo() can restore it
 * idempotently — turning the device off only if it was off before.
 */
public class TurnOnCommand implements DeviceCommand {
    private final Device receiver;
    private boolean wasPoweredOnBefore;

    public TurnOnCommand(Device receiver) {
        this.receiver = Objects.requireNonNull(receiver, "receiver must not be null");
    }

    @Override
    public void execute() {
        wasPoweredOnBefore = receiver.isPoweredOn();
        receiver.turnOn();
    }

    @Override
    public void undo() {
        if (!wasPoweredOnBefore) {
            receiver.turnOff();
        }
    }

    @Override
    public String describe() {
        return "Turn on " + receiver.getName();
    }
}
