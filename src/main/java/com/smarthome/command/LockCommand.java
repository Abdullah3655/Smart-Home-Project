package com.smarthome.command;

import com.smarthome.devices.Lock;

import java.util.Objects;

/**
 * COMMAND PATTERN — ConcreteCommand.
 * Receiver: {@link Lock}.
 */
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
