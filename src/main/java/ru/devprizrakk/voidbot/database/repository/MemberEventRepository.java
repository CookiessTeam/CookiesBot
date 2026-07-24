package ru.devprizrakk.voidbot.database.repository;

import ru.devprizrakk.voidbot.database.DatabaseManager;
import ru.devprizrakk.voidbot.database.model.MemberEventModel;
import ru.devprizrakk.voidbot.database.model.MemberEventType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberEventRepository extends JdbcRepository<MemberEventModel> {

    public MemberEventRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public MemberEventModel save(MemberEventModel event) throws SQLException {
        if (event.getId() > 0) {
            update(event);
            return event;
        }

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = prepareInsert(conn, """
                     INSERT INTO member_events (user_id, event_type, created_at)
                     VALUES (?, ?, ?)
                     """)) {
            stmt.setLong(1, event.getUserId());
            stmt.setString(2, event.getEventType().name());
            stmt.setTimestamp(3, event.getCreatedAt());
            stmt.executeUpdate();

            event.setId(generatedId(stmt));
            return event;
        }
    }

    public void update(MemberEventModel event) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     UPDATE member_events
                     SET user_id = ?, event_type = ?, created_at = ?
                     WHERE id = ?
                     """)) {
            stmt.setLong(1, event.getUserId());
            stmt.setString(2, event.getEventType().name());
            stmt.setTimestamp(3, event.getCreatedAt());
            stmt.setLong(4, event.getId());
            stmt.executeUpdate();
        }
    }

    public long countByType(MemberEventType type) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM member_events WHERE event_type = ?")) {
            stmt.setString(1, type.name());

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        }
    }

    public long countByTypeSince(MemberEventType type, java.sql.Timestamp since) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM member_events WHERE event_type = ? AND created_at >= ?")) {
            stmt.setString(1, type.name());
            stmt.setTimestamp(2, since);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        }
    }

    @Override
    protected String tableName() {
        return "member_events";
    }

    @Override
    protected MemberEventModel map(ResultSet rs) throws SQLException {
        return new MemberEventModel(
                rs.getLong("id"),
                rs.getLong("user_id"),
                MemberEventType.valueOf(rs.getString("event_type")),
                rs.getTimestamp("created_at")
        );
    }
}
