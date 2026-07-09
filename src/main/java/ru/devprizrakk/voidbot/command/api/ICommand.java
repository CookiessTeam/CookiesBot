package ru.devprizrakk.voidbot.command.api;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.sql.SQLException;
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

    default List<BaseSubCommand> getSubCommands() {
        return List.of();
    }

    void execute(final SlashCommandInteractionEvent event) throws SQLException;
}