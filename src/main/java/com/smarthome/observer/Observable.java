package com.smarthome.observer;

/**
 * OBSERVER PATTERN — Subject role.
 *
 * <p>Anything that exposes "I have state that may change" implements this:
 * primarily {@link com.smarthome.devices.Device}. Subjects keep a list of
 * attached {@link Observer}s and call {@link #notifyObservers(String)}
 * after every state mutation to push the change out.</p>
 *
 * <p>Three required methods: <b>attach / detach / notifyObservers</b>.</p>
 */
public interface Observable {
    /** Register an observer to be notified of future state changes. */
    void attach(Observer observer);

    /** Unregister a previously-attached observer. Safe if it wasn't attached. */
    void detach(Observer observer);

    /**
     * Push a state-change event to every attached observer.
     * Implementations should iterate a defensive copy of the observer list
     * so observers can detach themselves during the callback.
     */
    void notifyObservers(String event);
}
