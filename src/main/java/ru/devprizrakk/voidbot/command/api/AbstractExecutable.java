package ru.devprizrakk.voidbot.command.api;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ru.devprizrakk.voidbot.utils.Utils;

import java.sql.SQLException;

public abstract class AbstractExecutable extends Utils {

    protected SlashCommandInteractionEvent event;

    public final void execute(SlashCommandInteractionEvent event) throws SQLException {
        this.event = event;
        onExecute();
    }

    protected abstract void onExecute() throws SQLException;
}
