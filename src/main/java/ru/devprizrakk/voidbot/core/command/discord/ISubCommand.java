package ru.devprizrakk.voidbot.core.command.discord;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.sql.SQLException;
import java.util.List;

public interface ISubCommand {
    String getName();

    String getDescription();
    List<OptionData> getOptions();
    List<Permission> getRequiredPermissions();

    void execute(final SlashCommandInteractionEvent event) throws SQLException;
}
