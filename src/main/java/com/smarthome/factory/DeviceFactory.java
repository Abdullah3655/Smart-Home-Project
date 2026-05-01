package com.smarthome.factory;

import com.smarthome.devices.Device;
import java.util.UUID;

public abstract class DeviceFactory {
    public abstract Device createLight(String name);

    public abstract Device createThermostat(String name);

    public abstract Device createDoorLock(String name);

    public abstract Device createCamera(String name);

    protected String newId() {
        return UUID.randomUUID().toString();
    }
}
