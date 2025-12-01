package ru.devprizrakk.voidbot.core.system.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import ru.devprizrakk.voidbot.core.Utils;

public class LoggerLib extends Utils {
    private boolean logDebug = false;
    public LoggerLib() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        if (getConfigManager().getConfig().getString("system.debug.enable").equals("true")) {
            switch (getConfigManager().getConfig().getString("system.debug.level")) {
                case "high": break;
                case "medium": break;
                case "low": {
                    logDebug = true;
                    // При low отключаем шум
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
