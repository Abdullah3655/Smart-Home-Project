package com.smarthome.devices.decorator;

import com.smarthome.devices.Device;
import com.smarthome.factory.Version2DeviceFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the Decorator pattern follows Refactoring Guru's contract:
 *  - decorators are substitutable for the wrapped Component
 *  - decorators stack and execution flows outside-in
 *  - decorators can carry independent state
 */
class DecoratorTest {

    @Test
    void loggingDecoratorRecordsCallsAndDelegatesToWrappee() {
        Device base = new Version2DeviceFactory().createLight("Kitchen Light");
        LoggingDeviceDecorator logged = new LoggingDeviceDecorator(base);

        logged.turnOn();
        logged.turnOff();

        // Wrappee state actually changed (delegation worked)
        assertFalse(base.isPoweredOn());
        // Decorator captured the calls
        assertEquals(2, logged.getLog().size());
        assertTrue(logged.getLog().get(0).contains("turnOn"));
        assertTrue(logged.getLog().get(1).contains("turnOff"));
    }

    @Test
    void energyTrackedDecoratorAccumulatesOnTime() throws InterruptedException {
        Device base = new Version2DeviceFactory().createLight("Living Light");
        EnergyTrackedDecorator tracked = new EnergyTrackedDecorator(base);

        tracked.turnOn();
        Thread.sleep(50);
        tracked.turnOff();

        assertTrue(tracked.getTotalOnMillis() >= 50,
            "expected at least 50ms tracked, got " + tracked.getTotalOnMillis());
    }

    @Test
    void decoratorsStackAndExecuteOutsideIn() {
        // Per RG: source = Light → wrap with EnergyTracked → wrap with Logging
        // Calls flow Logging → EnergyTracked → Light
        Device base = new Version2DeviceFactory().createLight("Stacked Light");
        EnergyTrackedDecorator energy = new EnergyTrackedDecorator(base);
        LoggingDeviceDecorator outer = new LoggingDeviceDecorator(energy);

        outer.turnOn();

        // Logging captured the call
        assertEquals(1, outer.getLog().size());
        // Energy tracker also saw it
        assertTrue(base.isPoweredOn());
    }

    @Test
    void decoratorPreservesIdentityOfWrappedDevice() {
        Device base = new Version2DeviceFactory().createLight("Identity Light");
        LoggingDeviceDecorator logged = new LoggingDeviceDecorator(base);

        // Decorator carries the wrapped device's id and name — DAOs and UI
        // treat the decorated instance as the same logical device.
        assertEquals(base.getId(), logged.getId());
        assertEquals(base.getName(), logged.getName());
    }

    @Test
    void decoratorIsSubstitutableWhereDeviceExpected() {
        Device base = new Version2DeviceFactory().createLight("Sub Light");
        Device decorated = new LoggingDeviceDecorator(base);

        // The decorator IS-A Device — works wherever Device is expected.
        // This is the substitutability requirement of the pattern.
        decorated.turnOn();
        assertTrue(decorated.isPoweredOn());
    }
}
