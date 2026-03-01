package ru.devprizrakk.voidbot.api.bootstrap.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import ru.devprizrakk.voidbot.OnReady;
import ru.devprizrakk.voidbot.api.events.EventManager;
import ru.devprizrakk.voidbot.api.utils.Utils;
import ru.devprizrakk.voidbot.api.logging.LogType;
import ru.devprizrakk.voidbot.api.logging.Logger;
import ru.devprizrakk.voidbot.module.music.lavalink.LavalinkManager;

public class JDALoader extends Utils {
    private static JDA jda;
    private static final LavalinkManager lavalinkManager = new LavalinkManager();
    public JDA init() {
        Logger.getLogger().log(LogType.INFO,"loader", "Подключение API Discord...");
//        lavalinkManager  = new LavalinkManager();
        Activity activity;
        switch (getConfigManager().getConfig().getString("bot.activity.type")) {
            case "streaming" -> activity = Activity.streaming(getConfigManager().getConfig().getString("bot.activity.text"), getConfigManager().getConfig().getString("bot.activity.status.streaming-url"));
            case "playing" -> activity = Activity.playing(getConfigManager().getConfig().getString("bot.activity.text"));
            case "competing" -> activity = Activity.competing(getConfigManager().getConfig().getString("bot.activity.text"));
            case "watching" -> activity = Activity.watching(getConfigManager().getConfig().getString("bot.activity.text"));
            case "listening" -> activity = Activity.listening(getConfigManager().getConfig().getString("bot.activity.text"));
            default -> activity = Activity.customStatus(getConfigManager().getConfig().getString("bot.activity.text"));
        }
        try {
            jda = JDABuilder.createDefault(getConfigManager().getConfig().getString("bot.token"))
                    .setVoiceDispatchInterceptor(lavalinkManager.getVoiceUpdateListener())
                    .setStatus(OnlineStatus.ONLINE)
                    .setActivity(activity)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.VOICE_STATE)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_VOICE_STATES)
                    .build()
                    .awaitReady();
            //lavalinkManager = new LavalinkManager();

        } catch (InvalidTokenException e) {
            Logger.getLogger().log(LogType.ERROR,"loader", "Неправильный токен доступа", e);
            System.exit(1);
        }
        catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR,"loader", "Другая ошибка", e);
            System.exit(1);
        }

        EventManager.setJDA(jda);
        jda.addEventListener(new OnReady());
        return jda;

    }
    public static JDA getJDA() {
        return jda;
    }
    public static LavalinkManager getLavalinkManager() {
        return lavalinkManager;
    }
}
