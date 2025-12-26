package ru.devprizrakk.voidbot.logging;

public class Logger {
    private static final LoggerManager loggerManager = new LoggerManager();

    public static LoggerManager getLogger() {
        return loggerManager;
    }
}
