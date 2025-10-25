package ru.devprizrakk.voidbot.utils;

public class UtilsManager {
    public static String getLangMessage(String path) {
        return LangManager.get("ru", path);
    }
    public static ConfigManager getConfig() {
        return new ConfigManager();
    }
    public static ColorConsole getColor() {
        return new ColorConsole();
    }
    public static LoggerManager getLogger() {
        return new LoggerManager();
    }
}
