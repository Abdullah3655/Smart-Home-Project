package com.smarthome.factory;

import com.smarthome.devices.Device;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DeviceFactory<T extends Device> {
    private static final Set<String> GENERATED_IDS = ConcurrentHashMap.newKeySet();

    public abstract T create(String name);

    protected String newId() {
        String id;
        do {
            id = UUID.randomUUID().toString();
        } while (!GENERATED_IDS.add(id));
        return id;
    }
}
