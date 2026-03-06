package ru.devprizrakk.voidbot;


import net.dv8tion.jda.api.JDA;
import ru.devprizrakk.voidbot.core.bootstrap.discord.JDALoader;
import ru.devprizrakk.voidbot.core.language.LangManager;
import ru.devprizrakk.voidbot.core.logging.LoggerLib;
import ru.devprizrakk.voidbot.commands.fun.FunMain;
import ru.devprizrakk.voidbot.commands.music.MusicMain;
import ru.devprizrakk.voidbot.commands.server.ServerMain;

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