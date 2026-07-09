package ru.devprizrakk.voidbot.bootstrap.discord;

import club.minnced.discord.jdave.interop.JDaveSessionFactory;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.audio.AudioModuleConfig;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import ru.devprizrakk.voidbot.OnReady;
import ru.devprizrakk.voidbot.command.api.CommandRegister;
import ru.devprizrakk.voidbot.command.impl.CommandManager;
import ru.devprizrakk.voidbot.events.*;
import ru.devprizrakk.voidbot.events.autocreate.ThreadsListeners;
import ru.devprizrakk.voidbot.lavalink.LavalinkManager;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

public class JDALoader extends Utils {

    private static JDA jda;
    private static final LavalinkManager lavalinkManager = new LavalinkManager();

    public static void init() {
        CommandRegister commandRegister = new CommandRegister();
        Logger.getLogger().log(LogType.INFO, "loader", "Подключение API Discord...");

        // TODO: Продебажить работу активности

        try {
            jda = JDABuilder.createDefault(getConfig().getString("bot.token"))
                    .setVoiceDispatchInterceptor(lavalinkManager.getVoiceUpdateListener())
                    .setStatus(OnlineStatus.ONLINE)
                    .setActivity(buildActivity())
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.VOICE_STATE)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setAudioModuleConfig(new AudioModuleConfig().withDaveSessionFactory(new JDaveSessionFactory()).withAudioSendFactory(new NativeAudioSendFactory()))
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_VOICE_STATES)
                    .build();
        } catch ( InvalidTokenException e ) {
            Logger.getLogger().log(LogType.ERROR, "loader", "Неправильный токен доступа", e);
            System.exit(1);
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "loader", "Другая ошибка", e);
            System.exit(1);
        }

        jda.addEventListener(new OnReady());
        jda.addEventListener(new ThreadsListeners());
        jda.addEventListener(new MemberEventListener());
        jda.addEventListener(new VoiceStateListener());
        jda.addEventListener(new MessageReceiveListener());
        jda.addEventListener(new MessageDeleteListener());
        jda.addEventListener(new FeedbackListener());
        CommandManager.init(jda, commandRegister);
        jda.addEventListener(commandRegister);
    }

    public static JDA getJDA() {
        return jda;
    }

    public static LavalinkManager getLavalinkManager() {
        return lavalinkManager;
    }

    private static Activity buildActivity() {
        return switch (getConfig().getString("bot.activity.type")) {
            case "streaming" -> Activity.streaming(
                    getConfig().getString("bot.activity.text"),
                    getConfig().getString("bot.activity.status.streaming-url")
            );
            case "playing" -> Activity.playing(getConfig().getString("bot.activity.text"));
            case "competing" -> Activity.competing(getConfig().getString("bot.activity.text"));
            case "watching" -> Activity.watching(getConfig().getString("bot.activity.text"));
            case "listening" -> Activity.listening(getConfig().getString("bot.activity.text"));
            default -> Activity.customStatus(getConfig().getString("bot.activity.text"));
        };
    }
}
