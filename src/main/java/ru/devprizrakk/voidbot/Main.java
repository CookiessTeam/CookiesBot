package ru.devprizrakk.voidbot;


import net.dv8tion.jda.api.JDA;
import ru.devprizrakk.voidbot.bootstrap.discord.JDALoader;
import ru.devprizrakk.voidbot.bootstrap.libraries.LibraryManager;
import ru.devprizrakk.voidbot.bootstrap.module.ModuleLoader;
import ru.devprizrakk.voidbot.bootstrap.module.ModuleRegistry;
import ru.devprizrakk.voidbot.language.LangManager;
import ru.devprizrakk.voidbot.logging.LoggerLib;

import java.io.File;

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