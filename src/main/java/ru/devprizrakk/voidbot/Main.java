package ru.devprizrakk.voidbot;


import net.dv8tion.jda.api.JDA;
import ru.devprizrakk.voidbot.core.loader.discord.jda.JDALoader;
import ru.devprizrakk.voidbot.core.loader.libraries.LibraryManager;
import ru.devprizrakk.voidbot.core.system.lang.LangManager;
import ru.devprizrakk.voidbot.core.system.logger.LoggerLib;

public class Main {
    static JDA jda;
    public static void main(String[] args) {
        /*Init Module*/
        new LibraryManager().init();
        // Инициализация логера
        new LoggerLib();
        // Инициализация локализации
        LangManager.init();
        // Финальная инициализация API Discord
        new JDALoader(jda);
    }


}