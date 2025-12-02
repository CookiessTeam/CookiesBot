package ru.devprizrakk.voidbot.core.loader.discord.jda;

import net.dv8tion.jda.api.JDA;
import ru.devprizrakk.voidbot.commands.discord.fun.*;
import ru.devprizrakk.voidbot.commands.discord.loader.CommandManager;
import ru.devprizrakk.voidbot.commands.discord.system.help.Help;
import ru.devprizrakk.voidbot.commands.discord.system.help.HelpSelectelMenu;
import ru.devprizrakk.voidbot.core.Utils;
import ru.devprizrakk.voidbot.core.system.logger.LogType;
import ru.devprizrakk.voidbot.core.system.logger.Logger;

public class CommandsLoader extends Utils {

    private final CommandManager commandManager = new CommandManager();

    public CommandsLoader(JDA jda) {
        loadFunCommands();
        loadServerCommands();

        jda.addEventListener(commandManager);
        jda.addEventListener(new HelpSelectelMenu());
    }

    private void loadFunCommands() {
        Logger.getLogger().log(LogType.INFO,"loader", "Подгружаю развлекательные команды");

        commandManager.add(new Emote());
        commandManager.add(new Avatar());
        commandManager.add(new CoinFlip());
        commandManager.add(new Joke());
        commandManager.add(new RPS());
    }

    private void loadServerCommands() {
        Logger.getLogger().log(LogType.INFO,"loader", "Подгружаю серверные команды");

        commandManager.add(new Help());
    }
}
