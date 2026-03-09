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
        JDALoader.init();
    }



}