package com.smarthome.devices.decorator;

import com.smarthome.devices.Device;
import com.smarthome.observer.Observer;

import java.util.Objects;

/**
 * DECORATOR PATTERN — BaseDecorator (per Refactoring Guru's canonical
 * structure).
 *
 * Holds a reference to the wrapped {@link Device} (the "Component" in
 * RG terminology) and delegates every Component method to it. Subclasses
 * override individual methods to inject behavior before/after delegation
 * (logging, energy tracking, access control, etc.).
 *
 * RG canonically requires BaseDecorator to implement the Component
 * <i>interface</i>. Our Component (Device) is an abstract class, not an
 * interface, so we extend it instead — functionally equivalent in Java
 * (substitutability is preserved), and avoids invasive refactoring of
 * existing Strategy/Command code that already operates on Device.
 *
 * Decorators stack: {@code new LoggingDecorator(new EnergyTrackedDecorator(realDevice))}
 * applies energy tracking inside, logging outside. Calls flow outside-in.
 */
public abstract class DeviceDecorator extends Device {

    /** The wrapped Component, called {@code wrappee} in RG's pseudocode. */
    protected final Device wrappee;

    protected DeviceDecorator(Device wrappee) {
        // Reuse the wrapped device's identity so DAOs and UI treat the
        // decorated wrapper as the same logical device.
        super(wrappee.getId(), wrappee.getName());
        this.wrappee = Objects.requireNonNull(wrappee, "wrappee must not be null");
    }

    // --- BaseDecorator delegates every Component method to wrappee ---

    @Override
    public void turnOn() {
        wrappee.turnOn();
    }

    @Override
    public void turnOff() {
        wrappee.turnOff();
    }

    @Override
    public boolean isPoweredOn() {
        return wrappee.isPoweredOn();
    }

    @Override
    public void attach(Observer observer) {
        wrappee.attach(observer);
    }

    @Override
    public void detach(Observer observer) {
        wrappee.detach(observer);
    }

    @Override
    public void notifyObservers(String event) {
        wrappee.notifyObservers(event);
    }
}
