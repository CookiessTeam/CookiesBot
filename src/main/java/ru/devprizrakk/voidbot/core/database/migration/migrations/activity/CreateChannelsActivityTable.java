package ru.devprizrakk.voidbot.core.database.migration.migrations.activity;

import ru.devprizrakk.voidbot.core.database.migration.MigrationInterface;

import java.sql.Connection;
import java.sql.SQLException;

public class CreateChannelsActivityTable implements MigrationInterface {
    @Override
    public int version() {
        return 0;
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public void up(Connection connection) throws SQLException {

    }

    @Override
    public void down(Connection connection) throws SQLException {
        MigrationInterface.super.down(connection);
    }
}
