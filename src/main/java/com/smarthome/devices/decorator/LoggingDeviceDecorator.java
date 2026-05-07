package com.smarthome.devices.decorator;

import com.smarthome.devices.Device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


// Decorator that records device actions for later display.
public class LoggingDeviceDecorator extends DeviceDecorator {

    private final List<String> log = new ArrayList<>();

    public LoggingDeviceDecorator(Device wrappee) {
        super(wrappee);
    }

    @Override
    public void turnOn() {
        log.add("turnOn(" + wrappee.getName() + ")");
        super.turnOn();
    }

    @Override
    public void turnOff() {
        log.add("turnOff(" + wrappee.getName() + ")");
        super.turnOff();
    }

    
    public List<String> getLog() {
        return Collections.unmodifiableList(log);
    }
}
