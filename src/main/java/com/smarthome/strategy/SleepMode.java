package com.smarthome.strategy;

import com.smarthome.core.SmartHomeHub;

import java.util.Objects;

public class SleepMode implements AutomationMode {
    @Override
    public String name() {
        return "SLEEP";
    }

    @Override
    public void apply(SmartHomeHub hub) {
        Objects.requireNonNull(hub, "hub must not be null");
        // TODO(Task 5 integration): turn off non-bedroom lights and lock doors.
    }
}
