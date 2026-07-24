package ru.devprizrakk.voidbot.database.migration.migrations.moderation;

import ru.devprizrakk.voidbot.database.DatabaseDialect;
import ru.devprizrakk.voidbot.database.migration.IMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateMutesTable implements IMigration {

    @Override
    public int version() {
        return 11;
    }

    @Override
    public String description() {
        return "Create mutes table";
    }

    @Override
    public void up(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS mutes (
                        %s,
                        user_id BIGINT NOT NULL,
                        moderation_id BIGINT NOT NULL,
                        description VARCHAR(255),
                        created_at TIMESTAMP NOT NULL,
                        expired_at TIMESTAMP NULL
                    )
                    """.formatted(DatabaseDialect.idColumn(conn)));
        }
    }
}