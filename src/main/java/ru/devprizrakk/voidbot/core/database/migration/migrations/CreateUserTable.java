package ru.devprizrakk.voidbot.core.database.migration.migrations;

import ru.devprizrakk.voidbot.core.database.DatabaseDialect;
import ru.devprizrakk.voidbot.core.database.migration.MigrationInterface;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateUserTable implements MigrationInterface {
    @Override
    public int version() {
        return 1;
    }

    @Override
    public String description() {
        return "Create users table";
    }

    @Override
    public void up(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        %s,
                        discord_id BIGINT NOT NULL UNIQUE,
                        username VARCHAR(255),
                        joined_at TIMESTAMP NOT NULL,
                        left_at TIMESTAMP NULL,
                        is_bot %s NOT NULL DEFAULT 0
                    )
                    """.formatted(DatabaseDialect.idColumn(connection), DatabaseDialect.boolType(connection)));
        }
    }

    @Override
    public void down(Connection connection) throws SQLException {
        MigrationInterface.super.down(connection);
    }
}
