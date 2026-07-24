package ru.devprizrakk.voidbot.database.migration.migrations;

import ru.devprizrakk.voidbot.database.DatabaseDialect;
import ru.devprizrakk.voidbot.database.migration.IMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class AddLevelUpDmColumn implements IMigration {

    @Override
    public int version() {
        return 12;
    }

    @Override
    public String description() {
        return "Add level_up_dm_enabled column to users table";
    }

    @Override
    public void up(Connection conn) throws SQLException {
        String type = DatabaseDialect.boolType(conn);
        String sql = "ALTER TABLE users ADD COLUMN level_up_dm_enabled " + type + " DEFAULT 1";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException ignored) {
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("UPDATE users SET level_up_dm_enabled = 1 WHERE level_up_dm_enabled IS NULL");
        }
    }
}
