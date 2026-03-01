package ru.devprizrakk.voidbot.api.command.discord;

import java.sql.SQLException;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.devprizrakk.voidbot.api.exceptions.discord.NoPermissionErrorEmbedFactory;
import ru.devprizrakk.voidbot.api.logging.LogType;
import ru.devprizrakk.voidbot.api.logging.Logger;

public class CommandManager extends ListenerAdapter {

    public CommandManager() {
        commands = new ArrayList<>();
    }
    public List<ICommand> commands;

    private boolean commandsRegistered = false;

    @Override
    public void onReady(@NotNull final ReadyEvent event) {
        if (!commandsRegistered) {
            for (final Guild guild : event.getJDA().getGuilds()) {
                for (final ICommand command : commands) {
                    if (command.getOptions() == null || command.getOptions().isEmpty()) {
                        guild.upsertCommand(command.getName(), command.getDescription()).queue();
                        Logger.getLogger().log(LogType.INFO,"command", "Команда " + command.getName() + "загружена");
                    } else {
                        guild.upsertCommand(command.getName(), command.getDescription()).addOptions(command.getOptions()).queue();
                    }
                    //TODO: FIX
                    //new LoggerManager(LoggerEnum.DEBUG, "Команда " + command.getName() + " была загружена!");
                }
            }
            commandsRegistered = true; // Устанавливаем флаг после регистрации команд
        }
    }

    @Override
    public void onGuildJoin(final GuildJoinEvent event) {
        for (final Guild guild : event.getJDA().getGuilds()) {
            for (final ICommand command : commands) {
                if (command.getOptions() == null) {
                    guild.upsertCommand(command.getName(), command.getDescription()).queue();
                }
                else {
                    guild.upsertCommand(command.getName(), command.getDescription()).addOptions(command.getOptions()).queue();
                }
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        for (final ICommand command : commands) {
            if (command.getName().equals(event.getName())) {
                if (!hasRequiredPermissions(event, command)) {
                    return;
                }
                try {
                    command.execute(event);
                }
                catch (SQLException e) {
                    Logger.getLogger().log(LogType.ERROR,"command", "", e);
                }
            }
        }
    }

    public void add(final ICommand command) {
        commands.add(command);
    }


    public static boolean hasRequiredPermissions(SlashCommandInteractionEvent event, ICommand command) {
        List<Permission> requiredPermissions = command.getRequiredPermissions();
        if (requiredPermissions == null || requiredPermissions.isEmpty()) {
            return true;  // Если прав не требуется
        }
        if (!Objects.requireNonNull(event.getMember()).hasPermission(requiredPermissions)) {
            event.replyEmbeds(new NoPermissionErrorEmbedFactory(event).noPermission(requiredPermissions.toString()).build()).setEphemeral(true).queue();
            return false;
        }
        return true;
    }
}