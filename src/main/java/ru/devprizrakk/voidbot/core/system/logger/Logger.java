package ru.devprizrakk.voidbot.core.system.logger;

public class Logger {
    private static final LoggerManager loggerManager = new LoggerManager();

    public static LoggerManager getLogger() {
        return loggerManager;
    }
    public static void initLoggerLib() {
    }
}
