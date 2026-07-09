package ru.devprizrakk.voidbot.database.migration.migrations.activity;

import ru.devprizrakk.voidbot.database.migration.IMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateServerStatisticsTable implements IMigration {

    @Override
    public int version() {
        return 8;
    }

    @Override
    public String description() {
        return "Create server_statistics table";
    }

    @Override
    public void up(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
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
}
