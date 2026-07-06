package ru.devprizrakk.voidbot.core.database.repository;

import ru.devprizrakk.voidbot.core.database.DatabaseManager;
import ru.devprizrakk.voidbot.core.database.model.ServerStatistics;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServerStatisticsRepository {
    private final DatabaseManager databaseManager;

    public ServerStatisticsRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public ServerStatistics save(ServerStatistics statistics) throws SQLException {
        if (update(statistics) == 0) {
            insert(statistics);
        }
        return statistics;
    }

    public Optional<ServerStatistics> findByDate(Date date) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM server_statistics WHERE date = ?")) {
            statement.setDate(1, date);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    public List<ServerStatistics> findAll() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM server_statistics");
             ResultSet resultSet = statement.executeQuery()) {
            List<ServerStatistics> items = new ArrayList<>();
            while (resultSet.next()) {
                items.add(map(resultSet));
            }
            return items;
        }
    }

    public boolean deleteByDate(Date date) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM server_statistics WHERE date = ?")) {
            statement.setDate(1, date);
            return statement.executeUpdate() > 0;
        }
    }

    private void insert(ServerStatistics statistics) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     INSERT INTO server_statistics
                     (date, member_count, online_count, boost_count, channel_count, role_count)
                     VALUES (?, ?, ?, ?, ?, ?)
                     """)) {
            statement.setDate(1, statistics.getDate());
            statement.setLong(2, statistics.getMemberCount());
            statement.setLong(3, statistics.getOnlineCount());
            statement.setLong(4, statistics.getBoostCount());
            statement.setLong(5, statistics.getChannelCount());
            statement.setLong(6, statistics.getRoleCount());
            statement.executeUpdate();
        }
    }

    private int update(ServerStatistics statistics) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE server_statistics
                     SET member_count = ?, online_count = ?, boost_count = ?, channel_count = ?, role_count = ?
                     WHERE date = ?
                     """)) {
            statement.setLong(1, statistics.getMemberCount());
            statement.setLong(2, statistics.getOnlineCount());
            statement.setLong(3, statistics.getBoostCount());
            statement.setLong(4, statistics.getChannelCount());
            statement.setLong(5, statistics.getRoleCount());
            statement.setDate(6, statistics.getDate());
            return statement.executeUpdate();
        }
    }

    private ServerStatistics map(ResultSet resultSet) throws SQLException {
        return new ServerStatistics(
                resultSet.getDate("date"),
                resultSet.getLong("member_count"),
                resultSet.getLong("online_count"),
                resultSet.getLong("boost_count"),
                resultSet.getLong("channel_count"),
                resultSet.getLong("role_count")
        );
    }
}
