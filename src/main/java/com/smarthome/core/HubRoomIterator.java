package com.smarthome.core;

import java.util.List;


// Iterator over a snapshot list of rooms from SmartHomeHub.
public class HubRoomIterator implements RoomIterator {
    // Snapshot created by the hub when iterator is requested.
    private final List<Room> rooms;
    // Index of the next room to return.
    private int index;

    public HubRoomIterator(List<Room> rooms) {
        this.rooms = rooms;
        this.index = 0;
    }

    @Override
    public Room getNext() {
        // This API chooses null instead of throwing when exhausted.
        if (!hasMore()) {
            return null;
        }
        return rooms.get(index++);
    }

    @Override
    public boolean hasMore() {
        return index < rooms.size();
    }
}
