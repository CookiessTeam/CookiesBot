package ru.devprizrakk.voidbot.core.database.repository;

import ru.devprizrakk.voidbot.core.database.DatabaseManager;
import ru.devprizrakk.voidbot.core.database.model.MessageRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class MessageRepository extends JdbcRepository<MessageRecord> {
    public MessageRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public MessageRecord save(MessageRecord message) throws SQLException {
        if (message.getId() > 0) {
            update(message);
            return message;
        }

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = prepareInsert(connection, """
                     INSERT INTO messages (message_id, user_id, channel_id, created_at, deleted_at)
                     VALUES (?, ?, ?, ?, ?)
                     """)) {
            statement.setLong(1, message.getMessageId());
            statement.setLong(2, message.getUserId());
            statement.setLong(3, message.getChannelId());
            statement.setTimestamp(4, message.getCreatedAt());
            statement.setTimestamp(5, message.getDeletedAt());
            statement.executeUpdate();
            long id = generatedId(statement);
            if (id <= 0) {
                Optional<MessageRecord> justCreated = findByMessageId(message.getMessageId());
                if (justCreated.isPresent()) {
                    id = justCreated.get().getId();
                }
            }
            message.setId(id);
            return message;
        }
    }

    public void update(MessageRecord message) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE messages
                     SET message_id = ?, user_id = ?, channel_id = ?, created_at = ?, deleted_at = ?
                     WHERE id = ?
                     """)) {
            statement.setLong(1, message.getMessageId());
            statement.setLong(2, message.getUserId());
            statement.setLong(3, message.getChannelId());
            statement.setTimestamp(4, message.getCreatedAt());
            statement.setTimestamp(5, message.getDeletedAt());
            statement.setLong(6, message.getId());
            statement.executeUpdate();
        }
    }

    public Optional<MessageRecord> findByMessageId(long messageId) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages WHERE message_id = ?")) {
            statement.setLong(1, messageId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    public void markDeleted(long messageId, java.sql.Timestamp deletedAt) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE messages SET deleted_at = ? WHERE message_id = ?")) {
            statement.setTimestamp(1, deletedAt);
            statement.setLong(2, messageId);
            statement.executeUpdate();
        }
    }

    public long countAll() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM messages");
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    public long countSince(java.sql.Timestamp since) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM messages WHERE created_at >= ?")) {
            statement.setTimestamp(1, since);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        }
    }

    public long countDeleted() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM messages WHERE deleted_at IS NOT NULL");
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    @Override
    protected String tableName() {
        return "messages";
    }

    @Override
    protected MessageRecord map(ResultSet resultSet) throws SQLException {
        return new MessageRecord(
                resultSet.getLong("id"),
                resultSet.getLong("message_id"),
                resultSet.getLong("user_id"),
                resultSet.getLong("channel_id"),
                resultSet.getTimestamp("created_at"),
                resultSet.getTimestamp("deleted_at")
        );
    }
}
