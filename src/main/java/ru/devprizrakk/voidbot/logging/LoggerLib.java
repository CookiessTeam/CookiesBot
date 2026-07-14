package ru.devprizrakk.voidbot.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import ru.devprizrakk.voidbot.utils.Utils;

public class LoggerLib {

    public LoggerLib() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        String enabled = Utils.getConfig().getString("system.debug.enable", "false");
        String level = Utils.getConfig().getString("system.debug.level", "low");

        if ("true".equalsIgnoreCase(enabled)) {
            switch (level.toLowerCase()) {
                case "high" -> setRootLevel(loggerContext, Level.DEBUG);
                case "medium" -> setRootLevel(loggerContext, Level.INFO);
                default -> configureDefault(loggerContext);
            }
        } else {
            configureDefault(loggerContext);
        }
    }

    private void setRootLevel(LoggerContext context, Level lvl) {
        context.getLogger("root").setLevel(lvl);
    }

    private void configureDefault(LoggerContext loggerContext) {
        // Критические логи JDA/Lavalink — ERROR (видим ошибки, глушим спам)
        String[] errorOnly = {
                "net.dv8tion.jda",
                "net.dv8tion.jda.api.JDA",
                "dev.arbjerg.lavalink",
                "dev.arbjerg.lavalink.internal.LavalinkSocket",
                "spark",
                "org.eclipse.jetty"
        };
        for (String name : errorOnly) {
            loggerContext.getLogger(name).setLevel(Level.ERROR);
        }

        // Шумные подлоггеры — полностью OFF
        String[] silent = {
                "net.dv8tion.jda.internal.requests.Requester",
                "net.dv8tion.jda.api.requests.RestRateLimiter",
                "net.dv8tion.jda.api.utils.SessionControllerAdapter",
                "reactor.util.Loggers"
        };
        for (String name : silent) {
            loggerContext.getLogger(name).setLevel(Level.OFF);
        }
    }
}