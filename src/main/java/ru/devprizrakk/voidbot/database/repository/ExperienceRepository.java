package ru.devprizrakk.voidbot.database.repository;

import ru.devprizrakk.voidbot.database.DatabaseManager;
import ru.devprizrakk.voidbot.database.model.ExperienceModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExperienceRepository extends JdbcRepository<ExperienceModel> {

    public ExperienceRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public ExperienceModel save(ExperienceModel experience) throws SQLException {
        if (experience.getId() > 0) {
            update(experience);
            return experience;
        }

        Optional<ExperienceModel> existing = findByDiscordIdAndGuild(experience.getDiscordId(), experience.getGuildId());
        if (existing.isPresent()) {
            experience.setId(existing.get().getId());
            update(experience);
            return experience;
        }

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = prepareInsert(conn, """
                     INSERT INTO experience (discord_id, guild_id, level, experience, total_experience, updated_at)
                     VALUES (?, ?, ?, ?, ?, ?)
                     """)) {
            stmt.setLong(1, experience.getDiscordId());
            stmt.setLong(2, experience.getGuildId());
            stmt.setLong(3, experience.getLevel());
            stmt.setLong(4, experience.getExperience());
            stmt.setLong(5, experience.getTotalExperience());
            stmt.setTimestamp(6, experience.getUpdatedAt());
            stmt.executeUpdate();

            experience.setId(generatedId(stmt));
            return experience;
        }
    }

    private void update(ExperienceModel experience) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                     UPDATE experience
                     SET level = ?, experience = ?, total_experience = ?, updated_at = ?
                     WHERE id = ?
                     """)) {
            stmt.setLong(1, experience.getLevel());
            stmt.setLong(2, experience.getExperience());
            stmt.setLong(3, experience.getTotalExperience());
            stmt.setTimestamp(4, experience.getUpdatedAt());
            stmt.setLong(5, experience.getId());
            stmt.executeUpdate();
        }
    }

    public Optional<ExperienceModel> findByDiscordIdAndGuild(long discordId, long guildId) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM experience WHERE discord_id = ? AND guild_id = ?")) {
            stmt.setLong(1, discordId);
            stmt.setLong(2, guildId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public List<ExperienceModel> getTopByGuild(long guildId, int limit) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM experience WHERE guild_id = ? ORDER BY total_experience DESC LIMIT ?")) {
            stmt.setLong(1, guildId);
            stmt.setInt(2, Math.max(1, Math.min(limit, 100)));
            try (ResultSet rs = stmt.executeQuery()) {
                List<ExperienceModel> items = new ArrayList<>();
                while (rs.next()) items.add(map(rs));
                return items;
            }
        }
    }
public List<ExperienceModel> getTopByLevel(long guildId, int limit) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM experience WHERE guild_id = ? ORDER BY level DESC, total_experience DESC LIMIT ?")) {
            stmt.setLong(1, guildId);
            stmt.setInt(2, Math.max(1, Math.min(limit, 100)));
            try (ResultSet rs = stmt.executeQuery()) {
                List<ExperienceModel> items = new ArrayList<>();
                while (rs.next()) items.add(map(rs));
                return items;
            }
        }
    }

    public long countByGuild(long guildId) throws SQLException {
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM experience WHERE guild_id = ?")) {
            stmt.setLong(1, guildId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        }
    }

    @Override
    protected String tableName() {
        return "experience";
    }

    @Override
    protected ExperienceModel map(ResultSet rs) throws SQLException {
        return new ExperienceModel(
                rs.getLong("id"),
                rs.getLong("discord_id"),
                rs.getLong("guild_id"),
                rs.getLong("level"),
                rs.getLong("experience"),
                rs.getLong("total_experience"),
                rs.getTimestamp("updated_at")
        );
    }
}