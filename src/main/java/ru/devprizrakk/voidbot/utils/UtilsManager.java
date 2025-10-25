package ru.devprizrakk.voidbot.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilsManager {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public static String getLangMessage(String ymlFilePath,String path) {
        return LangManager.get("ru", ymlFilePath, path)
                .replace("%current-time%", getCurrentTime())
                .replace("%version%", getVersion());
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
    public static String getCurrentTime() {
        return dateFormat.format(new Date());
    }
    public static String getVersion() {
        //TODO: Реализовать метод получения версии
        return "2.0.0-beta.01";
    }
}
