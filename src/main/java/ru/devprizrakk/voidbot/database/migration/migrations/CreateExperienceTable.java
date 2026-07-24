package ru.devprizrakk.voidbot.database.migration.migrations;

import ru.devprizrakk.voidbot.database.DatabaseDialect;
import ru.devprizrakk.voidbot.database.migration.IMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateExperienceTable implements IMigration {

    @Override
    public int version() {
        return 9;
    }

    @Override
    public String description() {
        return "Create experience table";
    }

    @Override
    public void up(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS experience (
                        %s,
                        discord_id BIGINT NOT NULL,
                        guild_id BIGINT NOT NULL,
                        level BIGINT NOT NULL DEFAULT 0,
                        experience BIGINT NOT NULL DEFAULT 0,
                        total_experience BIGINT NOT NULL DEFAULT 0,
                        updated_at TIMESTAMP NULL,
                        UNIQUE (discord_id, guild_id)
                    )
                    """.formatted(DatabaseDialect.idColumn(conn)));
        }
    }

    @Override
    public void down(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS experience");
        }
    }
}