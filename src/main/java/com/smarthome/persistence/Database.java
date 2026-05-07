package com.smarthome.persistence;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * SINGLETON PATTERN — SQLite connection holder.
 *
 * Owns a single JDBC Connection for the production app, lazily opened
 * against {@code smarthome.db}. On first use, runs {@code db/schema.sql}
 * from the classpath to ensure all tables exist (idempotent: every
 * statement uses CREATE IF NOT EXISTS).
 *
 * For tests, use {@link #forUrl(String)} to get an isolated instance
 * (e.g. {@code Database.forUrl("jdbc:sqlite::memory:")}) so tests don't
 * pollute the real {@code smarthome.db} file.
 */
public final class Database {
    private static final String PRODUCTION_URL = "jdbc:sqlite:smarthome.db";
    private static final Database INSTANCE = new Database(PRODUCTION_URL);

    private final Connection connection;

    private Database(String jdbcUrl) {
        try {
            this.connection = DriverManager.getConnection(jdbcUrl);
            initSchema();
            runMigrations();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SQLite database at " + jdbcUrl, e);
        }
    }

    /** Production singleton — opens {@code smarthome.db}. */
    public static Database getInstance() {
        return INSTANCE;
    }

    /**
     * Test factory — returns a NEW Database instance bound to the given JDBC URL.
     * Use {@code "jdbc:sqlite::memory:"} for ephemeral test databases.
     */
    public static Database forUrl(String jdbcUrl) {
        return new Database(jdbcUrl);
    }

    public Connection getConnection() {
        return connection;
    }

    private void initSchema() throws Exception {
        try (var in = getClass().getResourceAsStream("/db/schema.sql");
             var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            // Strip line-comments BEFORE splitting on ';' — otherwise a comment
            // line attached to the next statement causes the statement to be
            // skipped (its trimmed form starts with "--").
            String sql = reader.lines()
                .map(line -> {
                    int commentStart = line.indexOf("--");
                    return commentStart >= 0 ? line.substring(0, commentStart) : line;
                })
                .collect(Collectors.joining("\n"));
            try (Statement stmt = connection.createStatement()) {
                for (String statement : sql.split(";")) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty()) {
                        stmt.execute(trimmed);
                    }
                }
            }
        }
    }

    /**
     * Idempotent schema migrations for databases that pre-date a column
     * being added to {@code schema.sql}. Each migration is wrapped in
     * a try/catch that swallows "duplicate column" errors so the call
     * is safe on every run, fresh or upgraded.
     *
     * <p>If you add a new column to schema.sql in the future, also add
     * an ALTER TABLE here so existing developer/grader databases pick
     * up the change without manual deletion of {@code smarthome.db}.</p>
     */
    private void runMigrations() {
        // 2026-05-07: added family column to devices table for Abstract
        // Factory variant tracking (Version1 vs Version2).
        tryMigrate(
            "ALTER TABLE devices ADD COLUMN family TEXT NOT NULL DEFAULT 'VERSION2'");

        // 2026-05-07: renamed state_json to state_blob. Older DBs may have
        // either column; prefer state_blob if missing.
        tryMigrate(
            "ALTER TABLE devices ADD COLUMN state_blob TEXT");
    }

    private void tryMigrate(String alterSql) {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(alterSql);
        } catch (Exception e) {
            // SQLite throws "duplicate column name" or "no such table" if
            // the migration was already applied or the table doesn't
            // exist yet. Both are non-fatal — schema.sql will create the
            // table fresh next time and we stay consistent.
            String msg = e.getMessage() == null ? "" : e.getMessage();
            if (!msg.contains("duplicate column") && !msg.contains("no such table")) {
                System.err.println("Migration warning: " + msg);
            }
        }
    }
}
