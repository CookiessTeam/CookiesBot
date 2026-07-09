package ru.devprizrakk.voidbot.database.repository;

import ru.devprizrakk.voidbot.database.DatabaseManager;
import ru.devprizrakk.voidbot.database.model.MuteModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MuteRepository extends JdbcRepository<MuteModel> {

    public MuteRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public MuteModel save(MuteModel mute) throws SQLException {
        if (mute.getId() > 0) {
            update(mute);
            return mute;
        }

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = prepareInsert(conn, """
                     INSERT INTO mutes (user_id, moderation_id, description, created_at, expired_at)
                     VALUES (?, ?, ?, ?, ?)
                     """)) {
            stmt.setLong(1, mute.getUserId());
            stmt.setLong(2, mute.getModeratorId());
            stmt.setString(3, mute.getDescription());
            stmt.setTimestamp(4, toTs(mute.getCreatedAt()));
            stmt.setTimestamp(5, toTs(mute.getExpiredAt()));
            stmt.executeUpdate();

            mute.setId(generatedId(stmt));
            return mute;
        }
    }

    public MuteModel update(MuteModel mute) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     UPDATE mutes
                     SET user_id = ?, moderation_id = ?, description = ?, created_at = ?, expired_at = ?
                     WHERE id = ?
                     """)) {
            stmt.setLong(1, mute.getUserId());
            stmt.setLong(2, mute.getModeratorId());
            stmt.setString(3, mute.getDescription());
            stmt.setTimestamp(4, toTs(mute.getCreatedAt()));
            stmt.setTimestamp(5, toTs(mute.getExpiredAt()));
            stmt.setLong(6, mute.getId());
            stmt.executeUpdate();
        }
        return mute;
    }

    public List<MuteModel> findActiveByUser(long userId) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM mutes WHERE user_id = ? AND (expired_at IS NULL OR expired_at > CURRENT_TIMESTAMP)")) {
            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                List<MuteModel> items = new ArrayList<>();
                while (rs.next()) items.add(map(rs));
                return items;
            }
        }
    }

    public long countAll() throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM mutes");
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    public long countActive() throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM mutes WHERE expired_at IS NULL OR expired_at > CURRENT_TIMESTAMP");
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    @Override
    protected String tableName() {
        return "mutes";
    }

    @Override
    protected MuteModel map(ResultSet rs) throws SQLException {
        return new MuteModel(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("moderation_id"),
                rs.getString("description"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("expired_at")
        );
    }
}