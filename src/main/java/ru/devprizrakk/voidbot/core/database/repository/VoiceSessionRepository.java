package ru.devprizrakk.voidbot.core.database.repository;

import ru.devprizrakk.voidbot.core.database.DatabaseManager;
import ru.devprizrakk.voidbot.core.database.model.VoiceSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VoiceSessionRepository extends JdbcRepository<VoiceSession> {
    public VoiceSessionRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public VoiceSession save(VoiceSession session) throws SQLException {
        if (session.getId() > 0) {
            update(session);
            return session;
        }

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = prepareInsert(connection, """
                     INSERT INTO voice_sessions (user_id, channel_id, joined_at, left_at, duration_seconds)
                     VALUES (?, ?, ?, ?, ?)
                     """)) {
            statement.setLong(1, session.getUserId());
            statement.setLong(2, session.getChannelId());
            statement.setTimestamp(3, session.getJoinedAt());
            statement.setTimestamp(4, session.getLeftAt());
            statement.setLong(5, session.getDurationSeconds());
            statement.executeUpdate();
            long id = 0;
            try {
                id = generatedId(statement);
            } catch (SQLException ignored) {
                // SQLite JDBC не реализует getGeneratedKeys — ищем только что вставленную строку
            }
            if (id <= 0) {
                Optional<VoiceSession> justCreated = findOpenByUser(session.getUserId());
                if (justCreated.isPresent()) {
                    id = justCreated.get().getId();
                }
            }
            session.setId(id);
            return session;
        }
    }

    public Optional<VoiceSession> findOpenByUser(long userId) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT * FROM voice_sessions
                     WHERE user_id = ? AND left_at IS NULL
                     ORDER BY id DESC
                     LIMIT 1
                     """)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    public List<VoiceSession> findAllOpen() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM voice_sessions WHERE left_at IS NULL"
             );
             ResultSet resultSet = statement.executeQuery()) {
            List<VoiceSession> items = new ArrayList<>();
            while (resultSet.next()) {
                items.add(map(resultSet));
            }
            return items;
        }
    }

    public int closeOrphanedSessions(Timestamp closeAt) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE voice_sessions
                     SET left_at = ?, duration_seconds = 0
                     WHERE left_at IS NULL
                     """)) {
            statement.setTimestamp(1, closeAt);
            return statement.executeUpdate();
        }
    }

    public void update(VoiceSession session) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE voice_sessions
                     SET user_id = ?, channel_id = ?, joined_at = ?, left_at = ?, duration_seconds = ?
                     WHERE id = ?
                     """)) {
            statement.setLong(1, session.getUserId());
            statement.setLong(2, session.getChannelId());
            statement.setTimestamp(3, session.getJoinedAt());
            statement.setTimestamp(4, session.getLeftAt());
            statement.setLong(5, session.getDurationSeconds());
            statement.setLong(6, session.getId());
            statement.executeUpdate();
        }
    }

public void closeSession(long id, java.sql.Timestamp leftAt, long durationSeconds) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                    UPDATE voice_sessions
                    SET left_at = ?, duration_seconds = ?
                    WHERE id = ?
                    """)) {
            statement.setTimestamp(1, leftAt);
            statement.setLong(2, durationSeconds);
            statement.setLong(3, id);
            statement.executeUpdate();
        }
    }

    public long sumDurationSecondsByUser(long userId) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COALESCE(SUM(duration_seconds), 0) FROM voice_sessions WHERE user_id = ?"
             )) {
            statement.setLong(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0L;
            }
        }
    }

    public long sumAllDurationSeconds() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COALESCE(SUM(duration_seconds), 0) FROM voice_sessions"
             );
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    public long countAllSessions() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM voice_sessions");
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    @Override
    protected String tableName() {
        return "voice_sessions";
    }

    @Override
    protected VoiceSession map(ResultSet resultSet) throws SQLException {
        return new VoiceSession(
                resultSet.getLong("id"),
                resultSet.getLong("user_id"),
                resultSet.getLong("channel_id"),
                resultSet.getTimestamp("joined_at"),
                resultSet.getTimestamp("left_at"),
                resultSet.getLong("duration_seconds")
        );
    }
}
