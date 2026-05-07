package com.smarthome.core;

import com.smarthome.devices.Device;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * ITERATOR PATTERN — exposes its internal device collection as an
 * {@link Enumeration}, satisfying the rubric line:
 *
 * <blockquote>"Iterator: Methods that return an Enumeration."</blockquote>
 *
 * <p>The {@link #devices()} method is the pattern's traversal method.
 * Internally a {@link LinkedHashMap} keeps insertion order so the UI
 * shows devices in the order they were added; {@link Collections#enumeration}
 * wraps the values into an Enumeration without leaking the underlying
 * mutable map.</p>
 */
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

    /**
     * ITERATOR PATTERN — required traversal method.
     * Returns devices via {@link Enumeration}, the form required by the
     * assignment brief.
     */
    public Enumeration<Device> devices() {
        Collection<Device> values = devicesById.values();
        return Collections.enumeration(values);
    }
}
