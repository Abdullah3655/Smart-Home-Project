package com.smarthome.command;

/**
 * COMMAND PATTERN — Command interface.
 *
 * Per Refactoring Guru's canonical structure: declares the single execution
 * method that every ConcreteCommand must implement. ConcreteCommands hold
 * references to Receivers (Device subclasses) and delegate the actual work
 * to them — they do NOT perform business logic themselves.
 *
 * The undo() extension is the standard "memento-lite" addition from the
 * Gang of Four book, allowing each command to capture pre-state during
 * execute() and restore it on undo().
 */
public interface DeviceCommand {
    /** Run the command. ConcreteCommands typically capture pre-state here for undo. */
    void execute();

    /** Reverse the command's effect, restoring the pre-execute state. */
    void undo();

    /** Human-readable description for command history UIs. */
    String describe();
}
