package com.smarthome.strategy;

import com.smarthome.core.SmartHomeHub;

import java.util.Objects;

public class EcoMode implements AutomationMode {
    @Override
    public String name() {
        return "ECO";
    }

    @Override
    public void apply(SmartHomeHub hub) {
        Objects.requireNonNull(hub, "hub must not be null");

    }
}
