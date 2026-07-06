package ru.devprizrakk.voidbot.core.database;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseDialect {
    private DatabaseDialect() {
    }

    public static boolean isSqlite(Connection connection) throws SQLException {
        return connection.getMetaData().getURL().toLowerCase().startsWith("jdbc:sqlite");
    }

    public static String idColumn(Connection connection) throws SQLException {
        if (isSqlite(connection)) {
            return "id INTEGER PRIMARY KEY AUTOINCREMENT";
        }
        return "id BIGINT PRIMARY KEY AUTO_INCREMENT";
    }

    public static String boolType(Connection connection) throws SQLException {
        return isSqlite(connection) ? "INTEGER" : "BOOLEAN";
    }
}
