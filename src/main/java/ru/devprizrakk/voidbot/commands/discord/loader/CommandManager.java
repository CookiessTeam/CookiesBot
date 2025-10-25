package ru.devprizrakk.voidbot.commands.discord.loader;

import java.awt.*;
import java.sql.SQLException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.text.SimpleDateFormat;
import java.util.Collection;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.devprizrakk.voidbot.utils.LangManager;
import ru.devprizrakk.voidbot.utils.UtilsManager;

public class CommandManager extends ListenerAdapter {

    public CommandManager() {
        commands = new ArrayList<ICommand>();
    }
    public static List<ICommand> commands;

    private boolean commandsRegistered = false;

    @Override
    public void onReady(@NotNull final ReadyEvent event) {
        if (!commandsRegistered) {
            for (final Guild guild : event.getJDA().getGuilds()) {
                for (final ICommand command : this.commands) {
                    if (command.getOptions() == null) {
                        guild.upsertCommand(command.getName(), command.getDescription()).queue();
                    } else {
                        guild.upsertCommand(command.getName(), command.getDescription()).addOptions((Collection<? extends OptionData>)command.getOptions()).queue();
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
                    guild.upsertCommand(command.getName(), command.getDescription()).addOptions((Collection<? extends OptionData>)command.getOptions()).queue();
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
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void add(final ICommand command) {
        commands.add(command);
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private static String getTimestamp() {
        return dateFormat.format(new Date());
    }

    public static boolean hasRequiredPermissions(SlashCommandInteractionEvent event, ICommand command) {
        String timestamp = getTimestamp();
        List<Permission> requiredPermissions = command.getRequiredPermissions();
        if (requiredPermissions.isEmpty()) {
            return true;  // Если прав не требуется
        }

        if (!event.getMember().hasPermission(requiredPermissions)) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.RED);
            embedBuilder.setTitle(UtilsManager.getLangMessage("system.no-permission.title"));
            embedBuilder.setDescription(UtilsManager.getLangMessage("system.no-permission.description").replace("%hasPermission%", requiredPermissions.toString()));
            embedBuilder.setFooter(UtilsManager.getLangMessage("system.no-permission.footer").replace("%time%", timestamp));
            event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
            return false;
        }
        return true;
    }
}