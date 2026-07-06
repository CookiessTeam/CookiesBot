package ru.devprizrakk.voidbot.core.database.repository;

import ru.devprizrakk.voidbot.core.database.DatabaseManager;
import ru.devprizrakk.voidbot.core.database.model.UserDailyStatistics;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDailyStatisticsRepository {
    private final DatabaseManager databaseManager;

    public UserDailyStatisticsRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public UserDailyStatistics save(UserDailyStatistics statistics) throws SQLException {
        if (update(statistics) == 0) {
            insert(statistics);
        }
        return statistics;
    }

    public void increment(Date date, long userId, String column, long delta) throws SQLException {
        if (delta == 0) return;
        if (!column.matches("messages|voice_minutes|reactions|commands")) {
            throw new IllegalArgumentException("Invalid column: " + column);
        }
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement st = connection.prepareStatement(
                     "UPDATE user_daily_statistics SET " + column + " = " + column + " + ? WHERE date = ? AND user_id = ?"
             )) {
            st.setLong(1, delta);
            st.setDate(2, date);
            st.setLong(3, userId);
            int rows = st.executeUpdate();
            if (rows == 0) {
                try (PreparedStatement ins = connection.prepareStatement("""
                        INSERT INTO user_daily_statistics (date, user_id, messages, voice_minutes, reactions, commands)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """)) {
                    ins.setDate(1, date);
                    ins.setLong(2, userId);
                    ins.setLong(3, "messages".equals(column) ? delta : 0);
                    ins.setLong(4, "voice_minutes".equals(column) ? delta : 0);
                    ins.setLong(5, "reactions".equals(column) ? delta : 0);
                    ins.setLong(6, "commands".equals(column) ? delta : 0);
                    ins.executeUpdate();
                }
            }
        }
    }

    public Optional<UserDailyStatistics> findByDateAndUserId(Date date, long userId) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM user_daily_statistics WHERE date = ? AND user_id = ?"
             )) {
            statement.setDate(1, date);
            statement.setLong(2, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    public List<UserDailyStatistics> findAll() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM user_daily_statistics");
             ResultSet resultSet = statement.executeQuery()) {
            List<UserDailyStatistics> items = new ArrayList<>();
            while (resultSet.next()) {
                items.add(map(resultSet));
            }
            return items;
        }
    }

    public boolean deleteByDateAndUserId(Date date, long userId) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM user_daily_statistics WHERE date = ? AND user_id = ?"
             )) {
            statement.setDate(1, date);
            statement.setLong(2, userId);
            return statement.executeUpdate() > 0;
        }
    }

    private void insert(UserDailyStatistics statistics) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     INSERT INTO user_daily_statistics
                     (date, user_id, messages, voice_minutes, reactions, commands)
                     VALUES (?, ?, ?, ?, ?, ?)
                     """)) {
            statement.setDate(1, statistics.getDate());
            statement.setLong(2, statistics.getUserId());
            statement.setLong(3, statistics.getMessages());
            statement.setLong(4, statistics.getVoiceMinutes());
            statement.setLong(5, statistics.getReactions());
            statement.setLong(6, statistics.getCommands());
            statement.executeUpdate();
        }
    }

    private int update(UserDailyStatistics statistics) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE user_daily_statistics
                     SET messages = ?, voice_minutes = ?, reactions = ?, commands = ?
                     WHERE date = ? AND user_id = ?
                     """)) {
            statement.setLong(1, statistics.getMessages());
            statement.setLong(2, statistics.getVoiceMinutes());
            statement.setLong(3, statistics.getReactions());
            statement.setLong(4, statistics.getCommands());
            statement.setDate(5, statistics.getDate());
            statement.setLong(6, statistics.getUserId());
            return statement.executeUpdate();
        }
    }

    private UserDailyStatistics map(ResultSet resultSet) throws SQLException {
        return new UserDailyStatistics(
                resultSet.getDate("date"),
                resultSet.getLong("user_id"),
                resultSet.getLong("messages"),
                resultSet.getLong("voice_minutes"),
                resultSet.getLong("reactions"),
                resultSet.getLong("commands")
        );
    }
}
