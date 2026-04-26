package com.smarthome.core;

import com.smarthome.devices.Device;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Room {
    private final String roomId;
    private final String name;
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
        devicesById.put(device.getId(), device);
    }

    public void removeDevice(String deviceId) {
        devicesById.remove(deviceId);
    }

    public Device getDevice(String deviceId) {
        return devicesById.get(deviceId);
    }

    public Enumeration<Device> devices() {
        Collection<Device> values = devicesById.values();
        return Collections.enumeration(values);
    }
}
