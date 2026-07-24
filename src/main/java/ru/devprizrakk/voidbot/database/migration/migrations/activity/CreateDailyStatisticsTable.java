package ru.devprizrakk.voidbot.database.migration.migrations.activity;

import ru.devprizrakk.voidbot.database.migration.IMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDailyStatisticsTable implements IMigration {

    @Override
    public int version() {
        return 6;
    }

    @Override
    public String description() {
        return "Create daily_statistics table";
    }

    @Override
    public void up(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS daily_statistics (
                        date DATE PRIMARY KEY,
                        messages BIGINT NOT NULL DEFAULT 0,
                        new_members BIGINT NOT NULL DEFAULT 0,
                        left_members BIGINT NOT NULL DEFAULT 0,
                        active_members BIGINT NOT NULL DEFAULT 0,
                        voice_minutes BIGINT NOT NULL DEFAULT 0,
                        warns BIGINT NOT NULL DEFAULT 0,
                        deleted_messages BIGINT NOT NULL DEFAULT 0
                    )
                    """);
        }
    }
}
