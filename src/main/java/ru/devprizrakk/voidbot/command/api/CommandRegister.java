package ru.devprizrakk.voidbot.command.api;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import ru.devprizrakk.voidbot.exceptions.discord.NoPermissionErrorEmbedFactory;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// TODO: Переписать класс и добавить реализацию префиксных команд
public class CommandRegister extends ListenerAdapter {

    private final List<BaseCommand> commands = new ArrayList<>();

    @Override
    public void onReady(@NotNull final ReadyEvent event) {
        for (Guild guild : event.getJDA().getGuilds()) {
            syncGuildCommands(guild);
        }
    }

    @Override
    public void onGuildJoin(final GuildJoinEvent event) {
        for (Guild guild : event.getJDA().getGuilds()) {
            syncGuildCommands(guild);
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        for (BaseCommand command : commands) {
            if (!command.getName().equals(event.getName())) {
                continue;
            }

            String subName = event.getSubcommandName();

            // 1) Обычная команда
            if (subName == null) {
                if (!hasRequiredPermissions(event, command.getRequiredPermissions())) {
                    return;
                }

                try {
                    command.execute(event);
                } catch (SQLException e) {
                    Logger.getLogger().log(LogType.ERROR, "command", "", e);
                } catch (ru.devprizrakk.voidbot.language.LocalizationException e) {
                    Logger.getLogger().log(LogType.ERROR, "command", "Localization error: " + e.getMessage());
                }
                return;
            }

            // 2) Сабкоманда
            List<BaseSubCommand> subs = command.getSubCommands();
            if (subs == null || subs.isEmpty()) {
                return; // root не поддерживает сабкоманды
            }

            for (BaseSubCommand sub : subs) {
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
                } catch (ru.devprizrakk.voidbot.language.LocalizationException e) {
                    Logger.getLogger().log(LogType.ERROR, "sub-command", "Localization error: " + e.getMessage());
                }
                return;
            }

            return; // subName пришел, но не найден в списке
        }
    }

    private void syncGuildCommands(Guild guild) {
        List<CommandData> built = new ArrayList<>();

        for (BaseCommand command : commands) {
            Logger.getLogger().log(LogType.INFO, "COMMAND_REGISTER", "Успешно зарегистрирована команда: " + command.getName());

            var commandData = Commands.slash(command.getName(), command.getDescription());
            var commandOptions = command.getOptions();

            if (command.isHidden()) {
                commandData.setDefaultPermissions(DefaultMemberPermissions.DISABLED);
            } else {
                var defaultPerm = command.getDefaultPermissions();
                if (defaultPerm != null) commandData.setDefaultPermissions(defaultPerm);
            }

            List<BaseSubCommand> subCommands = command.getSubCommands();
            if (subCommands != null && !subCommands.isEmpty()) {
                for (BaseSubCommand subCommand : subCommands) {
                    //subCommands.add(subCommand); // так как мы напрямую у CommandManager не уточняем команды на регистрацию мы их получаем когда регистрируем команды
                    var subcommandData = new SubcommandData(subCommand.getName(), subCommand.getDescription());
                    var subCommandOptions = subCommand.getOptions();
                    if (subCommandOptions != null && !subCommandOptions.isEmpty()) {
                        subcommandData.addOptions(subCommandOptions);
                    }
                    commandData.addSubcommands(subcommandData);
                }
            } else if (commandOptions != null && !commandOptions.isEmpty()) {
                commandData.addOptions(commandOptions);
            }
            built.add(commandData);
        }
        guild.updateCommands().addCommands(built).queue();
    }

    public static boolean hasRequiredPermissions(SlashCommandInteractionEvent event, List<Permission> requiredPermissions) {
        if (requiredPermissions == null || requiredPermissions.isEmpty()) {
            return true; // Если прав не требуется
        }

        if (!Objects.requireNonNull(event.getMember()).hasPermission(requiredPermissions)) {
            event.replyEmbeds(new NoPermissionErrorEmbedFactory(event).noPermission(requiredPermissions.toString()).build()).setEphemeral(true).queue();
            return false;
        }

        return true;
    }

    public void addCommand(BaseCommand command) {
        commands.add(command);
    }

    public List<BaseCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }
}
