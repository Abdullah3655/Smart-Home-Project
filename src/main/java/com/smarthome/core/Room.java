package com.smarthome.core;

import com.smarthome.devices.Device;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;


// ITERATOR pattern: Room aggregate exposes its devices as Enumeration<Device>.
public class Room {
    private final String roomId;
    private final String name;
    // LinkedHashMap preserves insertion order for predictable UI rendering.
    private final Map<String, Device> devicesById = new LinkedHashMap<>();

    public Room(String roomId, String name) {
        this.roomId = Objects.requireNonNull(roomId, "roomId must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    public String getRoomId() {
        return roomId;
    }

    public String getName() {
        return name;
    }

    public void addDevice(Device device) {
        Objects.requireNonNull(device, "device must not be null");
        // Replaces old device if the same id already exists.
        devicesById.put(device.getId(), device);
    }

    public void removeDevice(String deviceId) {
        devicesById.remove(deviceId);
    }

    public Device getDevice(String deviceId) {
        return devicesById.get(deviceId);
    }

    // Iterator-pattern style traversal API requested by the assignment.
    // ITERATOR pattern method (rubric requirement: returns Enumeration).
    public Enumeration<Device> devices() {
        Collection<Device> values = devicesById.values();
        return Collections.enumeration(values);
    }
}
