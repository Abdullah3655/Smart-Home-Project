package com.smarthome.strategy;

import com.smarthome.core.SmartHomeHub;

public interface AutomationMode {
    String name();

    void apply(SmartHomeHub hub);
}
