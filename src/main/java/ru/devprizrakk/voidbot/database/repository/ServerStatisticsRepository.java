package ru.devprizrakk.voidbot.database.repository;

import ru.devprizrakk.voidbot.database.DatabaseManager;
import ru.devprizrakk.voidbot.database.model.ServerStatisticsModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServerStatisticsRepository {

    private final DatabaseManager databaseManager;

    public ServerStatisticsRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public ServerStatisticsModel save(ServerStatisticsModel statistics) throws SQLException {
        if (update(statistics) == 0) {
            insert(statistics);
        }

        return statistics;
    }

    private int update(ServerStatisticsModel statistics) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     UPDATE server_statistics
                     SET member_count = ?, online_count = ?, boost_count = ?, channel_count = ?, role_count = ?
                     WHERE date = ?
                     """)) {
            stmt.setLong(1, statistics.getMemberCount());
            stmt.setLong(2, statistics.getOnlineCount());
            stmt.setLong(3, statistics.getBoostCount());
            stmt.setLong(4, statistics.getChannelCount());
            stmt.setLong(5, statistics.getRoleCount());
            stmt.setDate(6, statistics.getDate());

            return stmt.executeUpdate();
        }
    }

    private void insert(ServerStatisticsModel statistics) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     INSERT INTO server_statistics
                     (date, member_count, online_count, boost_count, channel_count, role_count)
                     VALUES (?, ?, ?, ?, ?, ?)
                     """)) {
            stmt.setDate(1, statistics.getDate());
            stmt.setLong(2, statistics.getMemberCount());
            stmt.setLong(3, statistics.getOnlineCount());
            stmt.setLong(4, statistics.getBoostCount());
            stmt.setLong(5, statistics.getChannelCount());
            stmt.setLong(6, statistics.getRoleCount());
            stmt.executeUpdate();
        }
    }

    public boolean deleteByDate(Date date) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM server_statistics WHERE date = ?")) {
            stmt.setDate(1, date);

            return stmt.executeUpdate() > 0;
        }
    }

    public List<ServerStatisticsModel> findAll() throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM server_statistics");
             ResultSet rs = stmt.executeQuery()) {

            List<ServerStatisticsModel> items = new ArrayList<>();
            while (rs.next()) items.add(map(rs));
            return items;
        }
    }

    public Optional<ServerStatisticsModel> findByDate(Date date) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM server_statistics WHERE date = ?")) {
            stmt.setDate(1, date);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    private ServerStatisticsModel map(ResultSet rs) throws SQLException {
        return new ServerStatisticsModel(
                rs.getDate("date"),
                rs.getLong("member_count"),
                rs.getLong("online_count"),
                rs.getLong("boost_count"),
                rs.getLong("channel_count"),
                rs.getLong("role_count")
        );
    }
}
