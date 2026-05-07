package com.smarthome.command;

import com.smarthome.devices.Lock;

import java.util.Objects;


// Command object that applies Lock to a target device and can undo it.
public class LockCommand implements DeviceCommand {
    private final Lock receiver;
    private boolean wasLockedBefore;

    public LockCommand(Lock receiver) {
        this.receiver = Objects.requireNonNull(receiver, "receiver must not be null");
    }

    @Override
    public void execute() {
        wasLockedBefore = receiver.isLocked();
        receiver.lock();
    }

    @Override
    public void undo() {
        if (!wasLockedBefore) {
            receiver.unlock();
        }
    }

    @Override
    public String describe() {
        return "Lock " + receiver.getName();
    }
}
