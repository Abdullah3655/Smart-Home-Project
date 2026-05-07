package com.smarthome.ui;

import com.smarthome.devices.Device;
import com.smarthome.observer.Observer;
import com.smarthome.persistence.dao.DeviceDAO;
import com.smarthome.persistence.dao.DeviceEventDAO;

import java.util.Objects;


// Observer that persists device events and latest device state to the database.
public class DaoEventBridge implements Observer {

    private final DeviceEventDAO eventDao;
    private final DeviceDAO deviceDao;

    
    public DaoEventBridge(DeviceEventDAO eventDao) {
        this(eventDao, null);
    }

    
    public DaoEventBridge(DeviceEventDAO eventDao, DeviceDAO deviceDao) {
        this.eventDao = Objects.requireNonNull(eventDao, "eventDao must not be null");
        this.deviceDao = deviceDao;
    }

    @Override
    public void update(Device d, String event) {
        try {
            eventDao.insert(d.getId(), event);
        } catch (Exception e) {
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
