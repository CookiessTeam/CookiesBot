package ru.devprizrakk.voidbot.commands.music;


import ru.devprizrakk.voidbot.core.bootstrap.discord.JDALoader;
import ru.devprizrakk.voidbot.commands.music.command.*;
import ru.devprizrakk.voidbot.commands.music.lavalink.GuildMusicManager;
import ru.devprizrakk.voidbot.core.command.discord.CommandRegister;

import java.util.HashMap;
import java.util.Map;

public class MusicMain {
    private static final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();
    public static void init(CommandRegister commandRegister) {
        commandRegister.add(new Play());
        commandRegister.add(new Stop());
        commandRegister.add(new NowPlaying());
        commandRegister.add(new Pause());
        commandRegister.add(new Volume());
    }
    public static GuildMusicManager getOrCreateMusicManager(long guildId) {
        synchronized (MusicMain.class) {
            var mng = musicManagers.get(guildId);

            if (mng == null) {
                mng = new GuildMusicManager(guildId, JDALoader.getLavalinkManager().getLavalinkClient());
                musicManagers.put(guildId, mng);
            }

            return mng;
        }
    }

}
