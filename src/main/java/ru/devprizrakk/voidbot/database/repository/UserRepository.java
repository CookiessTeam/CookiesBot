package ru.devprizrakk.voidbot.database.repository;

import ru.devprizrakk.voidbot.database.DatabaseManager;
import ru.devprizrakk.voidbot.database.model.UserModel;

import java.sql.*;
import java.util.Optional;

public class UserRepository extends JdbcRepository<UserModel> {

    public UserRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public UserModel save(UserModel user) throws SQLException {
        if (user.getId() > 0) {
            update(user);
            return user;
        }

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = prepareInsert(conn, """
                     INSERT INTO users (discord_id, username, joined_at, left_at, is_bot)
                     VALUES (?, ?, ?, ?, ?)
                     """)) {
            stmt.setLong(1, user.getDiscordId());
            stmt.setString(2, user.getUsername());
            stmt.setTimestamp(3, user.getJoinedAt());
            stmt.setTimestamp(4, user.getLeftAt());
            stmt.setBoolean(5, user.isBot());
            stmt.executeUpdate();

            long id = generatedId(stmt);
            if (id <= 0) {
                Optional<UserModel> justCreated = findByDiscordId(user.getDiscordId());
                if (justCreated.isPresent()) id = justCreated.get().getId();
            }

            user.setId(id);
            return user;
        }
    }

    public void update(UserModel user) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     UPDATE users
                     SET discord_id = ?, username = ?, joined_at = ?, left_at = ?, is_bot = ?
                     WHERE id = ?
                     """)) {
            stmt.setLong(1, user.getDiscordId());
            stmt.setString(2, user.getUsername());
            stmt.setTimestamp(3, user.getJoinedAt());
            stmt.setTimestamp(4, user.getLeftAt());
            stmt.setBoolean(5, user.isBot());
            stmt.setLong(6, user.getId());
            stmt.executeUpdate();
        }
    }

    public Optional<UserModel> findByDiscordId(long discordId) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE discord_id = ?")) {
            stmt.setLong(1, discordId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public void markLeft(long userId, Timestamp leftAt) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE users SET left_at = ? WHERE id = ?")) {
            stmt.setTimestamp(1, leftAt);
            stmt.setLong(2, userId);
            stmt.executeUpdate();
        }
    }

    @Override
    protected String tableName() {
        return "users";
    }

    @Override
    protected UserModel map(ResultSet rs) throws SQLException {
        return new UserModel(
                rs.getLong("id"),
                rs.getLong("discord_id"),
                rs.getString("username"),
                rs.getTimestamp("joined_at"),
                rs.getTimestamp("left_at"),
                rs.getBoolean("is_bot")
        );
    }
}
