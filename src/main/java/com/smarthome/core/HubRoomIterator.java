package com.smarthome.core;

import java.util.List;


// Iterator over a snapshot list of rooms from SmartHomeHub.
public class HubRoomIterator implements RoomIterator {
    private final List<Room> rooms;
    private int index;

    public HubRoomIterator(List<Room> rooms) {
        this.rooms = rooms;
        this.index = 0;
    }

    @Override
    public Room getNext() {
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
