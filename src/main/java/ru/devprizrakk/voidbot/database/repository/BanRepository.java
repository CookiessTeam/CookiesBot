package ru.devprizrakk.voidbot.database.repository;

import ru.devprizrakk.voidbot.database.DatabaseManager;
import ru.devprizrakk.voidbot.database.model.BanModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BanRepository extends JdbcRepository<BanModel> {

    public BanRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public BanModel save(BanModel ban) throws SQLException {
        if (ban.getId() > 0) {
            update(ban);
            return ban;
        }

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = prepareInsert(conn, """
                     INSERT INTO bans (user_id, moderation_id, description, created_at, expired_at)
                     VALUES (?, ?, ?, ?, ?)
                     """)) {
            stmt.setLong(1, ban.getUserId());
            stmt.setLong(2, ban.getModeratorId());
            stmt.setString(3, ban.getDescription());
            stmt.setTimestamp(4, toTs(ban.getCreatedAt()));
            stmt.setTimestamp(5, toTs(ban.getExpiredAt()));
            stmt.executeUpdate();

            ban.setId(generatedId(stmt));
            return ban;
        }
    }

    public void update(BanModel ban) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     UPDATE bans
                     SET user_id = ?, moderation_id = ?, description = ?, created_at = ?, expired_at = ?
                     WHERE id = ?
                     """)) {
            stmt.setLong(1, ban.getUserId());
            stmt.setLong(2, ban.getModeratorId());
            stmt.setString(3, ban.getDescription());
            stmt.setTimestamp(4, toTs(ban.getCreatedAt()));
            stmt.setTimestamp(5, toTs(ban.getExpiredAt()));
            stmt.setLong(6, ban.getId());
            stmt.executeUpdate();
        }
    }

    public List<BanModel> findActiveByUser(long userId) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bans WHERE user_id = ? AND (expired_at IS NULL OR expired_at > CURRENT_TIMESTAMP)")) {
            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                List<BanModel> items = new ArrayList<>();
                while (rs.next()) items.add(map(rs));
                return items;
            }
        }
    }

    public long countAll() throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM bans");
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    public long countActive() throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM bans WHERE expired_at IS NULL OR expired_at > CURRENT_TIMESTAMP");
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    @Override
    protected String tableName() {
        return "bans";
    }

    @Override
    protected BanModel map(ResultSet rs) throws SQLException {
        return new BanModel(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("moderation_id"),
                rs.getString("description"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("expired_at")
        );
    }
}