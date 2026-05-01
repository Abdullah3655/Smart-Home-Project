package com.smarthome.command;

import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Lock;
import com.smarthome.devices.Thermostat;
import com.smarthome.devices.version2.Version2Light;
import com.smarthome.devices.version2.Version2Lock;
import com.smarthome.devices.version2.Version2Thermostat;
import com.smarthome.strategy.AwayMode;
import com.smarthome.strategy.EcoMode;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandTest {

    @Test
    void turnOnExecuteAndUndoRestoresOriginalState() {
        Version2Light light = new Version2Light(UUID.randomUUID().toString(), "Living Light");
        assertFalse(light.isPoweredOn());

        CommandInvoker invoker = new CommandInvoker();
        invoker.execute(new TurnOnCommand(light));
        assertTrue(light.isPoweredOn());

        invoker.undo();
        assertFalse(light.isPoweredOn());
    }

    @Test
    void undoOnAlreadyOnDeviceLeavesItOn() {
        Version2Light light = new Version2Light(UUID.randomUUID().toString(), "Living Light");
        light.turnOn();
        assertTrue(light.isPoweredOn());

        CommandInvoker invoker = new CommandInvoker();
        invoker.execute(new TurnOnCommand(light));   // already on, no-op essentially
        invoker.undo();                              // should NOT turn it off

        assertTrue(light.isPoweredOn(), "undo must not change state when execute was a no-op");
    }

    @Test
    void undoOnEmptyHistoryReturnsNull() {
        CommandInvoker invoker = new CommandInvoker();
        assertNull(invoker.undo());
        assertFalse(invoker.canUndo());
    }

    @Test
    void historyReflectsExecutionOrder() {
        Version2Light light = new Version2Light(UUID.randomUUID().toString(), "L");
        CommandInvoker invoker = new CommandInvoker();

        invoker.execute(new TurnOnCommand(light));
        invoker.execute(new TurnOffCommand(light));

        assertEquals(2, invoker.getHistory().size());
        assertTrue(invoker.canUndo());
    }

    @Test
    void setTemperatureUndoRestoresExactPreviousValue() {
        Thermostat thermostat = new Version2Thermostat(UUID.randomUUID().toString(), "T");
        thermostat.setTemperature(22.0);

        CommandInvoker invoker = new CommandInvoker();
        invoker.execute(new SetTemperatureCommand(thermostat, 28.0));
        assertEquals(28.0, thermostat.getTemperature(), 0.01);

        invoker.undo();
        assertEquals(22.0, thermostat.getTemperature(), 0.01);
    }

    @Test
    void lockAndUnlockCommandsAreReversible() {
        Lock lock = new Version2Lock(UUID.randomUUID().toString(), "Front Door");
        // Version2Lock starts locked=true (inherits Lock's default). Unlock first to set up.
        lock.unlock();
        assertFalse(lock.isLocked());

        CommandInvoker invoker = new CommandInvoker();
        invoker.execute(new LockCommand(lock));
        assertTrue(lock.isLocked());

        invoker.undo();
        assertFalse(lock.isLocked(), "undo of LockCommand must restore unlocked state");
    }

    @Test
    void setAutomationModeUndoReappliesPreviousMode() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        AwayMode away = new AwayMode();
        EcoMode eco = new EcoMode();

        hub.setAutomationMode(away);
        assertEquals("AWAY", hub.getAutomationMode().name());

        CommandInvoker invoker = new CommandInvoker();
        invoker.execute(new SetAutomationModeCommand(hub, eco));
        assertEquals("ECO", hub.getAutomationMode().name());

        invoker.undo();
        assertEquals("AWAY", hub.getAutomationMode().name());
    }

    @Test
    void describeProducesHumanReadableText() {
        Version2Light light = new Version2Light(UUID.randomUUID().toString(), "Kitchen Light");
        DeviceCommand cmd = new TurnOnCommand(light);

        String description = cmd.describe();
        assertNotNull(description);
        assertTrue(description.contains("Kitchen Light"));
    }

    @Test
    void multipleUndosReplayInReverseOrder() {
        Version2Light light = new Version2Light(UUID.randomUUID().toString(), "L");
        CommandInvoker invoker = new CommandInvoker();

        invoker.execute(new TurnOnCommand(light));    // light: off → on
        invoker.execute(new TurnOffCommand(light));   // light: on → off
        invoker.execute(new TurnOnCommand(light));    // light: off → on
        assertTrue(light.isPoweredOn());

        invoker.undo();   // undo last TurnOn → off
        assertFalse(light.isPoweredOn());
        invoker.undo();   // undo TurnOff → on
        assertTrue(light.isPoweredOn());
        invoker.undo();   // undo first TurnOn → off
        assertFalse(light.isPoweredOn());

        assertFalse(invoker.canUndo());
    }
}

