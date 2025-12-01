package ru.devprizrakk.voidbot;


import net.dv8tion.jda.api.JDA;
import ru.devprizrakk.voidbot.loader.JDALoader;
import ru.devprizrakk.voidbot.utils.LangManager;
import ru.devprizrakk.voidbot.utils.LoggerManager;
import ru.devprizrakk.voidbot.utils.UtilsManager;

public class Main {
    static JDA jda;
    public static String currentVersion = "2.0.0-beta.1";
    public static void main(String[] args) {
        /*Init Module*/
        // Инициализация логера
        new LoggerLib();
        // Инициализация локализации
        LangManager.init();
        // Финальная инициализация API Discord
        new JDALoader(jda);
    }


}