package com.smarthome.factory;

import com.smarthome.devices.Light;

public class LightFactory extends DeviceFactory<Light> {
    @Override
    public Light create(String name) {
        return new Light(newId(), name);
    }
}
