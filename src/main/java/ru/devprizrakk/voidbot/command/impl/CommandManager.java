package ru.devprizrakk.voidbot.command.impl;

import net.dv8tion.jda.api.JDA;
import ru.devprizrakk.voidbot.bootstrap.discord.JDALoader;
import ru.devprizrakk.voidbot.command.api.CommandRegister;
import ru.devprizrakk.voidbot.command.impl.fun.*;
import ru.devprizrakk.voidbot.command.impl.music.*;
import ru.devprizrakk.voidbot.command.impl.server.moderation.Mod;
import ru.devprizrakk.voidbot.command.impl.server.system.*;
import ru.devprizrakk.voidbot.command.impl.server.user.Leaderboard;
import ru.devprizrakk.voidbot.command.impl.server.user.Rank;
import ru.devprizrakk.voidbot.command.impl.server.user.UserInfo;
import ru.devprizrakk.voidbot.lavalink.GuildMusicManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {
    private static final Map<Long, GuildMusicManager> musicManagers = new ConcurrentHashMap<>();

    public static void init(JDA jda, CommandRegister commandRegister) {
        initMusic(commandRegister);
        initServer(jda, commandRegister);
        initFun(commandRegister);
    }

    public static GuildMusicManager getOrCreateMusicManager(long guildId) {
        return musicManagers.computeIfAbsent(guildId,
                id -> new GuildMusicManager(id, JDALoader.getLavalinkManager().getLavalinkClient()));
    }

    private static void initMusic(CommandRegister commandRegister) {
        commandRegister.addCommand(new Play());
        commandRegister.addCommand(new Stop());
        commandRegister.addCommand(new NowPlaying());
        commandRegister.addCommand(new Pause());
        commandRegister.addCommand(new Volume());
    }

    private static void initServer(JDA jda, CommandRegister commandRegister) {
        commandRegister.addCommand(new Help());
        commandRegister.addCommand(new Mod());
        commandRegister.addCommand(new Rank());
        commandRegister.addCommand(new ServerStats());
        commandRegister.addCommand(new UserInfo());
        commandRegister.addCommand(new ServerInfo());
        commandRegister.addCommand(new Feedback());
        commandRegister.addCommand(new VoiceRoom());
        commandRegister.addCommand(new Leaderboard());

        jda.addEventListener(new HelpSelectMenu());
    }

    private static void initFun(CommandRegister commandRegister) {
        commandRegister.addCommand(new Avatar());
        commandRegister.addCommand(new Calc());
        commandRegister.addCommand(new CoinFlip());
        commandRegister.addCommand(new Emote());
        commandRegister.addCommand(new Joke());
        commandRegister.addCommand(new RPS());
    }
}
