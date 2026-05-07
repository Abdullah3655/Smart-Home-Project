package com.smarthome.command;

import com.smarthome.devices.Lock;

import java.util.Objects;


// Command object that applies Unlock to a target device and can undo it.
public class UnlockCommand implements DeviceCommand {
    private final Lock receiver;
    private boolean wasLockedBefore;

    public UnlockCommand(Lock receiver) {
        this.receiver = Objects.requireNonNull(receiver, "receiver must not be null");
    }

    @Override
    public void execute() {
        wasLockedBefore = receiver.isLocked();
        receiver.unlock();
    }

    @Override
    public void undo() {
        if (wasLockedBefore) {
            receiver.lock();
        }
    }

    @Override
    public String describe() {
        return "Unlock " + receiver.getName();
    }
}
