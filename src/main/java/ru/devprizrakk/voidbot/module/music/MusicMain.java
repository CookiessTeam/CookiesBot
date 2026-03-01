package ru.devprizrakk.voidbot.module.music;


import dev.arbjerg.lavalink.client.LavalinkClient;
import ru.devprizrakk.voidbot.api.CoreContext;
import ru.devprizrakk.voidbot.api.bootstrap.discord.JDALoader;
import ru.devprizrakk.voidbot.api.command.discord.ModuleCommandRegistrar;
import ru.devprizrakk.voidbot.module.music.command.*;
import ru.devprizrakk.voidbot.module.music.lavalink.GuildMusicManager;

import java.util.HashMap;
import java.util.Map;

public class MusicMain {
    private static final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();
    public static void onLoad(CoreContext coreContext, LavalinkClient lavalinkClient) {
        ModuleCommandRegistrar registrar = coreContext.getModuleCommandRegistrar();
        registrar.registerModuleCommand(new Play());
        registrar.registerModuleCommand(new Stop());
        registrar.registerModuleCommand(new NowPlaying());
        registrar.registerModuleCommand(new Pause());
        registrar.registerModuleCommand(new Volume());
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
