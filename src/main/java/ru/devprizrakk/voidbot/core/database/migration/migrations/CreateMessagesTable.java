package ru.devprizrakk.voidbot.core.database.migration.migrations;

import ru.devprizrakk.voidbot.core.database.DatabaseDialect;
import ru.devprizrakk.voidbot.core.database.migration.MigrationInterface;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateMessagesTable implements MigrationInterface {
    @Override
    public int version() {
        return 2;
    }

    @Override
    public String description() {
        return "Create messages table";
    }

    @Override
    public void up(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS messages (
                        %s,
                        message_id BIGINT NOT NULL,
                        user_id BIGINT NOT NULL,
                        channel_id BIGINT NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        deleted_at TIMESTAMP NULL
                    )
                    """.formatted(DatabaseDialect.idColumn(connection)));
        }
    }
}
