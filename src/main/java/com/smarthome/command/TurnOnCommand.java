package com.smarthome.command;

import com.smarthome.devices.Device;

import java.util.Objects;


// Command object that applies TurnOn to a target device and can undo it.
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
