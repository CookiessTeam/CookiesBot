package ru.devprizrakk.voidbot.database.repository;

import ru.devprizrakk.voidbot.database.DatabaseManager;
import ru.devprizrakk.voidbot.database.model.VoiceSessionModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VoiceSessionRepository extends JdbcRepository<VoiceSessionModel> {

    public VoiceSessionRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public VoiceSessionModel save(VoiceSessionModel session) throws SQLException {
        if (session.getId() > 0) {
            update(session);
            return session;
        }

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = prepareInsert(conn, """
                     INSERT INTO voice_sessions (user_id, channel_id, joined_at, left_at, duration_seconds)
                     VALUES (?, ?, ?, ?, ?)
                     """)) {
            stmt.setLong(1, session.getUserId());
            stmt.setLong(2, session.getChannelId());
            stmt.setTimestamp(3, session.getJoinedAt());
            stmt.setTimestamp(4, session.getLeftAt());
            stmt.setLong(5, session.getDurationSeconds());
            stmt.executeUpdate();

            long id = generatedId(stmt);
            if (id <= 0) {
                Optional<VoiceSessionModel> justCreated = findOpenByUser(session.getUserId());
                if (justCreated.isPresent()) id = justCreated.get().getId();
            }

            session.setId(id);
            return session;
        }
    }

    public Optional<VoiceSessionModel> findOpenByUser(long userId) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     SELECT * FROM voice_sessions
                     WHERE user_id = ? AND left_at IS NULL
                     ORDER BY id DESC
                     LIMIT 1
                     """)) {
            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public List<VoiceSessionModel> findAllOpen() throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM voice_sessions WHERE left_at IS NULL");
             ResultSet rs = stmt.executeQuery()) {

            List<VoiceSessionModel> items = new ArrayList<>();
            while (rs.next()) items.add(map(rs));
            return items;
        }
    }

    public int closeOrphanedSessions(Timestamp closeAt) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     UPDATE voice_sessions
                     SET left_at = ?, duration_seconds = 0
                     WHERE left_at IS NULL
                     """)) {
            stmt.setTimestamp(1, closeAt);

            return stmt.executeUpdate();
        }
    }

    public void update(VoiceSessionModel session) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     UPDATE voice_sessions
                     SET user_id = ?, channel_id = ?, joined_at = ?, left_at = ?, duration_seconds = ?
                     WHERE id = ?
                     """)) {
            stmt.setLong(1, session.getUserId());
            stmt.setLong(2, session.getChannelId());
            stmt.setTimestamp(3, session.getJoinedAt());
            stmt.setTimestamp(4, session.getLeftAt());
            stmt.setLong(5, session.getDurationSeconds());
            stmt.setLong(6, session.getId());
            stmt.executeUpdate();
        }
    }

    public void closeSession(long id, Timestamp leftAt, long durationSeconds) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     UPDATE voice_sessions
                     SET left_at = ?, duration_seconds = ?
                     WHERE id = ?
                     """)) {
            stmt.setTimestamp(1, leftAt);
            stmt.setLong(2, durationSeconds);
            stmt.setLong(3, id);
            stmt.executeUpdate();
        }
    }

    public long sumDurationSecondsByUser(long userId) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COALESCE(SUM(duration_seconds), 0) FROM voice_sessions WHERE user_id = ?")) {
            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        }
    }

    public long sumAllDurationSeconds() throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COALESCE(SUM(duration_seconds), 0) FROM voice_sessions");
             ResultSet rs = stmt.executeQuery()) {

            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    public long countAllSessions() throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM voice_sessions");
             ResultSet rs = stmt.executeQuery()) {

            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    @Override
    protected String tableName() {
        return "voice_sessions";
    }

    @Override
    protected VoiceSessionModel map(ResultSet rs) throws SQLException {
        return new VoiceSessionModel(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("channel_id"),
                rs.getTimestamp("joined_at"),
                rs.getTimestamp("left_at"),
                rs.getLong("duration_seconds")
        );
    }
}
