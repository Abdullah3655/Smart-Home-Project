package com.smarthome.persistence.dao;

import java.time.Instant;

/** A row from the {@code device_events} audit table. */
public record DeviceEvent(long eventId, String deviceId, String eventType, Instant timestamp) {}
