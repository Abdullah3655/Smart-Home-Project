package com.smarthome.core;


// Aggregate contract that returns a RoomIterator for traversal.
public interface RoomIterableCollection {
    // Iterator-pattern factory method used by clients that should not see map/list internals.
    RoomIterator createIterator();
}
