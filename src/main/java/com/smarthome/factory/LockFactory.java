package com.smarthome.factory;

import com.smarthome.devices.Lock;

public class LockFactory extends DeviceFactory<Lock> {
    @Override
    public Lock create(String name) {
        return new Lock(newId(), name);
    }
}
