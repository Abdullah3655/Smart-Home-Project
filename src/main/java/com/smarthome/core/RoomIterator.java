package com.smarthome.core;


// Iterator contract used to traverse rooms without exposing collection internals.
public interface RoomIterator {
    Room getNext();

    boolean hasMore();
}
