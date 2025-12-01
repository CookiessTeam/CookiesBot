package ru.devprizrakk.voidbot.commands.discord.loader;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ru.devprizrakk.voidbot.core.Utils;

import java.sql.SQLException;

public abstract class BaseCommand extends Utils implements ICommand {

    protected SlashCommandInteractionEvent event;

    @Override
    public final void execute(SlashCommandInteractionEvent event) throws SQLException {
        this.event = event;     // ← Автоматически сохраняем event
        onExecute();            // ← Логика отдельным методом
    }

    public abstract void onExecute() throws SQLException;
}
