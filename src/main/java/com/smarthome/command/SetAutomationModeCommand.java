package com.smarthome.command;

import com.smarthome.core.SmartHomeHub;
import com.smarthome.strategy.AutomationMode;

import java.util.Objects;

/**
 * COMMAND PATTERN — ConcreteCommand.
 * Receiver: {@link SmartHomeHub} (the Strategy holder).
 *
 * Captures the previous mode so undo() can restore it AND re-apply it,
 * because device states may have been mutated by the new mode's apply().
 */
public class SetAutomationModeCommand implements DeviceCommand {
    private final SmartHomeHub receiver;
    private final AutomationMode newMode;
    private AutomationMode previousMode;

    public SetAutomationModeCommand(SmartHomeHub receiver, AutomationMode newMode) {
        this.receiver = Objects.requireNonNull(receiver, "receiver must not be null");
        this.newMode = Objects.requireNonNull(newMode, "newMode must not be null");
    }

    @Override
    public void execute() {
        previousMode = receiver.getAutomationMode();
        receiver.setAutomationMode(newMode);
        newMode.apply(receiver);
    }

    @Override
    public void undo() {
        receiver.setAutomationMode(previousMode);
        if (previousMode != null) {
            previousMode.apply(receiver);
        }
    }

    @Override
    public String describe() {
        return "Set automation mode to " + newMode.name();
    }
}
