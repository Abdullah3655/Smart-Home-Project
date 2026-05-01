package com.smarthome.command;

import com.smarthome.devices.Lock;

import java.util.Objects;

/**
 * COMMAND PATTERN — ConcreteCommand.
 * Receiver: {@link Lock}.
 */
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
