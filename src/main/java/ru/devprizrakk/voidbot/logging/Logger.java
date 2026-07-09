package ru.devprizrakk.voidbot.logging;

// TODO: В помойку и сделать лучше
public class Logger {

    private static final LoggerManager loggerManager = new LoggerManager();

    public static LoggerManager getLogger() {
        return loggerManager;
    }
}
