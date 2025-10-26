package ru.devprizrakk.voidbot.database.repositories;

import ru.devprizrakk.voidbot.database.DatabaseManager;
import ru.devprizrakk.voidbot.database.models.GuildData;
import ru.devprizrakk.voidbot.utils.UtilsManager;

import java.sql.*;

public class GuildRepository extends UtilsManager {

    private final DatabaseManager db;

    public GuildRepository(DatabaseManager db) {
        this.db = db;
        createTable();
    }

    private void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS guilds (
                id INT AUTO_INCREMENT PRIMARY KEY,
                guild_id VARCHAR(100) UNIQUE NOT NULL,
                config_json TEXT
            );
        """;
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            getLogger().info("database | guild-repository", "Таблица с настройками гилдии успешно инициализирована");
        } catch (SQLException e) {
            getLogger().error("database | guild-repository", "Произошла ошибка при инициализации таблицы с настройками гилдии", e);
        }
    }

    public GuildData getGuild(String guildId) {
        String sql = "SELECT * FROM guilds WHERE guild_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, guildId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new GuildData(
                        rs.getInt("id"),
                        rs.getString("guild_id"),
                        rs.getString("config_json")
                );
            }
        } catch (SQLException e) {
            getLogger().error("database | guild-repository", "", e);
        }
        return null;
    }
}
