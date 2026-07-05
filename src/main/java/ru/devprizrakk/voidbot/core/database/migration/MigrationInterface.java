package ru.devprizrakk.voidbot.core.database.migration;

import java.sql.Connection;
import java.sql.SQLException;

public interface MigrationInterface {
    int version();

    String description();

    void up(Connection connection) throws SQLException;

    default void down(Connection connection) throws SQLException {
        throw new UnsupportedOperationException();
    }
}
