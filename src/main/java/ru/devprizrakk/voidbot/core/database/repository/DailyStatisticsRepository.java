package ru.devprizrakk.voidbot.core.database.repository;

import ru.devprizrakk.voidbot.core.database.DatabaseManager;
import ru.devprizrakk.voidbot.core.database.model.DailyStatistics;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DailyStatisticsRepository {
    private final DatabaseManager databaseManager;

    public DailyStatisticsRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public DailyStatistics save(DailyStatistics statistics) throws SQLException {
        if (update(statistics) == 0) {
            insert(statistics);
        }
        return statistics;
    }

    public void increment(Date date, String column, long delta) throws SQLException {
        if (delta == 0) return;
        if (!column.matches("messages|new_members|left_members|active_members|voice_minutes|warns|deleted_messages")) {
            throw new IllegalArgumentException("Invalid column: " + column);
        }
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement st = connection.prepareStatement("""
                     INSERT INTO daily_statistics (date, messages, new_members, left_members, active_members, voice_minutes, warns, deleted_messages)
                     VALUES (?, 0, 0, 0, 0, 0, 0, 0)
                     ON CONFLICT(date) DO NOTHING
                     """)) {
            st.setDate(1, date);
            st.executeUpdate();
        } catch (SQLException ignored) {
            // OnConflict может не поддерживаться — пробуем простой upsert ниже
        }
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement st = connection.prepareStatement(
                     "UPDATE daily_statistics SET " + column + " = " + column + " + ? WHERE date = ?"
             )) {
            st.setLong(1, delta);
            st.setDate(2, date);
            int rows = st.executeUpdate();
            if (rows == 0) {
                try (PreparedStatement ins = connection.prepareStatement("""
                        INSERT INTO daily_statistics (date, messages, new_members, left_members, active_members, voice_minutes, warns, deleted_messages)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                        """)) {
                    ins.setDate(1, date);
                    ins.setLong(2, "messages".equals(column) ? delta : 0);
                    ins.setLong(3, "new_members".equals(column) ? delta : 0);
                    ins.setLong(4, "left_members".equals(column) ? delta : 0);
                    ins.setLong(5, "active_members".equals(column) ? delta : 0);
                    ins.setLong(6, "voice_minutes".equals(column) ? delta : 0);
                    ins.setLong(7, "warns".equals(column) ? delta : 0);
                    ins.setLong(8, "deleted_messages".equals(column) ? delta : 0);
                    ins.executeUpdate();
                }
            }
        }
    }

    public Optional<DailyStatistics> findByDate(Date date) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM daily_statistics WHERE date = ?")) {
            statement.setDate(1, date);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    public boolean deleteByDate(Date date) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM daily_statistics WHERE date = ?")) {
            statement.setDate(1, date);
            return statement.executeUpdate() > 0;
        }
    }

    public List<DailyStatistics> findAll() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM daily_statistics");
             ResultSet resultSet = statement.executeQuery()) {
            List<DailyStatistics> items = new ArrayList<>();
            while (resultSet.next()) {
                items.add(map(resultSet));
            }
            return items;
        }
    }

    private void insert(DailyStatistics statistics) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     INSERT INTO daily_statistics
                     (date, messages, new_members, left_members, active_members, voice_minutes, warns, deleted_messages)
                     VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                     """)) {
            fill(statement, statistics);
            statement.executeUpdate();
        }
    }

    private int update(DailyStatistics statistics) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE daily_statistics
                     SET messages = ?, new_members = ?, left_members = ?, active_members = ?,
                         voice_minutes = ?, warns = ?, deleted_messages = ?
                     WHERE date = ?
                     """)) {
            statement.setLong(1, statistics.getMessages());
            statement.setLong(2, statistics.getNewMembers());
            statement.setLong(3, statistics.getLeftMembers());
            statement.setLong(4, statistics.getActiveMembers());
            statement.setLong(5, statistics.getVoiceMinutes());
            statement.setLong(6, statistics.getWarns());
            statement.setLong(7, statistics.getDeletedMessages());
            statement.setDate(8, statistics.getDate());
            return statement.executeUpdate();
        }
    }

    private void fill(PreparedStatement statement, DailyStatistics statistics) throws SQLException {
        statement.setDate(1, statistics.getDate());
        statement.setLong(2, statistics.getMessages());
        statement.setLong(3, statistics.getNewMembers());
        statement.setLong(4, statistics.getLeftMembers());
        statement.setLong(5, statistics.getActiveMembers());
        statement.setLong(6, statistics.getVoiceMinutes());
        statement.setLong(7, statistics.getWarns());
        statement.setLong(8, statistics.getDeletedMessages());
    }

    private DailyStatistics map(ResultSet resultSet) throws SQLException {
        return new DailyStatistics(
                resultSet.getDate("date"),
                resultSet.getLong("messages"),
                resultSet.getLong("new_members"),
                resultSet.getLong("left_members"),
                resultSet.getLong("active_members"),
                resultSet.getLong("voice_minutes"),
                resultSet.getLong("warns"),
                resultSet.getLong("deleted_messages")
        );
    }
}
