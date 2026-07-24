package ru.devprizrakk.voidbot.database.repository;

import ru.devprizrakk.voidbot.database.DatabaseManager;
import ru.devprizrakk.voidbot.database.model.DailyStatisticsModel;
import ru.devprizrakk.voidbot.database.model.DailyStatisticsColumn;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DailyStatisticsRepository {

    private final DatabaseManager databaseManager;

    public DailyStatisticsRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public DailyStatisticsModel save(DailyStatisticsModel statistics) throws SQLException {
        if (update(statistics) == 0) {
            insert(statistics);
        }

        return statistics;
    }

    private int update(DailyStatisticsModel statistics) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     UPDATE daily_statistics
                     SET messages = ?, new_members = ?, left_members = ?, active_members = ?,
                         voice_minutes = ?, warns = ?, deleted_messages = ?
                     WHERE date = ?
                     """)) {
            stmt.setLong(1, statistics.getMessages());
            stmt.setLong(2, statistics.getNewMembers());
            stmt.setLong(3, statistics.getLeftMembers());
            stmt.setLong(4, statistics.getActiveMembers());
            stmt.setLong(5, statistics.getVoiceMinutes());
            stmt.setLong(6, statistics.getWarns());
            stmt.setLong(7, statistics.getDeletedMessages());
            stmt.setDate(8, statistics.getDate());

            return stmt.executeUpdate();
        }
    }

    private void insert(DailyStatisticsModel statistics) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     INSERT INTO daily_statistics
                     (date, messages, new_members, left_members, active_members, voice_minutes, warns, deleted_messages)
                     VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                     """)) {
            stmt.setDate(1, statistics.getDate());
            stmt.setLong(2, statistics.getMessages());
            stmt.setLong(3, statistics.getNewMembers());
            stmt.setLong(4, statistics.getLeftMembers());
            stmt.setLong(5, statistics.getActiveMembers());
            stmt.setLong(6, statistics.getVoiceMinutes());
            stmt.setLong(7, statistics.getWarns());
            stmt.setLong(8, statistics.getDeletedMessages());
            stmt.executeUpdate();
        }
    }

    public void increment(Date date, DailyStatisticsColumn column, long delta) throws SQLException {
        if (delta == 0) return;

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     INSERT INTO daily_statistics (date, messages, new_members, left_members, active_members, voice_minutes, warns, deleted_messages)
                     VALUES (?, 0, 0, 0, 0, 0, 0, 0)
                     ON CONFLICT(date) DO NOTHING
                     """)) {
            stmt.setDate(1, date);
            stmt.executeUpdate();
        } catch ( SQLException e ) {
            Logger.getLogger().log(LogType.WARN, "DATABASE", "Insert-guard для daily_statistics не сработал", e);
        }

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE daily_statistics SET " + column.getColumn() + " = " + column.getColumn() + " + ? WHERE date = ?")) {
            stmt.setLong(1, delta);
            stmt.setDate(2, date);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                try (PreparedStatement ins = conn.prepareStatement("""
                        INSERT INTO daily_statistics (date, messages, new_members, left_members, active_members, voice_minutes, warns, deleted_messages)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                        """)) {
                    ins.setDate(1, date);
                    ins.setLong(2, column == DailyStatisticsColumn.MESSAGES ? delta : 0);
                    ins.setLong(3, column == DailyStatisticsColumn.NEW_MEMBERS ? delta : 0);
                    ins.setLong(4, column == DailyStatisticsColumn.LEFT_MEMBERS ? delta : 0);
                    ins.setLong(5, column == DailyStatisticsColumn.ACTIVE_MEMBERS ? delta : 0);
                    ins.setLong(6, column == DailyStatisticsColumn.VOICE_MINUTES ? delta : 0);
                    ins.setLong(7, column == DailyStatisticsColumn.WARNS ? delta : 0);
                    ins.setLong(8, column == DailyStatisticsColumn.DELETED_MESSAGES ? delta : 0);
                    ins.executeUpdate();
                }
            }
        }
    }

    public boolean deleteByDate(Date date) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM daily_statistics WHERE date = ?")) {
            stmt.setDate(1, date);

            return stmt.executeUpdate() > 0;
        }
    }

    public List<DailyStatisticsModel> findAll() throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM daily_statistics");
             ResultSet rs = stmt.executeQuery()) {

            List<DailyStatisticsModel> items = new ArrayList<>();
            while (rs.next()) items.add(map(rs));
            return items;
        }
    }

    public Optional<DailyStatisticsModel> findByDate(Date date) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM daily_statistics WHERE date = ?")) {
            stmt.setDate(1, date);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    private DailyStatisticsModel map(ResultSet rs) throws SQLException {
        return new DailyStatisticsModel(
                rs.getDate("date"),
                rs.getLong("messages"),
                rs.getLong("new_members"),
                rs.getLong("left_members"),
                rs.getLong("active_members"),
                rs.getLong("voice_minutes"),
                rs.getLong("warns"),
                rs.getLong("deleted_messages")
        );
    }
}
