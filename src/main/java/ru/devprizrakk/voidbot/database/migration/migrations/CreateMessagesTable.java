package ru.devprizrakk.voidbot.database.migration.migrations;

import ru.devprizrakk.voidbot.database.DatabaseDialect;
import ru.devprizrakk.voidbot.database.migration.IMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateMessagesTable implements IMigration {

    @Override
    public int version() {
        return 2;
    }

    @Override
    public String description() {
        return "Create messages table";
    }

    @Override
    public void up(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS messages (
                        %s,
                        message_id BIGINT NOT NULL,
                        user_id BIGINT NOT NULL,
                        channel_id BIGINT NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        deleted_at TIMESTAMP NULL
                    )
                    """.formatted(DatabaseDialect.idColumn(conn)));
        }
    }
}
