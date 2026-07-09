package ru.devprizrakk.voidbot.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import ru.devprizrakk.voidbot.utils.Utils;

import java.util.Objects;

// TODO: В помойку и сделать лучше
public class LoggerLib {

    public LoggerLib() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        if (Objects.equals(Utils.getConfig().getString("system.debug.enable"), "true")) {
            switch (Utils.getConfig().getString("system.debug.level")) {
                case "high":
                    break;
                case "medium":
                    break; // TODO: Реализовать логгер уровни
                case "low": {
                    disableExternalLogs(loggerContext);
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
}
