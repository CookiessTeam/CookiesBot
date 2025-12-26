package ru.devprizrakk.voidbot.command.discord;

import java.util.List;
import java.util.ArrayList;

/**
 * Менеджер команд модулей
 * Модули регистрируют свои команды здесь,
 * и они автоматически добавляются в CommandManager ядра
 */
public class ModuleCommandRegistrar {

    private final CommandManager commandManager;
    private final List<ICommand> registeredCommands;

    public ModuleCommandRegistrar(CommandManager commandManager) {
        this.commandManager = commandManager;
        this.registeredCommands = new ArrayList<>();
    }

    /**
     * Регистрирует команды модуля
     */
    public void registerModuleCommands(List<ICommand> moduleCommands) {
        for (ICommand command : moduleCommands) {
            if (!registeredCommands.contains(command)) {
                commandManager.add(command);        // добавляем в CommandManager
                registeredCommands.add(command);    // фиксируем чтобы не дублировать
            }
        }
    }

    /**
     * Регистрирует одну команду
     */
    public void registerModuleCommand(ICommand command) {
        if (!registeredCommands.contains(command)) {
            commandManager.add(command);
            registeredCommands.add(command);
        }
    }

    /**
     * Возвращает все зарегистрированные команды модулей
     */
    public List<ICommand> getRegisteredCommands() {
        return new ArrayList<>(registeredCommands);
    }
}
