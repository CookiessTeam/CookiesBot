package ru.devprizrakk.voidbot.core.database.migration.migrations.moderation;

import ru.devprizrakk.voidbot.core.database.DatabaseDialect;
import ru.devprizrakk.voidbot.core.database.migration.MigrationInterface;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateWarnsTable implements MigrationInterface {
    @Override
    public int version() {
        return 5;
    }

    @Override
    public String description() {
        return "Create warns table";
    }

    @Override
    public void up(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS warns (
                        %s,
                        user_id BIGINT NOT NULL,
                        moderator_id BIGINT NOT NULL,
                        reason VARCHAR(255),
                        created_at TIMESTAMP NOT NULL,
                        expired_at TIMESTAMP NULL
                    )
                    """.formatted(DatabaseDialect.idColumn(connection)));
        }
    }

    @Override
    public void down(Connection connection) throws SQLException {
        MigrationInterface.super.down(connection);
    }
}
