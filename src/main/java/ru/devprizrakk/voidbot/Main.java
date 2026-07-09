package ru.devprizrakk.voidbot;

import ru.devprizrakk.voidbot.bootstrap.discord.JDALoader;
import ru.devprizrakk.voidbot.language.LangManager;
import ru.devprizrakk.voidbot.logging.LoggerLib;
import ru.devprizrakk.voidbot.utils.Utils;

public class Main {

    static void main(String[] args) {
        new LoggerLib();
        Utils.getDatabaseManager().init();
        LangManager.init();
        JDALoader.init();
    }
}
