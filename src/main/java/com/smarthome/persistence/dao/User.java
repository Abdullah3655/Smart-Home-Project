package com.smarthome.persistence.dao;

/**
 * Data carrier for a smart-home user account.
 * Records auto-generate equals/hashCode/toString — perfect for DTOs.
 */
public record User(String userId, String name, String pin, String role) {}
