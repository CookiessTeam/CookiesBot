package ru.devprizrakk.voidbot.core.database.repository;

import ru.devprizrakk.voidbot.core.database.DatabaseManager;
import ru.devprizrakk.voidbot.core.database.model.Mute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MuteRepository extends JdbcRepository<Mute> {
    public MuteRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public Mute save(Mute mute) throws SQLException {
        if (mute.getId() > 0) {
            update(mute);
            return mute;
        }
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = prepareInsert(connection, """
                     INSERT INTO mutes (user_id, moderation_id, description, created_at, expired_at)
                     VALUES (?, ?, ?, ?, ?)
                     """)) {
            statement.setLong(1, mute.getUserId());
            statement.setLong(2, mute.getModeratorId());
            statement.setString(3, mute.getDescription());
            statement.setTimestamp(4, toTs(mute.getCreatedAt()));
            statement.setTimestamp(5, toTs(mute.getExpiredAt()));
            statement.executeUpdate();
            try {
                mute.setId(generatedId(statement));
            } catch (SQLException ignored) {
            }
            return mute;
        }
    }

    public Mute update(Mute mute) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE mutes
                     SET user_id = ?, moderation_id = ?, description = ?, created_at = ?, expired_at = ?
                     WHERE id = ?
                     """)) {
            statement.setLong(1, mute.getUserId());
            statement.setLong(2, mute.getModeratorId());
            statement.setString(3, mute.getDescription());
            statement.setTimestamp(4, toTs(mute.getCreatedAt()));
            statement.setTimestamp(5, toTs(mute.getExpiredAt()));
            statement.setLong(6, mute.getId());
            statement.executeUpdate();
        }
        return mute;
    }

    public List<Mute> findActiveByUser(long userId) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM mutes WHERE user_id = ? AND (expired_at IS NULL OR expired_at > CURRENT_TIMESTAMP)"
             )) {
            statement.setLong(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                List<Mute> items = new ArrayList<>();
                while (rs.next()) items.add(map(rs));
                return items;
            }
        }
    }

    public long countAll() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM mutes");
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    public long countActive() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM mutes WHERE expired_at IS NULL OR expired_at > CURRENT_TIMESTAMP");
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    @Override
    protected String tableName() {
        return "mutes";
    }

    @Override
    protected Mute map(ResultSet rs) throws SQLException {
        return new Mute(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("moderation_id"),
                rs.getString("description"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("expired_at")
        );
    }

    private static java.sql.Timestamp toTs(java.util.Date date) {
        return date == null ? null : new java.sql.Timestamp(date.getTime());
    }
}