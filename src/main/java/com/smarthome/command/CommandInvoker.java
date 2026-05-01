package com.smarthome.command;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * COMMAND PATTERN — Invoker.
 *
 * Per Refactoring Guru's canonical structure: holds Command references and
 * triggers their execute() method. The Invoker is decoupled from Receivers
 * — it does NOT import Device, Lock, Thermostat, or any other domain class.
 * Only knows DeviceCommand.
 *
 * Maintains an undo stack (Deque used as LIFO) so the most recent command
 * is undone first. This is RG's "CommandHistory" responsibility, kept here
 * for project simplicity rather than a separate class.
 */
public class CommandInvoker {
    private final Deque<DeviceCommand> history = new ArrayDeque<>();

    /**
     * Run the command and push it onto the undo stack on success.
     * If execute() throws, the command is NOT recorded (failed commands
     * shouldn't be undone — there's nothing to undo).
     */
    public void execute(DeviceCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        command.execute();
        history.push(command);
    }

    /** True if there is at least one previously-executed command to undo. */
    public boolean canUndo() {
        return !history.isEmpty();
    }

    /**
     * Pop the most recent command and undo it.
     * Returns the undone command, or null if history is empty.
     */
    public DeviceCommand undo() {
        if (history.isEmpty()) {
            return null;
        }
        DeviceCommand last = history.pop();
        last.undo();
        return last;
    }

    /**
     * Snapshot of the undo stack in execution order (most recent first).
     * Returned list is unmodifiable; caller cannot mutate invoker state.
     */
    public List<DeviceCommand> getHistory() {
        return Collections.unmodifiableList(List.copyOf(history));
    }

    /** Clears the undo stack. Useful for tests or "commit" semantics. */
    public void clearHistory() {
        history.clear();
    }
}
