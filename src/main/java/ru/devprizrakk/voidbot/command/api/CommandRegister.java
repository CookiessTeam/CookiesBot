package ru.devprizrakk.voidbot.command.api;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
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
import ru.devprizrakk.voidbot.language.LangManager;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CommandRegister extends ListenerAdapter {

    private final List<BaseCommand> commands = new ArrayList<>();
    private static CommandRegister instance;

    public CommandRegister() {
        instance = this;
    }

    public static CommandRegister getInstance() {
        return instance;
    }

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
        // Отсечение посторонних в профиле dev (default_member_permissions = DISABLED,
        // но администратор сервера может включить override в Integrations — запасная проверка).
        if (!isDevAccessGranted(event)) {
            event.reply(getDevAccessDeniedMessage(event)).setEphemeral(true).queue();
            return;
        }

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
                } catch (Throwable t) {
                    Logger.getLogger().log(LogType.ERROR, "command", "Uncaught error in command /" + command.getName(), t);
                    try { event.reply("Произошла непредвиденная ошибка при выполнении команды.").setEphemeral(true).queue(); } catch (Exception ignored) {}
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
                } catch (Throwable t) {
                    Logger.getLogger().log(LogType.ERROR, "sub-command", "Uncaught error in sub-command /" + command.getName() + " " + sub.getName(), t);
                    try { event.reply("Произошла непредвиденная ошибка при выполнении команды.").setEphemeral(true).queue(); } catch (Exception ignored) {}
                }
                return;
            }

            return; // subName пришел, но не найден в списке
        }
    }

    private void syncGuildCommands(Guild guild) {
        boolean devMode = !LangManager.isRemoteSyncEnabled(); // true, когда profile == dev
        List<Long> devRoleIdList = Utils.getConfig().getLongList("system.runtime.dev-access-roles");
        Set<Long> devRoleIds = new HashSet<>(devRoleIdList);

        if (devMode) {
            Logger.getLogger().log(LogType.INFO, "COMMAND_REGISTER",
                    "Профиль dev: команды регистрируются с default_member_permissions = DISABLED. "
                            + "Допуск участников — по ролям из system.runtime.dev-access-roles ("
                            + (devRoleIds.isEmpty()
                                ? "список пуст, допускаются только администраторы сервера"
                                : devRoleIds.size() + " ролей")
                            + "). Чтобы роли ВИДЕЛИ команды, администратор сервера должен включить "
                            + "их в Server Settings > Integrations > VoidBot — бот сам это сделать не может.");
        }

        List<CommandData> built = new ArrayList<>();
        for (BaseCommand command : commands) {
            Logger.getLogger().log(LogType.INFO, "COMMAND_REGISTER", "Успешно зарегистрирована команда: " + command.getName());

            var commandData = Commands.slash(command.getName(), command.getDescription());
            var commandOptions = command.getOptions();

            var defaultPerm = command.getDefaultPermissions();
            if (devMode) {
                // В dev-режиме скрываем команду от всех по умолчанию (видно только админам),
                // остальным доступ открывается либо админом (через Integrations), либо по ролям
                // из system.runtime.dev-access-roles на уровне runtime-гейта (см. onSlashCommandInteraction).
                commandData.setDefaultPermissions(DefaultMemberPermissions.DISABLED);
            } else if (command.isHidden() && defaultPerm == DefaultMemberPermissions.ENABLED) {
                // Скрытая команда без явного указания прав — недоступна никому по умолчанию
                commandData.setDefaultPermissions(DefaultMemberPermissions.DISABLED);
            } else if (defaultPerm != null) {
                commandData.setDefaultPermissions(defaultPerm);
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

    // ------------------------ Dev-mode access ------------------------

    private static boolean isDevMode() {
        return !LangManager.isRemoteSyncEnabled(); // true, когда profile == dev
    }

    private static boolean isDevAccessGranted(SlashCommandInteractionEvent event) {
        if (!isDevMode()) return true; // Прод-режим — пропуск
        Member member = event.getMember();
        if (member == null) return false; // Не из гильдии — нет
        if (member.hasPermission(Permission.ADMINISTRATOR)) return true;

        List<Long> allowedRoleIds = Utils.getConfig().getLongList("system.runtime.dev-access-roles");
        if (allowedRoleIds.isEmpty()) return false; // Список пуст — пускаем только админов (выше)

        for (Role role : member.getRoles()) {
            if (allowedRoleIds.contains(role.getIdLong())) return true;
        }
        return false;
    }

    private static String getDevAccessDeniedMessage(SlashCommandInteractionEvent event) {
        String profile = LangManager.getRuntimeProfile();
        return "Команды бота временно скрыты (профиль `" + (profile != null ? profile : "dev")
                + "`). Доступны только членам команды разработки.";
    }

    public List<BaseCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    public List<BaseCommand> getCommandsByCategory(CommandCategory category) {
        return commands.stream()
                .filter(c -> c.getCategory() == category)
                .toList();
    }

    public List<BaseCommand> getVisibleCommands() {
        return commands.stream()
                .filter(c -> !c.isHidden())
                .toList();
    }
}
