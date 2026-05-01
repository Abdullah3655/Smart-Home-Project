package com.smarthome.devices.version2;

import com.smarthome.devices.Light;

public class Version2Light extends Light {
    public Version2Light(String id, String name) {
        super(id, name);
    }

    @Override
    public void setBrightness(int value) {
        super.setBrightness(value);
    }
}

