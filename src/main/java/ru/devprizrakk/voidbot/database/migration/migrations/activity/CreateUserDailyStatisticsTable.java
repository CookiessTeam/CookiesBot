package ru.devprizrakk.voidbot.database.migration.migrations.activity;

import ru.devprizrakk.voidbot.database.migration.IMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateUserDailyStatisticsTable implements IMigration {

    @Override
    public int version() {
        return 7;
    }

    @Override
    public String description() {
        return "Create user_daily_statistics table";
    }

    @Override
    public void up(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS user_daily_statistics (
                        date DATE NOT NULL,
                        user_id BIGINT NOT NULL,
                        messages BIGINT NOT NULL DEFAULT 0,
                        voice_minutes BIGINT NOT NULL DEFAULT 0,
                        reactions BIGINT NOT NULL DEFAULT 0,
                        commands BIGINT NOT NULL DEFAULT 0,
                        PRIMARY KEY (date, user_id)
                    )
                    """);
        }
    }
}
