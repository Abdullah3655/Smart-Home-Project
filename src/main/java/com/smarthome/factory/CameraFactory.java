package com.smarthome.factory;

import com.smarthome.devices.Camera;

public class CameraFactory extends DeviceFactory<Camera> {
    @Override
    public Camera create(String name) {
        return new Camera(newId(), name);
    }
}
