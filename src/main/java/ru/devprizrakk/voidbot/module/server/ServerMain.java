package ru.devprizrakk.voidbot.module.server;

import ru.devprizrakk.voidbot.api.CoreContext;
import ru.devprizrakk.voidbot.api.command.discord.ICommand;
import ru.devprizrakk.voidbot.api.command.discord.ModuleCommandRegistrar;
import ru.devprizrakk.voidbot.module.server.commands.moderation.Ban;
import ru.devprizrakk.voidbot.module.server.commands.system.Help;
import ru.devprizrakk.voidbot.module.server.commands.system.HelpSelectMenu;

import java.util.List;

public class ServerMain {

    public static void onLoad(CoreContext coreContext) {

        // Регистрируем команды модуля в ОБЩЕМ ModuleCommandRegistrar из ядра
        ModuleCommandRegistrar registrar = coreContext.getModuleCommandRegistrar();
        registrar.registerModuleCommand(new Help());
        registrar.registerModuleCommand(new Ban());

        // Вешаем листенер help-меню на общий EventHandler / JDA
        List<ICommand> allCommands = registrar.getRegisteredCommands(); // или coreContext.getCommandManager().getCommands()
        HelpSelectMenu helpSelectMenu = new HelpSelectMenu(allCommands);

        coreContext.getEventHandler().register(helpSelectMenu);
    }


}