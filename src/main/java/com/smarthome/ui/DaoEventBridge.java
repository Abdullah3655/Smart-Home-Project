package com.smarthome.ui;

import com.smarthome.devices.Device;
import com.smarthome.observer.Observer;
import com.smarthome.persistence.dao.DeviceDAO;
import com.smarthome.persistence.dao.DeviceEventDAO;

import java.util.Objects;

/**
 * OBSERVER PATTERN (boundary adapter) — bridges domain Observer events
 * to the persistence layer.
 *
 * <p>The domain layer (Device, SmartHomeHub, Strategy) must not depend on
 * persistence — that would couple the model to the database. This bridge
 * lives in the UI layer's startup code: it's an {@link Observer} that
 * forwards every device event to (a) the {@link DeviceEventDAO} audit
 * log AND (b) the {@link DeviceDAO} live-state row, so both sides of the
 * persistence contract stay in sync after every state change.</p>
 *
 * <p>One instance is attached to every device on app startup, alongside
 * any in-memory listeners the UI uses for live updates.</p>
 */
public class DaoEventBridge implements Observer {

    private final DeviceEventDAO eventDao;
    private final DeviceDAO deviceDao;

    /** Audit-only constructor — used by tests and by the older code path. */
    public DaoEventBridge(DeviceEventDAO eventDao) {
        this(eventDao, null);
    }

    /** Full constructor — audit + live-state persistence. */
    public DaoEventBridge(DeviceEventDAO eventDao, DeviceDAO deviceDao) {
        this.eventDao = Objects.requireNonNull(eventDao, "eventDao must not be null");
        this.deviceDao = deviceDao;
    }

    @Override
    public void update(Device d, String event) {
        try {
            eventDao.insert(d.getId(), event);
        } catch (Exception e) {
            // Persistence failures must not block the UI or break domain events.
            System.err.println("DaoEventBridge: failed to persist event " + event
                + " for device " + d.getId() + ": " + e.getMessage());
        }
        if (deviceDao != null) {
            try {
                deviceDao.updateState(d);
            } catch (Exception e) {
                System.err.println("DaoEventBridge: failed to update device row for "
                    + d.getId() + ": " + e.getMessage());
            }
        }
    }
}
