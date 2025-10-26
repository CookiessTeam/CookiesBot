package ru.devprizrakk.voidbot.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerManager extends UtilsManager {
    private static final LoggerManager instance = new LoggerManager(); // Синглтон экземпляр
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private Boolean logDebug = false;

    // Приватный конструктор для запрета создания экземпляра класса
    public LoggerManager() {
        LoggerLib();
    }

    // Глобальный доступ к экземпляру логгера
    public static LoggerManager getInstance() {
        return instance;
    }
    public void LoggerLib(){
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        if (getConfig().getProperty("system.debug.enable").equals("true")) {
            switch (getConfig().getProperty("system.debug.level")) {
                case "high" : {
                    logDebug = true;
                    break;
                }
                case "medium" : {
                    //TODO
                    logDebug = true;
                    break;
                }
                case "low" : {
                    loggerContext.getLogger("net.dv8tion.jda").setLevel(Level.OFF);
                    loggerContext.getLogger("net.dv8tion.jda.internal.requests.Requester").setLevel(Level.OFF);
                    loggerContext.getLogger("net.dv8tion.jda.api.requests.RestRateLimiter").setLevel(Level.OFF);
                    loggerContext.getLogger("net.dv8tion.jda.api.JDA").setLevel(Level.OFF);
                    loggerContext.getLogger("net.dv8tion.jda.api.utils.SessionControllerAdapter").setLevel(Level.OFF);

                    loggerContext.getLogger("reactor.util.Loggers").setLevel(Level.OFF);
                    loggerContext.getLogger("dev.arbjerg.lavalink.internal.LavalinkSocket").setLevel(Level.OFF);

                    // Отключаем Spark и Jetty
                    loggerContext.getLogger("spark").setLevel(Level.OFF);
                    loggerContext.getLogger("org.eclipse.jetty").setLevel(Level.OFF);
                    logDebug = true;
                    break;
                }
            }
        } else {
            loggerContext.getLogger("net.dv8tion.jda").setLevel(Level.OFF);
            loggerContext.getLogger("net.dv8tion.jda.internal.requests.Requester").setLevel(Level.OFF);
            loggerContext.getLogger("net.dv8tion.jda.api.requests.RestRateLimiter").setLevel(Level.OFF);
            loggerContext.getLogger("net.dv8tion.jda.api.JDA").setLevel(Level.OFF);
            loggerContext.getLogger("net.dv8tion.jda.api.utils.SessionControllerAdapter").setLevel(Level.OFF);

            loggerContext.getLogger("reactor.util.Loggers").setLevel(Level.OFF);
            loggerContext.getLogger("dev.arbjerg.lavalink.internal.LavalinkSocket").setLevel(Level.OFF);

            // Отключаем Spark и Jetty
            loggerContext.getLogger("spark").setLevel(Level.OFF);
            loggerContext.getLogger("org.eclipse.jetty").setLevel(Level.OFF);
        }
    }

    private String getTimestamp() {
        return dateFormat.format(new Date());
    }

    private void log(String level,String type , String colorCode, String message) {
        String timestamp = getTimestamp();
        String[] lines = message.split("\n");
        for (String line : lines) {
            System.out.println("[" + timestamp + " " + colorCode + level + getColor().ANSI_RESET + "] " + getColor().ANSI_CYAN + "[" + type + "] " + getColor().ANSI_RESET + line);
        }
    }

    public void info(String type, String message) {
        log("INFO",type, getColor().ANSI_GREEN, message + getColor().ANSI_RESET);

    }

    public void debug(String type, String message) {
        if(logDebug) {
            log("DEBUG", type, getColor().ANSI_BLUE, message + getColor().ANSI_RESET);
        }
    }

    public void warn(String type, String message) {
        log("WARN", type, getColor().ANSI_YELLOW, message + getColor().ANSI_RESET);
    }

    public void error(String type, String message) {
        log("ERROR", type, getColor().ANSI_RED, message + getColor().ANSI_RESET);
    }

    public void error(String type, String message, Exception e) {
        error(type, message);
        if (e != null) {
            log("ERROR", type, getColor().ANSI_RED, e.toString());
            for (StackTraceElement element : e.getStackTrace()) {
                log("ERROR", type, getColor().ANSI_RED, "\tat " + element.toString());
            }
        }
    }

}