package com.smarthome.observer;

import com.smarthome.devices.Device;

/**
 * OBSERVER PATTERN — Observer role.
 *
 * <p>Implemented by anything that wants to react to a {@link Device}'s
 * state change: UI controllers redraw cards, {@link com.smarthome.ui.DaoEventBridge}
 * persists events to {@code device_events}, history feeds prepend rows.
 * One Observer interface, many concrete observers.</p>
 *
 * <p><b>Locked contract</b> — the signature {@code update(Device d, String event)}
 * is the canonical "push" form: the Subject pushes both the affected device
 * and the event name to every observer in one call. Multiple subsystems
 * depend on this exact shape; do not change.</p>
 */
public interface Observer {
    /**
     * Called by an {@link Observable} after its state changes.
     *
     * @param d the device whose state just changed
     * @param event a short uppercase event name, e.g. {@code "TURNED_ON"},
     *              {@code "LOCKED"}, {@code "TEMP_CHANGED"}
     */
    void update(Device d, String event);
}
