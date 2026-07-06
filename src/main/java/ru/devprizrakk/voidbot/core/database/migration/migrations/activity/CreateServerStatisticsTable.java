package ru.devprizrakk.voidbot.core.database.migration.migrations.activity;

import ru.devprizrakk.voidbot.core.database.migration.MigrationInterface;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateServerStatisticsTable implements MigrationInterface {
    @Override
    public int version() {
        return 8;
    }

    @Override
    public String description() {
        return "Create server_statistics table";
    }

    @Override
    public void up(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS server_statistics (
                        date DATE PRIMARY KEY,
                        member_count BIGINT NOT NULL DEFAULT 0,
                        online_count BIGINT NOT NULL DEFAULT 0,
                        boost_count BIGINT NOT NULL DEFAULT 0,
                        channel_count BIGINT NOT NULL DEFAULT 0,
                        role_count BIGINT NOT NULL DEFAULT 0
                    )
                    """);
        }
    }

    @Override
    public void down(Connection connection) throws SQLException {
        MigrationInterface.super.down(connection);
    }
}
