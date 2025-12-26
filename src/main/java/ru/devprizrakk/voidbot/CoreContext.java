package ru.devprizrakk.voidbot;

import net.dv8tion.jda.api.JDA;
import ru.devprizrakk.voidbot.command.discord.CommandManager;
import ru.devprizrakk.voidbot.command.discord.ModuleCommandRegistrar;
import ru.devprizrakk.voidbot.events.discord.EventHandler;

public final class CoreContext {
    private final JDA jda;
    private final CommandManager commandManager;
    private final ModuleCommandRegistrar moduleCommandRegistrar;
    private final EventHandler eventHandler;
    // можно добавить Logger, LangManager и т.п.

    public CoreContext(JDA jda) {
        this.jda = jda;
        this.commandManager = new CommandManager();
        this.moduleCommandRegistrar = new ModuleCommandRegistrar(commandManager);
        this.eventHandler = new EventHandler(jda);

        jda.addEventListener(commandManager);
    }

    public JDA getJda() { return jda; }
    public CommandManager getCommandManager() { return commandManager; }
    public ModuleCommandRegistrar getModuleCommandRegistrar() { return moduleCommandRegistrar; }
    public EventHandler getEventHandler() {
        return eventHandler;
    }
}
