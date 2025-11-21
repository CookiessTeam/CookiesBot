package ru.devprizrakk.voidbot.loader;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import ru.devprizrakk.voidbot.utils.UtilsManager;

public class JDALoader extends UtilsManager {
    JDA jda;
    public JDALoader(JDA jda) {
        this.jda = jda;
        jdaInit();
    }
    public void jdaInit() {
        getLogger().info("loader","jda-main", "Подключение API Discord...");

        Activity activity;
        switch (getConfig().getProperty("bot.activity.type")) {
            case "streaming" -> activity = Activity.streaming(getConfig().getProperty("bot.activity.text"), getConfig().getProperty("bot.activity.status.streaming-url"));
            case "playing" -> activity = Activity.playing(getConfig().getProperty("bot.activity.text"));
            case "competing" -> activity = Activity.competing(getConfig().getProperty("bot.activity.text"));
            case "watching" -> activity = Activity.watching(getConfig().getProperty("bot.activity.text"));
            case "listening" -> activity = Activity.listening(getConfig().getProperty("bot.activity.text"));
            default -> activity = Activity.customStatus(getConfig().getProperty("bot.activity.text"));
        }
        try {
            jda = JDABuilder.createDefault(getConfig().getProperty("bot.token"))
                    .setStatus(OnlineStatus.ONLINE)
                    .setActivity(activity)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .enableCache(CacheFlag.ONLINE_STATUS)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.MESSAGE_CONTENT)
                    .build();
        } catch (InvalidTokenException e) {
            getLogger().error("loader","jda-main","Неправильный токен доступа", e);

            System.exit(1);
        }
        catch (Exception e) {
            getLogger().error("loader","jda-main","Другая ошибка", e);
            System.exit(1);
        }
        new EventsLoader(jda);
        new CommandsLoader(jda);

    }
}
