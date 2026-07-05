package ru.devprizrakk.voidbot.core.utils;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import ru.devprizrakk.voidbot.core.config.ConfigManager;
import ru.devprizrakk.voidbot.core.database.DatabaseManager;
import ru.devprizrakk.voidbot.core.utils.applicationinfo.ApplicationInfo;
import ru.devprizrakk.voidbot.core.language.LangHelper;

public class Utils {

    private static final ConfigManager configManager = new ConfigManager();
    private static final ApplicationInfo applicationInfo = new ApplicationInfo();
    private static final DatabaseManager databaseManager = new DatabaseManager();

    public static LangHelper getLangManager(IReplyCallback event) {
        return new LangHelper(event);
    }
    public static LangHelper getLangManager() {
        return new LangHelper();
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }
    public static ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

}
