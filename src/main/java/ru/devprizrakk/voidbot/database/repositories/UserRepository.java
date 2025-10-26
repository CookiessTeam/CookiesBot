package ru.devprizrakk.voidbot.database.repositories;

import ru.devprizrakk.voidbot.database.DatabaseManager;
import ru.devprizrakk.voidbot.database.models.UserData;
import ru.devprizrakk.voidbot.utils.UtilsManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository extends UtilsManager {

    private final DatabaseManager db;

    public UserRepository(DatabaseManager db) {
        this.db = db;
        createTable();
    }

    private void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                uuid VARCHAR(100) UNIQUE NOT NULL,
                warn INT DEFAULT 0,
                role VARCHAR(100)
            );
        """;

        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            getLogger().info("database | user-repository", "Таблица с пользователями успешно инициализирована");
        } catch (SQLException e) {
            getLogger().error("database | user-repository", "Произошла ошибка при инициализации таблицы с пользователями", e);
        }
    }

    public void addUser(String uuid, String role) {
        String sql = "INSERT INTO users (uuid, role) VALUES (?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ps.setString(2, role);
            ps.executeUpdate();
        } catch (SQLException e) {
            getLogger().error("database | user-repository", "", e);
        }
    }

    public UserData getUserByUUID(String uuid) {
        String sql = "SELECT * FROM users WHERE uuid = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new UserData(
                        rs.getInt("id"),
                        rs.getString("uuid"),
                        rs.getInt("warn"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            getLogger().error("database | user-repository", "", e);
        }
        return null;
    }

    public List<UserData> getAllUsers() {
        List<UserData> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(new UserData(
                        rs.getInt("id"),
                        rs.getString("uuid"),
                        rs.getInt("warn"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            getLogger().error("database | user-repository", "", e);
        }

        return users;
    }

    public void updateWarn(String uuid, int warn) {
        String sql = "UPDATE users SET warn = ? WHERE uuid = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, warn);
            ps.setString(2, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            getLogger().error("database | user-repository", "", e);
        }
    }

    public void updateRole(String uuid, String role) {
        String sql = "UPDATE users SET role = ? WHERE uuid = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setString(2, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            getLogger().error("database | user-repository", "", e);
        }
    }

    public void deleteUser(String uuid) {
        String sql = "DELETE FROM users WHERE uuid = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            getLogger().error("database | user-repository", "", e);
        }
    }
}
