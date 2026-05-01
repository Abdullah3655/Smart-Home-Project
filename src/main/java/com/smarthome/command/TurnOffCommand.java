package com.smarthome.command;

import com.smarthome.devices.Device;

import java.util.Objects;

/**
 * COMMAND PATTERN — ConcreteCommand.
 * Receiver: {@link Device}.
 *
 * Mirror of {@link TurnOnCommand}: captures pre-state so undo() restores
 * the device only if it was on before.
 */
public class TurnOffCommand implements DeviceCommand {
    private final Device receiver;
    private boolean wasPoweredOnBefore;

    public TurnOffCommand(Device receiver) {
        this.receiver = Objects.requireNonNull(receiver, "receiver must not be null");
    }

    @Override
    public void execute() {
        wasPoweredOnBefore = receiver.isPoweredOn();
        receiver.turnOff();
    }

    @Override
    public void undo() {
        if (wasPoweredOnBefore) {
            receiver.turnOn();
        }
    }

    @Override
    public String describe() {
        return "Turn off " + receiver.getName();
    }
}
