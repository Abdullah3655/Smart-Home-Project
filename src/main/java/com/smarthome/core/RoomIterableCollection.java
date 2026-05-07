package com.smarthome.core;


// Aggregate contract that returns a RoomIterator for traversal.
public interface RoomIterableCollection {
    RoomIterator createIterator();
}
