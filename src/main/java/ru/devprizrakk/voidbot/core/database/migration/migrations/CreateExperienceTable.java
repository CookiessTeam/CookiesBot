package ru.devprizrakk.voidbot.core.database.migration.migrations;

import ru.devprizrakk.voidbot.core.database.DatabaseDialect;
import ru.devprizrakk.voidbot.core.database.migration.MigrationInterface;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateExperienceTable implements MigrationInterface {
    @Override
    public int version() {
        return 9;
    }

    @Override
    public String description() {
        return "Create experience table";
    }

    @Override
    public void up(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
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
                    """.formatted(DatabaseDialect.idColumn(connection)));
        }
    }

    @Override
    public void down(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS experience");
        }
    }
}