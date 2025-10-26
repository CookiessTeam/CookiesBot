package ru.devprizrakk.voidbot.database;

import ru.devprizrakk.voidbot.database.repositories.*;
import ru.devprizrakk.voidbot.utils.UtilsManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager extends UtilsManager {

    private final String url;
    private final String username;
    private final String password;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ExperienceRepository experienceRepository;
    private final GuildRepository guildRepository;
    private final LogRepository logRepository;

    public DatabaseManager(String jdbc, String username, String password) {
        this.url = jdbc;
        this.username = username;
        this.password = password;

        // Инициализация репозиториев
        this.userRepository = new UserRepository(this);
        this.roleRepository = new RoleRepository(this);
        this.experienceRepository = new ExperienceRepository(this);
        this.guildRepository = new GuildRepository(this);
        this.logRepository = new LogRepository(this);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // --- Репозитории ---
    public UserRepository getUserRepository() {
        return userRepository;
    }

    public RoleRepository getRoleRepository() {
        return roleRepository;
    }

    public ExperienceRepository getExperienceRepository() {
        return experienceRepository;
    }

    public GuildRepository getGuildRepository() {
        return guildRepository;
    }

    public LogRepository getLogRepository() {
        return logRepository;
    }

    // Проверка соединения
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            getLogger().error("database | manager", "При тестовом подключение произошла ошибка", e);
            return false;
        }
    }
}
