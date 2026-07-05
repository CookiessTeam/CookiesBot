package ru.devprizrakk.voidbot;


import ru.devprizrakk.voidbot.core.bootstrap.discord.JDALoader;
import ru.devprizrakk.voidbot.core.language.LangManager;
import ru.devprizrakk.voidbot.core.logging.LoggerLib;
import ru.devprizrakk.voidbot.core.utils.Utils;

public class Main {
    public static void main(String[] args) {
        new LoggerLib();
        Utils.getDatabaseManager().init();
        LangManager.init();
        JDALoader.init();
    }



}
