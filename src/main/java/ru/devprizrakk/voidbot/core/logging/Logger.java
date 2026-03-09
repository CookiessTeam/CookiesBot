package ru.devprizrakk.voidbot.core.logging;

public class Logger {
    private static final LoggerManager loggerManager = new LoggerManager();

    public static LoggerManager getLogger() {
        return loggerManager;
    }
}
