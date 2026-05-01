package com.smarthome.devices;

public class Light extends Device {
    private int brightness = 100;

    public Light(String id, String name) {
        super(id, name);
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int value) {
        this.brightness = Math.max(0, Math.min(100, value));
        notifyObservers("BRIGHTNESS_CHANGED");
    }
}
