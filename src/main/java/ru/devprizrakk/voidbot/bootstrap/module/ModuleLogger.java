package ru.devprizrakk.voidbot.bootstrap.module;

import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;

public class ModuleLogger {

    public static void logLoaded(String moduleName) {
        Logger.getLogger().log(LogType.INFO, "ModuleLoader", "Loaded module: " + moduleName);
    }

    public static void logEnabled(String moduleName) {
        Logger.getLogger().log(LogType.INFO, "ModuleLoader", "Enabled module: " + moduleName);
    }

    public static void logDisabled(String moduleName) {
        Logger.getLogger().log(LogType.INFO, "ModuleLoader", "Disabled module: " + moduleName);
    }

    public static void logFailed(String moduleName, Exception e) {
        Logger.getLogger().log(LogType.ERROR, "ModuleLoader", "Failed to load module: " + moduleName, e);
    }

    public static void logInfo(String message) {
        Logger.getLogger().log(LogType.INFO, "ModuleLoader", message);
    }
}
