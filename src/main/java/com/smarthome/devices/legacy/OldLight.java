package com.smarthome.devices.legacy;

import com.smarthome.devices.Light;

public class OldLight extends Light {
    public OldLight(String id, String name) {
        super(id, name);
    }

    @Override
    public void setBrightness(int value) {
        int clamped = Math.max(0, Math.min(100, value));
        int stepped = (clamped / 25) * 25;
        super.setBrightness(stepped);
    }
}
