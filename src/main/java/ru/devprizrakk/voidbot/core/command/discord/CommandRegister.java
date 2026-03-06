package ru.devprizrakk.voidbot.core.command.discord;

import java.sql.SQLException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.devprizrakk.voidbot.commands.fun.FunMain;
import ru.devprizrakk.voidbot.commands.music.MusicMain;
import ru.devprizrakk.voidbot.commands.server.ServerMain;
import ru.devprizrakk.voidbot.core.exceptions.discord.NoPermissionErrorEmbedFactory;
import ru.devprizrakk.voidbot.core.logging.LogType;
import ru.devprizrakk.voidbot.core.logging.Logger;

public class CommandRegister extends ListenerAdapter {

    public CommandRegister() {
        commands = new ArrayList<>();
    }
    public List<ICommand> commands;


    @Override
    public void onReady(@NotNull final ReadyEvent event) {
        for (Guild guild : event.getJDA().getGuilds()) {
            syncGuildCommands(guild);
        }
//        for (final Guild guild : event.getJDA().getGuilds()) {
//            for (final ICommand command : commands) {
//                if (command.getOptions() == null || command.getOptions().isEmpty()) {
//                    guild.upsertCommand(command.getName(), command.getDescription()).queue();
//                    Logger.getLogger().log(LogType.INFO,"command", "Команда " + command.getName() + "загружена");
//                } else {
//                    guild.upsertCommand(command.getName(), command.getDescription()).addOptions(command.getOptions()).queue();
//                }
//                //TODO: FIX
//                //new LoggerManager(LoggerEnum.DEBUG, "Команда " + command.getName() + " была загружена!");
//            }
//        }
    }

    @Override
    public void onGuildJoin(final GuildJoinEvent event) {
        for (Guild guild : event.getJDA().getGuilds()) {
            syncGuildCommands(guild);
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
    private void syncGuildCommands(Guild guild) {
        List<CommandData> built = new ArrayList<>();

        for (ICommand command : commands) {
            Logger.getLogger().log(LogType.INFO, "COMMAND_REGISTER", "Успешно зарегистрирована команда: " + command.getName());
            var data = Commands.slash(command.getName(), command.getDescription());
            if (command.getOptions() != null && !command.getOptions().isEmpty()) {
                data.addOptions(command.getOptions());
            }
            built.add(data);
        }
        guild.updateCommands().addCommands(built).queue();
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