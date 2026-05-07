package com.smarthome.strategy;

import com.smarthome.core.SmartHomeHub;


// Strategy contract for applying one automation mode to the hub.
public interface AutomationMode {
    String name();

    void apply(SmartHomeHub hub);
}
