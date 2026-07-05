package ru.devprizrakk.voidbot.core.database.repository;

import ru.devprizrakk.voidbot.core.database.DatabaseManager;
import ru.devprizrakk.voidbot.core.database.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserRepository extends JdbcRepository<User> {
    public UserRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public User save(User user) throws SQLException {
        if (user.getId() > 0) {
            update(user);
            return user;
        }

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = prepareInsert(connection, """
                     INSERT INTO users (discord_id, username, joined_at, left_at, is_bot)
                     VALUES (?, ?, ?, ?, ?)
                     """)) {
            statement.setLong(1, user.getDiscordId());
            statement.setString(2, user.getUsername());
            statement.setTimestamp(3, user.getJoinedAt());
            statement.setTimestamp(4, user.getLeftAt());
            statement.setBoolean(5, user.isBot());
            statement.executeUpdate();
            long id = generatedId(statement);
            if (id <= 0) {
                Optional<User> justCreated = findByDiscordId(user.getDiscordId());
                if (justCreated.isPresent()) {
                    id = justCreated.get().getId();
                }
            }
            user.setId(id);
            return user;
        }
    }

    public void update(User user) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE users
                     SET discord_id = ?, username = ?, joined_at = ?, left_at = ?, is_bot = ?
                     WHERE id = ?
                     """)) {
            statement.setLong(1, user.getDiscordId());
            statement.setString(2, user.getUsername());
            statement.setTimestamp(3, user.getJoinedAt());
            statement.setTimestamp(4, user.getLeftAt());
            statement.setBoolean(5, user.isBot());
            statement.setLong(6, user.getId());
            statement.executeUpdate();
        }
    }

    public Optional<User> findByDiscordId(long discordId) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE discord_id = ?")) {
            statement.setLong(1, discordId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    public void markLeft(long userId, java.sql.Timestamp leftAt) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE users SET left_at = ? WHERE id = ?")) {
            statement.setTimestamp(1, leftAt);
            statement.setLong(2, userId);
            statement.executeUpdate();
        }
    }

    @Override
    protected String tableName() {
        return "users";
    }

    @Override
    protected User map(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getLong("id"),
                resultSet.getLong("discord_id"),
                resultSet.getString("username"),
                resultSet.getTimestamp("joined_at"),
                resultSet.getTimestamp("left_at"),
                resultSet.getBoolean("is_bot")
        );
    }
}
