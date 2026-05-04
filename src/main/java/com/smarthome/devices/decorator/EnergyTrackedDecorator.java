package com.smarthome.devices.decorator;

import com.smarthome.devices.Device;

/**
 * DECORATOR PATTERN — ConcreteDecorator.
 *
 * Tracks total time the wrapped Device has spent powered on, accumulating
 * a usage figure that the GUI's "energy report" tab can display.
 *
 * Demonstrates that decorators can carry their own state on top of the
 * wrapped Component without touching the Component's class hierarchy.
 */
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

    /** Total milliseconds powered on, including the current session if still on. */
    public long getTotalOnMillis() {
        long live = wrappee.isPoweredOn()
            ? System.currentTimeMillis() - onSinceMillis
            : 0L;
        return totalOnMillis + live;
    }
}
