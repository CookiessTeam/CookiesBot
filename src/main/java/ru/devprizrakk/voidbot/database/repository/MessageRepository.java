package ru.devprizrakk.voidbot.database.repository;

import ru.devprizrakk.voidbot.database.DatabaseManager;
import ru.devprizrakk.voidbot.database.model.MessageRecordModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class MessageRepository extends JdbcRepository<MessageRecordModel> {

    public MessageRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public MessageRecordModel save(MessageRecordModel message) throws SQLException {
        if (message.getId() > 0) {
            update(message);
            return message;
        }

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = prepareInsert(conn, """
                     INSERT INTO messages (message_id, user_id, channel_id, created_at, deleted_at)
                     VALUES (?, ?, ?, ?, ?)
                     """)) {
            stmt.setLong(1, message.getMessageId());
            stmt.setLong(2, message.getUserId());
            stmt.setLong(3, message.getChannelId());
            stmt.setTimestamp(4, message.getCreatedAt());
            stmt.setTimestamp(5, message.getDeletedAt());
            stmt.executeUpdate();

            long id = generatedId(stmt);
            if (id <= 0) {
                Optional<MessageRecordModel> justCreated = findByMessageId(message.getMessageId());
                if (justCreated.isPresent()) id = justCreated.get().getId();
            }

            message.setId(id);
            return message;
        }
    }

    public void update(MessageRecordModel message) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     UPDATE messages
                     SET message_id = ?, user_id = ?, channel_id = ?, created_at = ?, deleted_at = ?
                     WHERE id = ?
                     """)) {
            stmt.setLong(1, message.getMessageId());
            stmt.setLong(2, message.getUserId());
            stmt.setLong(3, message.getChannelId());
            stmt.setTimestamp(4, message.getCreatedAt());
            stmt.setTimestamp(5, message.getDeletedAt());
            stmt.setLong(6, message.getId());
            stmt.executeUpdate();
        }
    }

    public Optional<MessageRecordModel> findByMessageId(long messageId) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM messages WHERE message_id = ?")) {
            stmt.setLong(1, messageId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public void markDeleted(long messageId, java.sql.Timestamp deletedAt) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE messages SET deleted_at = ? WHERE message_id = ?")) {
            stmt.setTimestamp(1, deletedAt);
            stmt.setLong(2, messageId);
            stmt.executeUpdate();
        }
    }

    public long countAll() throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM messages");
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    public long countSince(java.sql.Timestamp since) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM messages WHERE created_at >= ?")) {
            stmt.setTimestamp(1, since);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        }
    }

    public long countDeleted() throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM messages WHERE deleted_at IS NOT NULL");
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    @Override
    protected String tableName() {
        return "messages";
    }

    @Override
    protected MessageRecordModel map(ResultSet rs) throws SQLException {
        return new MessageRecordModel(
                rs.getLong("id"),
                rs.getLong("message_id"),
                rs.getLong("user_id"),
                rs.getLong("channel_id"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("deleted_at")
        );
    }
}
