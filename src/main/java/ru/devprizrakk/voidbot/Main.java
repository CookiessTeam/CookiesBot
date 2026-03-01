package ru.devprizrakk.voidbot;


import net.dv8tion.jda.api.JDA;
import ru.devprizrakk.voidbot.bootstrap.discord.JDALoader;
import ru.devprizrakk.voidbot.bootstrap.libraries.LibraryManager;
import ru.devprizrakk.voidbot.bootstrap.module.ModuleLoader;
import ru.devprizrakk.voidbot.bootstrap.module.ModuleRegistry;
import ru.devprizrakk.voidbot.language.LangManager;
import ru.devprizrakk.voidbot.logging.LoggerLib;

import java.io.File;
import ru.devprizrakk.voidbot.api.CoreContext;
import ru.devprizrakk.voidbot.api.bootstrap.discord.JDALoader;
import ru.devprizrakk.voidbot.api.language.LangManager;
import ru.devprizrakk.voidbot.api.logging.LoggerLib;

public class Main {
    static JDA jda;
    public static void main(String[] args) {
        new LoggerLib();
        LangManager.init();

        JDALoader jdaLoader = new JDALoader();
        JDA jda = jdaLoader.init();

        CoreContext context = new CoreContext(jda);

        ModuleRegistry registry = new ModuleRegistry();
        ModuleLoader loader = new ModuleLoader(registry, context); // см. ниже
        loader.loadModules(new File("module"));
        registry.enableAll();

        Runtime.getRuntime().addShutdownHook(new Thread(registry::disableAll));
    }


}