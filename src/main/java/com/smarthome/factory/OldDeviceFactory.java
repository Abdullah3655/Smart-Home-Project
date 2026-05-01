package com.smarthome.factory;

import com.smarthome.devices.Device;
import com.smarthome.devices.legacy.OldCamera;
import com.smarthome.devices.legacy.OldLight;
import com.smarthome.devices.legacy.OldLock;
import com.smarthome.devices.legacy.OldThermostat;

public class OldDeviceFactory extends DeviceFactory {
    @Override
    public Device createLight(String name) {
        return new OldLight(newId(), name);
    }

    @Override
    public Device createThermostat(String name) {
        return new OldThermostat(newId(), name);
    }

    @Override
    public Device createDoorLock(String name) {
        return new OldLock(newId(), name);
    }

    @Override
    public Device createCamera(String name) {
        return new OldCamera(newId(), name);
    }
}
