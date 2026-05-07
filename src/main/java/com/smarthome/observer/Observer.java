package com.smarthome.observer;

import com.smarthome.devices.Device;


// Observer contract for receiving device state-change events.
public interface Observer {
    
    void update(Device d, String event);
}
