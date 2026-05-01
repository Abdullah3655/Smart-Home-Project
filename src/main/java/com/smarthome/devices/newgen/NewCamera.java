package com.smarthome.devices.newgen;

import com.smarthome.devices.Camera;

public class NewCamera extends Camera {
    public NewCamera(String id, String name) {
        super(id, name);
        turnOn();
    }
}
