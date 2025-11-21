package ru.devprizrakk.voidbot.loader;

import net.dv8tion.jda.api.JDA;
import ru.devprizrakk.voidbot.commands.discord.fun.Emote;
import ru.devprizrakk.voidbot.commands.discord.loader.CommandManager;
import ru.devprizrakk.voidbot.commands.discord.system.help.Help;
import ru.devprizrakk.voidbot.commands.discord.system.help.HelpSelectelMenu;
import ru.devprizrakk.voidbot.utils.UtilsManager;

public class CommandsLoader extends UtilsManager {
    JDA jda;
    public CommandsLoader(JDA jda) {
        this.jda = jda;
        onFunLoader();
        onServerLoader();
    }

    private void onFunLoader() {
        getLogger().info("loader","jda-commands", "Подгружаю развлекательные команды");
        CommandManager commandManager = new CommandManager();

        commandManager.add(new Emote());

        jda.addEventListener(commandManager);
    }
    private void onServerLoader() {
        getLogger().info("loader","jda-commands", "Подгружаю серверные команды");

        CommandManager commandManager = new CommandManager();

        commandManager.add(new Help());
        jda.addEventListener(new HelpSelectelMenu());

        jda.addEventListener(commandManager);
    }
}
