package ru.devprizrakk.voidbot.database.repositories;

import ru.devprizrakk.voidbot.database.DatabaseManager;
import ru.devprizrakk.voidbot.database.models.RoleData;
import ru.devprizrakk.voidbot.utils.UtilsManager;

import java.sql.*;
import java.util.*;

public class RoleRepository extends UtilsManager {

    private final DatabaseManager db;

    public RoleRepository(DatabaseManager db) {
        this.db = db;
        createTable();
    }

    private void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS roles (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) UNIQUE NOT NULL,
                display_name VARCHAR(100),
                perms TEXT,
                discord_sync BOOLEAN DEFAULT FALSE,
                discord_sync_role_id VARCHAR(100)
            );
        """;
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            getLogger().info("database | role-repository", "Таблица с ролями успешно инициализирована");
        } catch (SQLException e) {
            getLogger().error("database | role-repository", "Произошла ошибка при инициализации таблицы с ролями", e);
        }
    }

    public void addRole(String name, String displayName, String perms, boolean discordSync, String discordRoleId) {
        String sql = "INSERT INTO roles (name, display_name, perms, discord_sync, discord_sync_role_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, displayName);
            ps.setString(3, perms);
            ps.setBoolean(4, discordSync);
            ps.setString(5, discordRoleId);
            ps.executeUpdate();
        } catch (SQLException e) {
            getLogger().error("database | role-repository", "", e);
        }
    }

    public RoleData getRoleByName(String name) {
        String sql = "SELECT * FROM roles WHERE name = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new RoleData(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("display_name"),
                        rs.getString("perms"),
                        rs.getBoolean("discord_sync"),
                        rs.getString("discord_sync_role_id")
                );
            }
        } catch (SQLException e) {
            getLogger().error("database | role-repository", "", e);
        }
        return null;
    }
}
