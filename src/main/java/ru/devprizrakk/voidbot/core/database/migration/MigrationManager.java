package ru.devprizrakk.voidbot.core.database.migration;

import ru.devprizrakk.voidbot.core.logging.LogType;
import ru.devprizrakk.voidbot.core.logging.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.List;

public class MigrationManager {
    private final List<MigrationInterface> migrations;

    public MigrationManager(List<MigrationInterface> migrations) {
        this.migrations = migrations;
    }

    public void init() {
    }

    public void migrate(Connection connection) throws SQLException {
        createMigrationsTable(connection);

        migrations.stream()
                .sorted(Comparator.comparingInt(MigrationInterface::version))
                .forEach(migration -> {
                    try {
                        if (isApplied(connection, migration)) {
                            return;
                        }

                        migration.up(connection);
                        markApplied(connection, migration);
                    } catch (SQLException e) {
                        Logger.getLogger().log(LogType.ERROR, "DATABASE", "Database migration failed", e);
                    }
                });
    }

    private void createMigrationsTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS schema_migrations (
                        id VARCHAR(255) PRIMARY KEY,
                        version INTEGER NOT NULL,
                        description VARCHAR(255),
                        applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
        }
    }

    private boolean isApplied(Connection connection, MigrationInterface migration) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id FROM schema_migrations WHERE id = ?"
        )) {
            statement.setString(1, migration.getClass().getName());

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private void markApplied(Connection connection, MigrationInterface migration) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO schema_migrations (id, version, description) VALUES (?, ?, ?)"
        )) {
            statement.setString(1, migration.getClass().getName());
            statement.setInt(2, migration.version());
            statement.setString(3, migration.description());
            statement.executeUpdate();
        }
    }
}
