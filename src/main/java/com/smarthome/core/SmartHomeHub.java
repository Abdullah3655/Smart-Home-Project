package com.smarthome.core;

import com.smarthome.strategy.AutomationMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SmartHomeHub implements RoomIterableCollection {
    private static final SmartHomeHub INSTANCE = new SmartHomeHub();

    private final Map<String, Room> roomsById = new ConcurrentHashMap<>();
    private AutomationMode automationMode;

    private SmartHomeHub() {
    }

    public static SmartHomeHub getInstance() {
        return INSTANCE;
    }

    public void addRoom(Room room) {
        Objects.requireNonNull(room, "room must not be null");
        roomsById.put(room.getRoomId(), room);
    }

    public Room getRoom(String roomId) {
        return roomsById.get(roomId);
    }

    public Collection<Room> getRooms() {
        return Collections.unmodifiableCollection(roomsById.values());
    }

    public void setAutomationMode(AutomationMode mode) {
        this.automationMode = mode;
    }

    public AutomationMode getAutomationMode() {
        return automationMode;
    }

    @Override
    public RoomIterator createIterator() {
        return new HubRoomIterator(new ArrayList<>(roomsById.values()));
    }
}
