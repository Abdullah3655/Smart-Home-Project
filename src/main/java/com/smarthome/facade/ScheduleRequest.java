package com.smarthome.facade;

/**
 * Request DTO for {@link HomeController#createSchedule(ScheduleRequest)}.
 * Minimal carrier for schedule creation — mode name and cron expression
 * are required; deviceId is optional (null means "applies to whole hub").
 */
public record ScheduleRequest(String deviceId, String modeName, String cronExpression) {}
