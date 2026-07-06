package ru.devprizrakk.voidbot.core.database.migration.migrations.activity;

import ru.devprizrakk.voidbot.core.database.migration.MigrationInterface;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateUserDailyStatisticsTable implements MigrationInterface {
    @Override
    public int version() {
        return 7;
    }

    @Override
    public String description() {
        return "Create user_daily_statistics table";
    }

    @Override
    public void up(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
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

    @Override
    public void down(Connection connection) throws SQLException {
        MigrationInterface.super.down(connection);
    }
}
