package ru.devprizrakk.voidbot.core.bootstrap.discord;

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
import ru.devprizrakk.voidbot.commands.CommandManager;
import ru.devprizrakk.voidbot.core.command.discord.CommandRegister;
import ru.devprizrakk.voidbot.core.events.EventManager;
import ru.devprizrakk.voidbot.core.utils.Utils;
import ru.devprizrakk.voidbot.core.logging.LogType;
import ru.devprizrakk.voidbot.core.logging.Logger;
import ru.devprizrakk.voidbot.commands.music.lavalink.LavalinkManager;

public class JDALoader extends Utils {
    private static JDA jda;
    private static final LavalinkManager lavalinkManager = new LavalinkManager();

    public static void init() {
        CommandRegister commandRegister = new CommandRegister();
        Logger.getLogger().log(LogType.INFO, "loader", "Подключение API Discord...");
//        lavalinkManager  = new LavalinkManager();
        Activity activity;
        switch (getConfigManager().getConfig().getString("bot.activity.type")) {
            case "streaming" ->
                    activity = Activity.streaming(getConfigManager().getConfig().getString("bot.activity.text"), getConfigManager().getConfig().getString("bot.activity.status.streaming-url"));
            case "playing" ->
                    activity = Activity.playing(getConfigManager().getConfig().getString("bot.activity.text"));
            case "competing" ->
                    activity = Activity.competing(getConfigManager().getConfig().getString("bot.activity.text"));
            case "watching" ->
                    activity = Activity.watching(getConfigManager().getConfig().getString("bot.activity.text"));
            case "listening" ->
                    activity = Activity.listening(getConfigManager().getConfig().getString("bot.activity.text"));
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
                    // TODO: Реализовать подключение протокола DAVE
                    .setAudioModuleConfig(new AudioModuleConfig().withDaveSessionFactory(new JDaveSessionFactory()).withAudioSendFactory(new NativeAudioSendFactory()))
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_VOICE_STATES)
                    .build();
            //lavalinkManager = new LavalinkManager();

        } catch (InvalidTokenException e) {
            Logger.getLogger().log(LogType.ERROR, "loader", "Неправильный токен доступа", e);
            System.exit(1);
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "loader", "Другая ошибка", e);
            System.exit(1);
        }
        EventManager.setJDA(jda);
        jda.addEventListener(new OnReady());
        CommandManager.init(jda, commandRegister);
        jda.addEventListener(commandRegister);

    }

    public static JDA getJDA() {
        return jda;
    }

    public static LavalinkManager getLavalinkManager() {
        return lavalinkManager;
    }

}
