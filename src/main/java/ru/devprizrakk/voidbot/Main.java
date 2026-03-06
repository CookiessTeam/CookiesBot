package ru.devprizrakk.voidbot;


import net.dv8tion.jda.api.JDA;
import ru.devprizrakk.voidbot.api.CoreContext;
import ru.devprizrakk.voidbot.api.bootstrap.discord.JDALoader;
import ru.devprizrakk.voidbot.api.language.LangManager;
import ru.devprizrakk.voidbot.api.logging.LoggerLib;
import ru.devprizrakk.voidbot.module.fun.FunMain;
import ru.devprizrakk.voidbot.module.music.MusicMain;
import ru.devprizrakk.voidbot.module.server.ServerMain;
import ru.devprizrakk.voidbot.core.bootstrap.discord.JDALoader;
import ru.devprizrakk.voidbot.core.language.LangManager;
import ru.devprizrakk.voidbot.core.logging.LoggerLib;

public class Main {
    public static void main(String[] args) {
        new LoggerLib();
        LangManager.init();

        JDALoader jdaLoader = new JDALoader();
        JDA jda = jdaLoader.init();

        CoreContext context = new CoreContext(jda);

        onLoad(context);

    }
    public static void onLoad(CoreContext coreContext) {
        MusicMain.onLoad(coreContext, JDALoader.getLavalinkManager().getLavalinkClient());
        ServerMain.onLoad(coreContext);
        FunMain.onLoad(coreContext);
    }


}