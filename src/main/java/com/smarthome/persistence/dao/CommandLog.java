package com.smarthome.persistence.dao;

import java.time.Instant;

/**
 * A row from the {@code commands_log} audit table.
 * Schema-locked field order: command_id, device_id, action, params_json, result, timestamp.
 */
public record CommandLog(
    String commandId,
    String deviceId,
    String action,
    String paramsJson,
    String result,
    Instant timestamp
) {}
