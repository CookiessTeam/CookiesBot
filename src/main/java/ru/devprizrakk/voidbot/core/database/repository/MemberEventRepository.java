package ru.devprizrakk.voidbot.core.database.repository;

import ru.devprizrakk.voidbot.core.database.DatabaseManager;
import ru.devprizrakk.voidbot.core.database.model.MemberEvent;
import ru.devprizrakk.voidbot.core.database.model.MemberEventType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberEventRepository extends JdbcRepository<MemberEvent> {
    public MemberEventRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public MemberEvent save(MemberEvent event) throws SQLException {
        if (event.getId() > 0) {
            update(event);
            return event;
        }

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = prepareInsert(connection, """
                     INSERT INTO member_events (user_id, event_type, created_at)
                     VALUES (?, ?, ?)
                     """)) {
            statement.setLong(1, event.getUserId());
            statement.setString(2, event.getEventType().name());
            statement.setTimestamp(3, event.getCreatedAt());
            statement.executeUpdate();
            try {
                event.setId(generatedId(statement));
            } catch (SQLException ignored) {
                event.setId(0);
            }
            return event;
        }
    }

    public void update(MemberEvent event) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE member_events
                     SET user_id = ?, event_type = ?, created_at = ?
                     WHERE id = ?
                     """)) {
            statement.setLong(1, event.getUserId());
            statement.setString(2, event.getEventType().name());
            statement.setTimestamp(3, event.getCreatedAt());
            statement.setLong(4, event.getId());
            statement.executeUpdate();
        }
    }

    public long countByType(MemberEventType type) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM member_events WHERE event_type = ?")) {
            statement.setString(1, type.name());
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        }
    }

    public long countByTypeSince(MemberEventType type, java.sql.Timestamp since) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM member_events WHERE event_type = ? AND created_at >= ?")) {
            statement.setString(1, type.name());
            statement.setTimestamp(2, since);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        }
    }

    @Override
    protected String tableName() {
        return "member_events";
    }

    @Override
    protected MemberEvent map(ResultSet resultSet) throws SQLException {
        return new MemberEvent(
                resultSet.getLong("id"),
                resultSet.getLong("user_id"),
                MemberEventType.valueOf(resultSet.getString("event_type")),
                resultSet.getTimestamp("created_at")
        );
    }
}
