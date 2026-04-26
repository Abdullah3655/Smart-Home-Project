package com.smarthome.observer;

import com.smarthome.devices.Device;

public interface Observer {
    void update(Device d, String event);
}
