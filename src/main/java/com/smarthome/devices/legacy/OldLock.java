package com.smarthome.devices.legacy;

import com.smarthome.devices.Lock;

public class OldLock extends Lock {
    public OldLock(String id, String name) {
        super(id, name);
        lock();
    }
}
