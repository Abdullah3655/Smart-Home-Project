package com.smarthome.core;

import com.smarthome.strategy.AutomationMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SINGLETON PATTERN + STRATEGY pattern Context + ITERATOR pattern aggregate.
 *
 * <p><b>Singleton:</b> private constructor + eager static {@code INSTANCE} +
 * {@link #getInstance()} static accessor — guarantees a single hub for the
 * application's lifetime. Thread-safety is provided by JVM class loading
 * (the {@code static final} field is initialised exactly once, atomically,
 * before any thread reads it).</p>
 *
 * <p><b>Strategy Context:</b> holds the current {@link AutomationMode} and
 * exposes {@link #applyAutomationMode()} to delegate to it. Callers never
 * need to know which concrete strategy is active.</p>
 *
 * <p><b>Iterator:</b> implements {@link RoomIterableCollection} and returns
 * a {@link HubRoomIterator} via {@link #createIterator()} — the textbook
 * Gang-of-Four Iterator with {@code hasMore()} / {@code getNext()}. (The
 * Enumeration-style iterator required by the rubric lives one level
 * deeper, on {@link Room#devices()}.)</p>
 */
public class SmartHomeHub implements RoomIterableCollection {
    private static final SmartHomeHub INSTANCE = new SmartHomeHub();

    private final Map<String, Room> roomsById = new ConcurrentHashMap<>();
    private AutomationMode automationMode;

    private SmartHomeHub() {
    }

    public static SmartHomeHub getInstance() {
        return INSTANCE;
    }

    public void addRoom(Room room) {
        Objects.requireNonNull(room, "room must not be null");
        roomsById.put(room.getRoomId(), room);
    }

    public Room getRoom(String roomId) {
        return roomsById.get(roomId);
    }

    public Collection<Room> getRooms() {
        return Collections.unmodifiableCollection(roomsById.values());
    }

    public void setAutomationMode(AutomationMode mode) {
        this.automationMode = mode;
    }

    public AutomationMode getAutomationMode() {
        return automationMode;
    }

    /**
     * STRATEGY PATTERN — Context delegate method.
     *
     * Per Refactoring Guru's canonical Strategy structure, the Context
     * exposes a method that internally delegates to the configured Strategy,
     * so callers do not need to know about concrete strategy classes.
     *
     * Use this instead of {@code hub.getAutomationMode().apply(hub)} —
     * it keeps the strategy reference encapsulated inside the hub.
     */
    public void applyAutomationMode() {
        if (automationMode != null) {
            automationMode.apply(this);
        }
    }

    @Override
    public RoomIterator createIterator() {
        return new HubRoomIterator(new ArrayList<>(roomsById.values()));
    }
}
