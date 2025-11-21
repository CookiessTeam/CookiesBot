package ru.devprizrakk.voidbot.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerManager extends UtilsManager {
    private static final LoggerManager instance = new LoggerManager();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private boolean logDebug = false;

    // Настройка выравнивания — можно подстроить под ширину своих категорий
    private static final int CATEGORY_WIDTH = 10;
    private static final int TYPE_WIDTH = 24;

    public LoggerManager() {
        LoggerLib();
    }

    public static LoggerManager getInstance() {
        return instance;
    }

    private void LoggerLib() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        if (getConfig().getProperty("system.debug.enable").equals("true")) {
            switch (getConfig().getProperty("system.debug.level")) {
                case "high":
                case "medium":
                case "low": {
                    logDebug = true;
                    // При low отключаем шум
                    if (getConfig().getProperty("system.debug.level").equals("low")) {
                        disableExternalLogs(loggerContext);
                    }
                    break;
                }
            }
        } else {
            disableExternalLogs(loggerContext);
        }
    }

    private void disableExternalLogs(LoggerContext loggerContext) {
        String[] loggers = {
                "net.dv8tion.jda",
                "net.dv8tion.jda.internal.requests.Requester",
                "net.dv8tion.jda.api.requests.RestRateLimiter",
                "net.dv8tion.jda.api.JDA",
                "net.dv8tion.jda.api.utils.SessionControllerAdapter",
                "reactor.util.Loggers",
                "dev.arbjerg.lavalink.internal.LavalinkSocket",
                "spark",
                "org.eclipse.jetty"
        };
        for (String name : loggers) {
            loggerContext.getLogger(name).setLevel(Level.OFF);
        }
    }

    private String getTimestamp() {
        return dateFormat.format(new Date());
    }

    private String padRight(String text, int length) {
        if (text == null) return " ".repeat(length);
        return String.format("%-" + length + "s", text);
    }

    private void log(String level, String category, String type, String colorCode, String message) {
        String timestamp = getTimestamp();

        // Форматированная строка категории и типа с фиксированной шириной
        String categoryPadded = padRight(category, CATEGORY_WIDTH);
        String typePadded = padRight(type, TYPE_WIDTH);

        String header = String.format("[%s %s%s%s] [%s | %s] ",
                timestamp,
                colorCode,
                level,
                getColor().ANSI_RESET,
                getColor().ANSI_CYAN + categoryPadded + getColor().ANSI_RESET,
                getColor().ANSI_CYAN + typePadded + getColor().ANSI_RESET
        );

        String[] lines = message.split("\n");
        for (String line : lines) {
            System.out.println(header + line);
        }
    }

    public void info(String category, String type, String message) {
        log("INFO", category, type, getColor().ANSI_GREEN, message);
    }

    public void debug(String category, String type, String message) {
        if (logDebug) log("DEBUG", category, type, getColor().ANSI_BLUE, message);
    }

    public void warn(String category, String type, String message) {
        log("WARN", category, type, getColor().ANSI_YELLOW, message);
    }

    public void error(String category, String type, String message) {
        log("ERROR", category, type, getColor().ANSI_RED, message);
    }

    public void error(String category, String type, String message, Exception e) {
        error(category, type, message);
        if (e != null) {
            log("ERROR", category, type, getColor().ANSI_RED, e.toString());
            for (StackTraceElement element : e.getStackTrace()) {
                log("ERROR", category, type, getColor().ANSI_RED, "\tat " + element);
            }
        }
    }
}
