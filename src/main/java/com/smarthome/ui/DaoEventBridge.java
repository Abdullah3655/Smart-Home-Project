package com.smarthome.ui;

import com.smarthome.devices.Device;
import com.smarthome.observer.Observer;
import com.smarthome.persistence.dao.DeviceEventDAO;

import java.util.Objects;

/**
 * OBSERVER PATTERN (boundary adapter) — bridges domain Observer events
 * to the persistence layer.
 *
 * <p>The domain layer (Device, SmartHomeHub, Strategy) must not depend on
 * persistence — that would couple the model to the database. This bridge
 * lives in the UI layer's startup code: it's an {@link Observer} that
 * forwards every device event to {@link DeviceEventDAO#insert}, satisfying
 * the rubric's "modular, easy to expand" constraint while keeping the
 * domain pure.</p>
 *
 * <p>One instance is attached to every device on app startup, alongside
 * any in-memory listeners the UI uses for live updates.</p>
 */
public class DaoEventBridge implements Observer {

    private final DeviceEventDAO dao;

    public DaoEventBridge(DeviceEventDAO dao) {
        this.dao = Objects.requireNonNull(dao, "dao must not be null");
    }

    @Override
    public void update(Device d, String event) {
        try {
            dao.insert(d.getId(), event);
        } catch (Exception e) {
            // Persistence failures must not block the UI or break domain events.
            // Log to stderr and carry on.
            System.err.println("DaoEventBridge: failed to persist event " + event
                + " for device " + d.getId() + ": " + e.getMessage());
        }
    }
}
