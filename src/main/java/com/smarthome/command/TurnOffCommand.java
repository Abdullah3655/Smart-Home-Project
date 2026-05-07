package com.smarthome.command;

import com.smarthome.devices.Device;

import java.util.Objects;


// Command object that applies TurnOff to a target device and can undo it.
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
