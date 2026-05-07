package com.smarthome.core;


// Iterator contract used to traverse rooms without exposing collection internals.
public interface RoomIterator {
    // Returns the next room and advances the cursor.
    // Returns null when traversal is finished.
    Room getNext();

    // True means getNext() can still return a room.
    boolean hasMore();
}
