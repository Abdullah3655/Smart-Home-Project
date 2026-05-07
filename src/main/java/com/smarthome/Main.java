package com.smarthome;

import com.smarthome.command.CommandInvoker;
import com.smarthome.command.SetAutomationModeCommand;
import com.smarthome.command.TurnOnCommand;
import com.smarthome.core.Room;
import com.smarthome.core.RoomIterator;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Device;
import com.smarthome.devices.Light;
import com.smarthome.devices.Lock;
import com.smarthome.devices.decorator.LoggingDeviceDecorator;
import com.smarthome.factory.DeviceFactory;
import com.smarthome.factory.Version2DeviceFactory;
import com.smarthome.strategy.AwayMode;
import com.smarthome.strategy.EcoMode;

import java.util.List;


// Application entry point.
public class Main {

    public static void main(String[] args) {
        section("1. SINGLETON — SmartHomeHub.getInstance()");
        SmartHomeHub hub = SmartHomeHub.getInstance();
        System.out.println("Checking if hub is the same as hub.getInstance(): " + (hub == SmartHomeHub.getInstance()));

        
        section("2. ABSTRACT FACTORY + FACTORY METHODS");
        DeviceFactory factory = new Version2DeviceFactory();
        Light kitchenLight = (Light) factory.createLight("Kitchen Light");
        Lock frontLock = (Lock) factory.createDoorLock("Front Door");
        Device thermostat = factory.createThermostat("Living Thermo");
        System.out.println("Light  -> " + kitchenLight.getClass().getSimpleName() + " id=" + shortId(kitchenLight));
        System.out.println("Lock   -> " + frontLock.getClass().getSimpleName()    + " id=" + shortId(frontLock));
        System.out.println("Thermo -> " + thermostat.getClass().getSimpleName()    + " id=" + shortId(thermostat));

        section("3. OBSERVER (push) — attach a listener and toggle the light");
        kitchenLight.attach((d, event) ->
            System.out.println("    [observer] " + d.getName() + " fired " + event));
        kitchenLight.turnOn();
        kitchenLight.turnOff();

        section("4. ITERATOR — Room.devices() returns List<Device>");
        Room kitchen = new Room("kitchen", "Kitchen");
        kitchen.addDevice(kitchenLight);
        kitchen.addDevice(thermostat);
        Room frontDoor = new Room("front-door", "Front Door");
        frontDoor.addDevice(frontLock);
        hub.addRoom(kitchen);
        hub.addRoom(frontDoor);

        List<Device> devices = kitchen.devices();
        System.out.println("Devices in Kitchen:");
        for (Device d : devices) {
            System.out.println("    - " + d.getName());
        }

        section("5. ITERATOR (custom RoomIterator) — walk all rooms");
        RoomIterator rooms = hub.createIterator();
        while (rooms.hasMore()) {
            System.out.println("    room: " + rooms.getNext().getName());
        }

        section("6. STRATEGY — apply EcoMode then AwayMode");
        kitchenLight.turnOn();
        kitchenLight.setBrightness(100);
        new EcoMode().apply(hub);
        System.out.println("After Eco: light brightness = " + kitchenLight.getBrightness()
            + ", lock locked = " + frontLock.isLocked());

        new AwayMode().apply(hub);
        System.out.println("After Away: light on = " + kitchenLight.isPoweredOn()
            + ", lock locked = " + frontLock.isLocked());

        section("7. COMMAND + UNDO");
        CommandInvoker invoker = new CommandInvoker();
        invoker.execute(new TurnOnCommand(kitchenLight));
        System.out.println("After turn-on command: " + kitchenLight.isPoweredOn());
        invoker.undo();
        System.out.println("After undo:             " + kitchenLight.isPoweredOn());

        section("8. COMMAND wrapping STRATEGY (undoable mode change)");
        hub.setAutomationMode(new AwayMode());            // initial mode
        invoker.execute(new SetAutomationModeCommand(hub, new EcoMode()));
        System.out.println("Active mode: " + hub.getAutomationMode().name());
        invoker.undo();
        System.out.println("After undo:  " + hub.getAutomationMode().name());

        section("9. DECORATOR — wrap a device, see captured calls");
        LoggingDeviceDecorator wrapped = new LoggingDeviceDecorator(kitchenLight);
        wrapped.turnOn();
        wrapped.turnOff();
        System.out.println("Captured by decorator:");
        wrapped.getLog().forEach(line -> System.out.println("    " + line));

        section("DONE — all 9 patterns exercised. Run './mvnw javafx:run' for the GUI.");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("== " + title + " ==");
    }

    private static String shortId(Device d) {
        String id = d.getId();
        return id.length() > 8 ? id.substring(0, 8) + "…" : id;
    }
}
