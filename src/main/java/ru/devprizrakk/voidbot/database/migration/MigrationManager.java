package ru.devprizrakk.voidbot.database.migration;

import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;

import java.sql.*;
import java.util.Comparator;
import java.util.List;

public class MigrationManager {

    private final List<IMigration> migrations;

    public MigrationManager(List<IMigration> migrations) {
        this.migrations = migrations;
    }

    public void migrate(Connection conn) throws SQLException {
        createMigrationsTable(conn);

        migrations.stream()
                .sorted(Comparator.comparingInt(IMigration::version))
                .forEach(migration -> {
                    try {
                        if (isApplied(conn, migration)) {
                            return;
                        }

                        migration.up(conn);
                        markApplied(conn, migration);
                    } catch ( SQLException e ) {
                        Logger.getLogger().log(LogType.ERROR, "DATABASE", "Database migration failed", e);
                    }
                });
    }

    private void createMigrationsTable(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS schema_migrations (
                        id VARCHAR(255) PRIMARY KEY,
                        version INTEGER NOT NULL,
                        description VARCHAR(255),
                        applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
        }
    }

    private boolean isApplied(Connection conn, IMigration migration) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM schema_migrations WHERE id = ?")) {
            stmt.setString(1, migration.getClass().getName());

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void markApplied(Connection conn, IMigration migration) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO schema_migrations (id, version, description) VALUES (?, ?, ?)")) {
            stmt.setString(1, migration.getClass().getName());
            stmt.setInt(2, migration.version());
            stmt.setString(3, migration.description());
            stmt.executeUpdate();
        }
    }
}
