package ru.devprizrakk.voidbot.database.repositories;

import ru.devprizrakk.voidbot.database.DatabaseManager;
import ru.devprizrakk.voidbot.database.models.ExperienceData;
import ru.devprizrakk.voidbot.utils.UtilsManager;

import java.sql.*;

public class ExperienceRepository extends UtilsManager {

    private final DatabaseManager db;

    public ExperienceRepository(DatabaseManager db) {
        this.db = db;
        createTable();
    }

    private void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS experience (
                id INT AUTO_INCREMENT PRIMARY KEY,
                uuid VARCHAR(100) UNIQUE NOT NULL,
                level INT DEFAULT 0,
                exp INT DEFAULT 0,
                maxExp INT DEFAULT 100,
                voiceTime BIGINT DEFAULT 0
            );
        """;
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            getLogger().info("database | experience-repository", "Таблица с пользовательской системой уровней успешно инициализирована");
        } catch (SQLException e) {
            getLogger().error("database | experience-repository", "Произошла ошибка при инициализации таблицы с пользовательской системой уровней", e);
        }
    }

    public ExperienceData getByUUID(String uuid) {
        String sql = "SELECT * FROM experience WHERE uuid = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new ExperienceData(
                        rs.getInt("id"),
                        rs.getString("uuid"),
                        rs.getInt("level"),
                        rs.getInt("exp"),
                        rs.getInt("maxExp"),
                        rs.getLong("voiceTime")
                );
            }
        } catch (SQLException e) {
            getLogger().error("database | experience-repository", "", e);
        }
        return null;
    }
}
