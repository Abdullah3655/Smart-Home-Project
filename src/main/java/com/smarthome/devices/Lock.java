package com.smarthome.devices;

public class Lock extends Device {
    private boolean locked = false;

    public Lock(String id, String name) {
        super(id, name);
    }

    public boolean isLocked() {
        return locked;
    }

    public void lock() {
        if (!locked) {
            locked = true;
            notifyObservers(EVENT_LOCKED);
        }
    }

    public void unlock() {
        if (locked) {
            locked = false;
            notifyObservers(EVENT_UNLOCKED);
        }
    }
}
