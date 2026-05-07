package com.smarthome.factory;

import com.smarthome.devices.Device;
import java.util.UUID;

// Abstract factory for creating one concrete device family.
public abstract class DeviceFactory {
    
    public abstract Device createLight(String name);

    
    public abstract Device createThermostat(String name);

    
    public abstract Device createDoorLock(String name);

    
    public abstract Device createCamera(String name);

    // Shared id generator used by all concrete factories.
    protected String newId() {
        return UUID.randomUUID().toString();
    }
}
