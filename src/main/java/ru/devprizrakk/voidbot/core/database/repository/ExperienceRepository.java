package ru.devprizrakk.voidbot.core.database.repository;

import ru.devprizrakk.voidbot.core.database.DatabaseManager;
import ru.devprizrakk.voidbot.core.database.model.Experience;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExperienceRepository extends JdbcRepository<Experience> {
    public ExperienceRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public Optional<Experience> findByDiscordIdAndGuild(long discordId, long guildId) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM experience WHERE discord_id = ? AND guild_id = ?")) {
            statement.setLong(1, discordId);
            statement.setLong(2, guildId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    public Experience save(Experience experience) throws SQLException {
        if (experience.getId() > 0) {
            update(experience);
            return experience;
        }

        Optional<Experience> existing = findByDiscordIdAndGuild(experience.getDiscordId(), experience.getGuildId());
        if (existing.isPresent()) {
            experience.setId(existing.get().getId());
            update(experience);
            return experience;
        }

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = prepareInsert(connection, """
                     INSERT INTO experience (discord_id, guild_id, level, experience, total_experience, updated_at)
                     VALUES (?, ?, ?, ?, ?, ?)
                     """)) {
            statement.setLong(1, experience.getDiscordId());
            statement.setLong(2, experience.getGuildId());
            statement.setLong(3, experience.getLevel());
            statement.setLong(4, experience.getExperience());
            statement.setLong(5, experience.getTotalExperience());
            statement.setTimestamp(6, experience.getUpdatedAt());
            statement.executeUpdate();
            try {
                experience.setId(generatedId(statement));
            } catch (SQLException ignored) {
                experience.setId(0);
            }
            return experience;
        }
    }

    private void update(Experience experience) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE experience
                     SET level = ?, experience = ?, total_experience = ?, updated_at = ?
                     WHERE id = ?
                     """)) {
            statement.setLong(1, experience.getLevel());
            statement.setLong(2, experience.getExperience());
            statement.setLong(3, experience.getTotalExperience());
            statement.setTimestamp(4, experience.getUpdatedAt());
            statement.setLong(5, experience.getId());
            statement.executeUpdate();
        }
    }

    public List<Experience> topByGuild(long guildId, int limit) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM experience WHERE guild_id = ? ORDER BY total_experience DESC LIMIT ?")) {
            statement.setLong(1, guildId);
            statement.setInt(2, Math.max(1, Math.min(limit, 25)));
            try (ResultSet rs = statement.executeQuery()) {
                List<Experience> items = new ArrayList<>();
                while (rs.next()) {
                    items.add(map(rs));
                }
                return items;
            }
        }
    }

    @Override
    protected String tableName() {
        return "experience";
    }

    @Override
    protected Experience map(ResultSet resultSet) throws SQLException {
        return new Experience(
                resultSet.getLong("id"),
                resultSet.getLong("discord_id"),
                resultSet.getLong("guild_id"),
                resultSet.getLong("level"),
                resultSet.getLong("experience"),
                resultSet.getLong("total_experience"),
                resultSet.getTimestamp("updated_at")
        );
    }
}