package com.smarthome.devices;

public class Light extends Device {
    private int brightness;

    public Light(String id, String name) {
        super(id, name);
        this.brightness = 100;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        if (brightness < 0 || brightness > 100) {
            throw new IllegalArgumentException("brightness must be between 0 and 100");
        }
        this.brightness = brightness;
    }
}
