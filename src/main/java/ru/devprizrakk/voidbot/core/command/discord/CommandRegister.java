package ru.devprizrakk.voidbot.core.command.discord;

import java.sql.SQLException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
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
    public List<ISubCommand> subCommands;


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
        for (ICommand command : commands) {
            if (!command.getName().equals(event.getName())) {
                continue;
            }

            String subName = event.getSubcommandName();

            // 1) Обычная команда (не саб)
            if (subName == null) {
                if (!hasRequiredPermissions(event, command)) {
                    return;
                }
                try {
                    command.execute(event);
                } catch (SQLException e) {
                    Logger.getLogger().log(LogType.ERROR, "command", "", e);
                }
                return;
            }

            // 2) Сабкоманда
            List<ISubCommand> subs = command.getSubCommand();
            if (subs == null || subs.isEmpty()) {
                return; // root не поддерживает сабкоманды
            }

            for (ISubCommand sub : subs) {
                if (!sub.getName().equals(subName)) {
                    continue;
                }

                if (!hasRequiredPermissions(event, sub.getRequiredPermissions())) {
                    return;
                }

                try {
                    sub.execute(event);
                } catch (SQLException e) {
                    Logger.getLogger().log(LogType.ERROR, "sub-command", "", e);
                }
                return;
            }

            return; // subName пришел, но не найден в списке
        }
    }


    private void syncGuildCommands(Guild guild) {
        List<CommandData> built = new ArrayList<>();

        for (ICommand command : commands) {
            Logger.getLogger().log(LogType.INFO, "COMMAND_REGISTER", "Успешно зарегистрирована команда: " + command.getName());
            var data = Commands.slash(command.getName(), command.getDescription());
            if (command.isHidden()) {
                data.setDefaultPermissions(DefaultMemberPermissions.DISABLED);
            } else {
                var dp = command.getDefaultPermissions();
                if (dp != null) data.setDefaultPermissions(dp);
            }
            if (command.getSubCommand() != null && !command.getSubCommand().isEmpty()) {
                for (ISubCommand iSubCommand : command.getSubCommand()) {
                    //subCommands.add(iSubCommand); // так как мы напрямую у CommandManager не уточняем команды на регистрацию мы их получаем когда регистрируем команды
                    SubcommandData subcommandData = new SubcommandData(iSubCommand.getName(), iSubCommand.getDescription());
                    if (iSubCommand.getOptions() != null && !iSubCommand.getOptions().isEmpty()) {
                        subcommandData.addOptions(iSubCommand.getOptions());
                    }
                    data.addSubcommands(subcommandData);
                }
            } else if (command.getOptions() != null && !command.getOptions().isEmpty()) {
                data.addOptions(command.getOptions());
            }
            built.add(data);
        }
        guild.updateCommands().addCommands(built).queue();
    }


    public void add(ICommand command) {
        commands.add(command);
    }


    public static boolean hasRequiredPermissions(SlashCommandInteractionEvent event, ICommand command) {
        return hasRequiredPermissions(event, command.getRequiredPermissions());
    }

    public static boolean hasRequiredPermissions(SlashCommandInteractionEvent event, List<Permission> requiredPermissions) {
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
