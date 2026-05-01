package com.smarthome.factory;

import com.smarthome.devices.Device;
import com.smarthome.devices.newgen.NewCamera;
import com.smarthome.devices.newgen.NewLight;
import com.smarthome.devices.newgen.NewLock;
import com.smarthome.devices.newgen.NewThermostat;

public class NewDeviceFactory extends DeviceFactory {
    @Override
    public Device createLight(String name) {
        return new NewLight(newId(), name);
    }

    @Override
    public Device createThermostat(String name) {
        return new NewThermostat(newId(), name);
    }

    @Override
    public Device createDoorLock(String name) {
        return new NewLock(newId(), name);
    }

    @Override
    public Device createCamera(String name) {
        return new NewCamera(newId(), name);
    }
}
