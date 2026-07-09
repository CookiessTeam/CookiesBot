package ru.devprizrakk.voidbot.database.repository;

import ru.devprizrakk.voidbot.database.DatabaseManager;
import ru.devprizrakk.voidbot.database.model.WarnModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WarnRepository extends JdbcRepository<WarnModel> {

    public WarnRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public WarnModel save(WarnModel warn) throws SQLException {
        if (warn.getId() > 0) {
            update(warn);
            return warn;
        }

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = prepareInsert(conn, """
                     INSERT INTO warns (user_id, moderator_id, reason, created_at, expired_at)
                     VALUES (?, ?, ?, ?, ?)
                     """)) {
            stmt.setLong(1, warn.getUserId());
            stmt.setLong(2, warn.getModeratorId());
            stmt.setString(3, warn.getReason());
            stmt.setTimestamp(4, warn.getCreatedAt());
            stmt.setTimestamp(5, warn.getExpiredAt());
            stmt.executeUpdate();

            warn.setId(generatedId(stmt));
            return warn;
        }
    }

    public void update(WarnModel warn) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     UPDATE warns
                     SET user_id = ?, moderator_id = ?, reason = ?, created_at = ?, expired_at = ?
                     WHERE id = ?
                     """)) {
            stmt.setLong(1, warn.getUserId());
            stmt.setLong(2, warn.getModeratorId());
            stmt.setString(3, warn.getReason());
            stmt.setTimestamp(4, warn.getCreatedAt());
            stmt.setTimestamp(5, warn.getExpiredAt());
            stmt.setLong(6, warn.getId());
            stmt.executeUpdate();
        }
    }

    public long countAll() throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM warns");
             ResultSet rs = stmt.executeQuery()) {

            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    @Override
    protected String tableName() {
        return "warns";
    }

    @Override
    protected WarnModel map(ResultSet rs) throws SQLException {
        return new WarnModel(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("moderator_id"),
                rs.getString("reason"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("expired_at")
        );
    }
}
