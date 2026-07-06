package ru.devprizrakk.voidbot.core.database.migration.migrations.activity;

import ru.devprizrakk.voidbot.core.database.DatabaseDialect;
import ru.devprizrakk.voidbot.core.database.migration.MigrationInterface;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateVoiceSessionsTable implements MigrationInterface {
    @Override
    public int version() {
        return 3;
    }

    @Override
    public String description() {
        return "Create voice_sessions table";
    }

    @Override
    public void up(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS voice_sessions (
                        %s,
                        user_id BIGINT NOT NULL,
                        channel_id BIGINT NOT NULL,
                        joined_at TIMESTAMP NOT NULL,
                        left_at TIMESTAMP NULL,
                        duration_seconds BIGINT NOT NULL DEFAULT 0
                    )
                    """.formatted(DatabaseDialect.idColumn(connection)));
        }
    }

    @Override
    public void down(Connection connection) throws SQLException {
        MigrationInterface.super.down(connection);
    }
}
