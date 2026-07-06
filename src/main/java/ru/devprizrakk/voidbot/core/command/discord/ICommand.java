package ru.devprizrakk.voidbot.core.command.discord;

import java.sql.SQLException;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public interface ICommand {
    String getName();

    String getDescription();

    default List<OptionData> getOptions() {
        return List.of();
    }
    CommandCategory getCategory();
    default List<Permission> getRequiredPermissions() {
        return List.of();
    }
    default boolean isHidden() {
        return false;
    }
    default DefaultMemberPermissions getDefaultPermissions() {
        return DefaultMemberPermissions.ENABLED;
    }
    default  List<ISubCommand> getSubCommand() {
        return List.of();
    }

    void execute(final SlashCommandInteractionEvent event) throws SQLException;
}