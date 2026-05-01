package com.smarthome.devices.newgen;

import com.smarthome.devices.Light;

public class NewLight extends Light {
    public NewLight(String id, String name) {
        super(id, name);
    }

    @Override
    public void setBrightness(int value) {
        super.setBrightness(value);
    }
}
