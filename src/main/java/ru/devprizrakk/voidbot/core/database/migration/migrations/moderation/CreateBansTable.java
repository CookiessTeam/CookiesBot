package ru.devprizrakk.voidbot.core.database.migration.migrations.moderation;

import ru.devprizrakk.voidbot.core.database.DatabaseDialect;
import ru.devprizrakk.voidbot.core.database.migration.MigrationInterface;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateBansTable implements MigrationInterface {
    @Override
    public int version() {
        return 10;
    }

    @Override
    public String description() {
        return "Create bans table";
    }

    @Override
    public void up(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS bans (
                        %s,
                        user_id BIGINT NOT NULL,
                        moderation_id BIGINT NOT NULL,
                        description VARCHAR(255),
                        created_at TIMESTAMP NOT NULL,
                        expired_at TIMESTAMP NULL
                    )
                    """.formatted(DatabaseDialect.idColumn(connection)));
        }
    }
}