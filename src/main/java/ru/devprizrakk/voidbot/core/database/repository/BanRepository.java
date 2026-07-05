package ru.devprizrakk.voidbot.core.database.repository;

import ru.devprizrakk.voidbot.core.database.DatabaseManager;
import ru.devprizrakk.voidbot.core.database.model.Ban;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BanRepository extends JdbcRepository<Ban> {
    public BanRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public Ban save(Ban ban) throws SQLException {
        if (ban.getId() > 0) {
            update(ban);
            return ban;
        }
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = prepareInsert(connection, """
                     INSERT INTO bans (user_id, moderation_id, description, created_at, expired_at)
                     VALUES (?, ?, ?, ?, ?)
                     """)) {
            statement.setLong(1, ban.getUserId());
            statement.setLong(2, ban.getModeratorId());
            statement.setString(3, ban.getDescription());
            statement.setTimestamp(4, toTs(ban.getCreatedAt()));
            statement.setTimestamp(5, toTs(ban.getExpiredAt()));
            statement.executeUpdate();
            try {
                ban.setId(generatedId(statement));
            } catch (SQLException ignored) {
            }
            return ban;
        }
    }

    public void update(Ban ban) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE bans
                     SET user_id = ?, moderation_id = ?, description = ?, created_at = ?, expired_at = ?
                     WHERE id = ?
                     """)) {
            statement.setLong(1, ban.getUserId());
            statement.setLong(2, ban.getModeratorId());
            statement.setString(3, ban.getDescription());
            statement.setTimestamp(4, toTs(ban.getCreatedAt()));
            statement.setTimestamp(5, toTs(ban.getExpiredAt()));
            statement.setLong(6, ban.getId());
            statement.executeUpdate();
        }
    }

    public List<Ban> findActiveByUser(long userId) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM bans WHERE user_id = ? AND (expired_at IS NULL OR expired_at > CURRENT_TIMESTAMP)"
             )) {
            statement.setLong(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                List<Ban> items = new ArrayList<>();
                while (rs.next()) items.add(map(rs));
                return items;
            }
        }
    }

    public long countAll() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM bans");
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    public long countActive() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM bans WHERE expired_at IS NULL OR expired_at > CURRENT_TIMESTAMP");
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    @Override
    protected String tableName() {
        return "bans";
    }

    @Override
    protected Ban map(ResultSet resultSet) throws SQLException {
        return new Ban(
                resultSet.getLong("id"),
                resultSet.getLong("user_id"),
                resultSet.getLong("moderation_id"),
                resultSet.getString("description"),
                resultSet.getTimestamp("created_at"),
                resultSet.getTimestamp("expired_at")
        );
    }

    private static java.sql.Timestamp toTs(java.util.Date date) {
        return date == null ? null : new java.sql.Timestamp(date.getTime());
    }
}