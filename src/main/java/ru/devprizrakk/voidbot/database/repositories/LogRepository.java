package ru.devprizrakk.voidbot.database.repositories;

import ru.devprizrakk.voidbot.database.DatabaseManager;
import ru.devprizrakk.voidbot.database.models.LogData;
import ru.devprizrakk.voidbot.utils.UtilsManager;

import java.sql.*;
import java.util.*;

public class LogRepository extends UtilsManager {

    private final DatabaseManager db;

    public LogRepository(DatabaseManager db) {
        this.db = db;
        createTable();
    }

    private void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS logs (
                id INT AUTO_INCREMENT PRIMARY KEY,
                uuid VARCHAR(100),
                body TEXT,
                createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """;
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            getLogger().info("database | log-repository", "Таблица с логами успешно инициализирована");
        } catch (SQLException e) {
            getLogger().error("database | log-repository", "Произошла ошибка при инициализации таблицы с логами", e);
        }
    }

    public void addLog(String uuid, String body) {
        String sql = "INSERT INTO logs (uuid, body) VALUES (?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ps.setString(2, body);
            ps.executeUpdate();
        } catch (SQLException e) {
            getLogger().error("database | log-repository", "", e);
        }
    }

    public List<LogData> getLogsByUUID(String uuid) {
        List<LogData> list = new ArrayList<>();
        String sql = "SELECT * FROM logs WHERE uuid = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new LogData(
                        rs.getInt("id"),
                        rs.getString("uuid"),
                        rs.getString("body"),
                        rs.getString("createdTime")
                ));
            }
        } catch (SQLException e) {
            getLogger().error("database | log-repository", "", e);
        }
        return list;
    }
}
