package ru.devprizrakk.voidbot.database.migration;

import java.sql.Connection;
import java.sql.SQLException;

public interface IMigration {
    int version();

    String description();

    void up(Connection conn) throws SQLException;

    default void down(Connection conn) throws SQLException {
        throw new UnsupportedOperationException();
    }
}
