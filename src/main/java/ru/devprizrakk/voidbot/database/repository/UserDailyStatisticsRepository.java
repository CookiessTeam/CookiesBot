package ru.devprizrakk.voidbot.database.repository;

import ru.devprizrakk.voidbot.database.DatabaseManager;
import ru.devprizrakk.voidbot.database.model.UserDailyStatisticsModel;
import ru.devprizrakk.voidbot.database.model.UserDailyStatisticsColumn;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDailyStatisticsRepository {

    private final DatabaseManager databaseManager;

    public UserDailyStatisticsRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public UserDailyStatisticsModel save(UserDailyStatisticsModel statistics) throws SQLException {
        if (update(statistics) == 0) {
            insert(statistics);
        }

        return statistics;
    }

    private int update(UserDailyStatisticsModel statistics) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     UPDATE user_daily_statistics
                     SET messages = ?, voice_minutes = ?, reactions = ?, commands = ?
                     WHERE date = ? AND user_id = ?
                     """)) {
            stmt.setLong(1, statistics.getMessages());
            stmt.setLong(2, statistics.getVoiceMinutes());
            stmt.setLong(3, statistics.getReactions());
            stmt.setLong(4, statistics.getCommands());
            stmt.setDate(5, statistics.getDate());
            stmt.setLong(6, statistics.getUserId());

            return stmt.executeUpdate();
        }
    }

    private void insert(UserDailyStatisticsModel statistics) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     INSERT INTO user_daily_statistics
                     (date, user_id, messages, voice_minutes, reactions, commands)
                     VALUES (?, ?, ?, ?, ?, ?)
                     """)) {
            stmt.setDate(1, statistics.getDate());
            stmt.setLong(2, statistics.getUserId());
            stmt.setLong(3, statistics.getMessages());
            stmt.setLong(4, statistics.getVoiceMinutes());
            stmt.setLong(5, statistics.getReactions());
            stmt.setLong(6, statistics.getCommands());
            stmt.executeUpdate();
        }
    }

    public void increment(Date date, long userId, UserDailyStatisticsColumn column, long delta) throws SQLException {
        if (delta == 0) return;

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE user_daily_statistics SET " + column + " = " + column + " + ? WHERE date = ? AND user_id = ?")) {
            stmt.setLong(1, delta);
            stmt.setDate(2, date);
            stmt.setLong(3, userId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                try (PreparedStatement ins = conn.prepareStatement("""
                        INSERT INTO user_daily_statistics (date, user_id, messages, voice_minutes, reactions, commands)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """)) {
                    ins.setDate(1, date);
                    ins.setLong(2, userId);
                    ins.setLong(3, column == UserDailyStatisticsColumn.MESSAGES ? delta : 0);
                    ins.setLong(4, column == UserDailyStatisticsColumn.VOICE_MINUTES ? delta : 0);
                    ins.setLong(5, column == UserDailyStatisticsColumn.REACTIONS ? delta : 0);
                    ins.setLong(6, column == UserDailyStatisticsColumn.COMMANDS ? delta : 0);
                    ins.executeUpdate();
                }
            }
        }
    }

    public boolean deleteByDateAndUserId(Date date, long userId) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM user_daily_statistics WHERE date = ? AND user_id = ?")) {
            stmt.setDate(1, date);
            stmt.setLong(2, userId);

            return stmt.executeUpdate() > 0;
        }
    }

    public List<UserDailyStatisticsModel> findAll() throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_daily_statistics");
             ResultSet rs = stmt.executeQuery()) {

            List<UserDailyStatisticsModel> items = new ArrayList<>();
            while (rs.next()) items.add(map(rs));
            return items;
        }
    }

    public Optional<UserDailyStatisticsModel> findByDateAndUserId(Date date, long userId) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_daily_statistics WHERE date = ? AND user_id = ?")) {
            stmt.setDate(1, date);
            stmt.setLong(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    private UserDailyStatisticsModel map(ResultSet rs) throws SQLException {
        return new UserDailyStatisticsModel(
                rs.getDate("date"),
                rs.getLong("user_id"),
                rs.getLong("messages"),
                rs.getLong("voice_minutes"),
                rs.getLong("reactions"),
                rs.getLong("commands")
        );
    }
}
