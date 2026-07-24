package ru.devprizrakk.voidbot.database.migration.migrations.moderation;

import ru.devprizrakk.voidbot.database.DatabaseDialect;
import ru.devprizrakk.voidbot.database.migration.IMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateWarnsTable implements IMigration {

    @Override
    public int version() {
        return 5;
    }

    @Override
    public String description() {
        return "Create warns table";
    }

    @Override
    public void up(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS warns (
                        %s,
                        user_id BIGINT NOT NULL,
                        moderator_id BIGINT NOT NULL,
                        reason VARCHAR(255),
                        created_at TIMESTAMP NOT NULL,
                        expired_at TIMESTAMP NULL
                    )
                    """.formatted(DatabaseDialect.idColumn(conn)));
        }
    }
}
