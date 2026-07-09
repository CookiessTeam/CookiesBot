package ru.devprizrakk.voidbot.utils;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import ru.devprizrakk.voidbot.config.Config;
import ru.devprizrakk.voidbot.config.ConfigManager;
import ru.devprizrakk.voidbot.database.DatabaseManager;
import ru.devprizrakk.voidbot.language.LangHelper;
import ru.devprizrakk.voidbot.utils.applicationinfo.Module;
import ru.devprizrakk.voidbot.utils.applicationinfo.ModuleInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private static final ConfigManager configManager = new ConfigManager();
    private static final DatabaseManager databaseManager = new DatabaseManager();

    public static LangHelper getLangManager(IReplyCallback event) {
        return new LangHelper(event);
    }

    public static LangHelper getLangManager() {
        return new LangHelper();
    }

    public static Config getConfig() {
        return configManager.getConfig();
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static String getCurrentTime() {
        return dateFormat.format(new Date());
    }

    public static ModuleInfo getModule(Module moduleName) {
        return new ModuleInfo(moduleName);
    }
}
