package ru.devprizrakk.voidbot.database.migration.migrations.activity;

import ru.devprizrakk.voidbot.database.DatabaseDialect;
import ru.devprizrakk.voidbot.database.migration.IMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateVoiceSessionsTable implements IMigration {

    @Override
    public int version() {
        return 3;
    }

    @Override
    public String description() {
        return "Create voice_sessions table";
    }

    @Override
    public void up(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS voice_sessions (
                        %s,
                        user_id BIGINT NOT NULL,
                        channel_id BIGINT NOT NULL,
                        joined_at TIMESTAMP NOT NULL,
                        left_at TIMESTAMP NULL,
                        duration_seconds BIGINT NOT NULL DEFAULT 0
                    )
                    """.formatted(DatabaseDialect.idColumn(conn)));
        }
    }
}
