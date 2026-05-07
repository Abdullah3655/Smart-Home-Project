package com.smarthome.command;


// Contract for executable actions that support undo.
public interface DeviceCommand {
    
    void execute();

    
    void undo();

    
    String describe();
}
