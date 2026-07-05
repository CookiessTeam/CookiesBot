package ru.devprizrakk.voidbot.core.database.migration.migrations;

import ru.devprizrakk.voidbot.core.database.DatabaseDialect;
import ru.devprizrakk.voidbot.core.database.migration.MigrationInterface;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateMemberEventsTable implements MigrationInterface {
    @Override
    public int version() {
        return 4;
    }

    @Override
    public String description() {
        return "Create member_events table";
    }

    @Override
    public void up(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS member_events (
                        %s,
                        user_id BIGINT NOT NULL,
                        event_type VARCHAR(32) NOT NULL,
                        created_at TIMESTAMP NOT NULL
                    )
                    """.formatted(DatabaseDialect.idColumn(connection)));
        }
    }
}
