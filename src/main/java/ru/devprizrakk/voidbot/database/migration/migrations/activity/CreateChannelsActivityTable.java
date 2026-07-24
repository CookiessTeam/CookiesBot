package ru.devprizrakk.voidbot.database.migration.migrations.activity;

import ru.devprizrakk.voidbot.database.migration.IMigration;

import java.sql.Connection;
import java.sql.SQLException;

public class CreateChannelsActivityTable implements IMigration {

    @Override
    public int version() {
        return 0;
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public void up(Connection conn) throws SQLException {

    }
}
