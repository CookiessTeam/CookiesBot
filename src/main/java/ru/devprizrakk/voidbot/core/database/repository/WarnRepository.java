package ru.devprizrakk.voidbot.core.database.repository;

import ru.devprizrakk.voidbot.core.database.DatabaseManager;
import ru.devprizrakk.voidbot.core.database.model.Warn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WarnRepository extends JdbcRepository<Warn> {
    public WarnRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public Warn save(Warn warn) throws SQLException {
        if (warn.getId() > 0) {
            update(warn);
            return warn;
        }

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = prepareInsert(connection, """
                     INSERT INTO warns (user_id, moderator_id, reason, created_at, expired_at)
                     VALUES (?, ?, ?, ?, ?)
                     """)) {
            statement.setLong(1, warn.getUserId());
            statement.setLong(2, warn.getModeratorId());
            statement.setString(3, warn.getReason());
            statement.setTimestamp(4, warn.getCreatedAt());
            statement.setTimestamp(5, warn.getExpiredAt());
            statement.executeUpdate();
            try {
                warn.setId(generatedId(statement));
            } catch (SQLException ignored) {
                warn.setId(0);
            }
            return warn;
        }
    }

    public void update(Warn warn) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE warns
                     SET user_id = ?, moderator_id = ?, reason = ?, created_at = ?, expired_at = ?
                     WHERE id = ?
                     """)) {
            statement.setLong(1, warn.getUserId());
            statement.setLong(2, warn.getModeratorId());
            statement.setString(3, warn.getReason());
            statement.setTimestamp(4, warn.getCreatedAt());
            statement.setTimestamp(5, warn.getExpiredAt());
            statement.setLong(6, warn.getId());
            statement.executeUpdate();
        }
    }

    public long countAll() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM warns");
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    @Override
    protected String tableName() {
        return "warns";
    }

    @Override
    protected Warn map(ResultSet resultSet) throws SQLException {
        return new Warn(
                resultSet.getLong("id"),
                resultSet.getLong("user_id"),
                resultSet.getLong("moderator_id"),
                resultSet.getString("reason"),
                resultSet.getTimestamp("created_at"),
                resultSet.getTimestamp("expired_at")
        );
    }
}
