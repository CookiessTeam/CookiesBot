package ru.devprizrakk.voidbot.database;

import ru.devprizrakk.voidbot.config.Config;
import ru.devprizrakk.voidbot.database.migration.IMigration;
import ru.devprizrakk.voidbot.database.migration.MigrationManager;
import ru.devprizrakk.voidbot.database.migration.migrations.CreateExperienceTable;
import ru.devprizrakk.voidbot.database.migration.migrations.CreateMemberEventsTable;
import ru.devprizrakk.voidbot.database.migration.migrations.CreateMessagesTable;
import ru.devprizrakk.voidbot.database.migration.migrations.AddLevelUpDmColumn;
import ru.devprizrakk.voidbot.database.migration.migrations.CreateUserTable;
import ru.devprizrakk.voidbot.database.migration.migrations.activity.CreateDailyStatisticsTable;
import ru.devprizrakk.voidbot.database.migration.migrations.activity.CreateServerStatisticsTable;
import ru.devprizrakk.voidbot.database.migration.migrations.activity.CreateUserDailyStatisticsTable;
import ru.devprizrakk.voidbot.database.migration.migrations.activity.CreateVoiceSessionsTable;
import ru.devprizrakk.voidbot.database.migration.migrations.moderation.CreateBansTable;
import ru.devprizrakk.voidbot.database.migration.migrations.moderation.CreateMutesTable;
import ru.devprizrakk.voidbot.database.migration.migrations.moderation.CreateWarnsTable;
import ru.devprizrakk.voidbot.database.repository.RepositoryManager;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class DatabaseManager {

    private final String type;
    private final String url;
    private final String login;
    private final String password;
    private RepositoryManager repositoryManager;

    public DatabaseManager() {
        Config config = Utils.getConfig();
        this.type = config.getString("database.type", "sqlite").toLowerCase();

        if (isMysql()) {
            this.url = config.getString("database.mysql.url-connect");
            this.login = config.getString("database.mysql.login");
            this.password = config.getString("database.mysql.password");
        } else {
            this.url = config.getString("database.sqlite.url-connect", "jdbc:sqlite:voidbot.db");
            this.login = null;
            this.password = null;
        }
    }

    public void init() {
        try (Connection conn = getConnection()) {
            MigrationManager migrationManager = new MigrationManager(getMigrations());
            migrationManager.migrate(conn);
            this.repositoryManager = new RepositoryManager(this);
            Logger.getLogger().log(LogType.INFO, "DATABASE", "Database initialized");
        } catch ( SQLException e ) {
            Logger.getLogger().log(LogType.ERROR, "DATABASE", "Database initialization failed", e);
        }
    }

    public Connection getConnection() throws SQLException {
        if (isMysql()) {
            return DriverManager.getConnection(url, login, password);
        }
        return DriverManager.getConnection(url);
    }

    public RepositoryManager getRepositoryManager() {
        if (repositoryManager == null) {
            repositoryManager = new RepositoryManager(this);
        }
        return repositoryManager;
    }

    public boolean isMysql() {
        return type.equals("mysql");
    }

    private List<IMigration> getMigrations() {
        return List.of(
                new CreateUserTable(),
                new AddLevelUpDmColumn(),
                new CreateMessagesTable(),
                new CreateVoiceSessionsTable(),
                new CreateMemberEventsTable(),
                new CreateWarnsTable(),
                new CreateDailyStatisticsTable(),
                new CreateUserDailyStatisticsTable(),
                new CreateServerStatisticsTable(),
                new CreateExperienceTable(),
                new CreateBansTable(),
                new CreateMutesTable()
        );
    }
}
