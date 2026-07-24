package ru.devprizrakk.voidbot.database;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseDialect {

    private DatabaseDialect() {
    }

    public static boolean isSqlite(Connection conn) throws SQLException {
        return conn.getMetaData().getURL().toLowerCase().startsWith("jdbc:sqlite");
    }

    public static String idColumn(Connection conn) throws SQLException {
        if (isSqlite(conn)) {
            return "id INTEGER PRIMARY KEY AUTOINCREMENT";
        }
        return "id BIGINT PRIMARY KEY AUTO_INCREMENT";
    }

    public static String boolType(Connection conn) throws SQLException {
        return isSqlite(conn) ? "INTEGER" : "BOOLEAN";
    }
}
