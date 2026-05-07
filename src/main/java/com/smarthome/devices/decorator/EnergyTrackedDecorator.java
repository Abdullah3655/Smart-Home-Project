package com.smarthome.devices.decorator;

import com.smarthome.devices.Device;


// Decorator that tracks how long a device stays powered on.
public class EnergyTrackedDecorator extends DeviceDecorator {

    private long onSinceMillis = 0L;
    private long totalOnMillis = 0L;

    public EnergyTrackedDecorator(Device wrappee) {
        super(wrappee);
    }

    @Override
    public void turnOn() {
        boolean wasOff = !wrappee.isPoweredOn();
        super.turnOn();
        if (wasOff && wrappee.isPoweredOn()) {
            onSinceMillis = System.currentTimeMillis();
        }
    }

    @Override
    public void turnOff() {
        if (wrappee.isPoweredOn()) {
            totalOnMillis += System.currentTimeMillis() - onSinceMillis;
        }
        super.turnOff();
    }

    
    public long getTotalOnMillis() {
        long live = wrappee.isPoweredOn()
            ? System.currentTimeMillis() - onSinceMillis
            : 0L;
        return totalOnMillis + live;
    }
}
