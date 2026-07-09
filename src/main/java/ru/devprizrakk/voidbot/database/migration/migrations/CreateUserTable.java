package ru.devprizrakk.voidbot.database.migration.migrations;

import ru.devprizrakk.voidbot.database.DatabaseDialect;
import ru.devprizrakk.voidbot.database.migration.IMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateUserTable implements IMigration {

    @Override
    public int version() {
        return 1;
    }

    @Override
    public String description() {
        return "Create users table";
    }

    @Override
    public void up(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        %s,
                        discord_id BIGINT NOT NULL UNIQUE,
                        username VARCHAR(255),
                        joined_at TIMESTAMP NOT NULL,
                        left_at TIMESTAMP NULL,
                        is_bot %s NOT NULL DEFAULT 0
                    )
                    """.formatted(DatabaseDialect.idColumn(conn), DatabaseDialect.boolType(conn)));
        }
    }
}
