package ru.devprizrakk.voidbot.commands.discord.system.help;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.commands.discord.loader.CommandCategory;
import ru.devprizrakk.voidbot.commands.discord.loader.ICommand;
import ru.devprizrakk.voidbot.utils.UtilsManager;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class Help extends UtilsManager implements ICommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return UtilsManager.getLangMessage("command/system/help.yml", "help.description-command");
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }
    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SYSTEM;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of(Permission.MESSAGE_SEND);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws SQLException {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255, 104, 0));
        embed.setTitle(getLangMessage("command/system/help.yml","help.command.embed.title"));
        embed.setDescription(getLangMessage("command/system/help.yml","help.command.embed.description"));
        embed.setFooter(getLangMessage("command/system/help.yml","help.command.embed.footer"));
        event.replyEmbeds(embed.build()).addComponents(createSelectMenu()).queue();
    }

    public static ActionRow createSelectMenu() {
        StringSelectMenu menu = StringSelectMenu.create("helpmenu")
                .setPlaceholder(getLangMessage("command/system/help.yml", "help.command.embed.actionRow.placeholder"))
                .addOptions(SelectOption.of(getLangMessage("command/system/help.yml", "help.command.embed.actionRow.info.title"), "info")
                        .withDescription(getLangMessage("command/system/help.yml", "help.command.embed.actionRow.info.description"))
                        .withEmoji(Emoji.fromUnicode("ℹ️")))
                .addOptions(SelectOption.of(getLangMessage("command/system/help.yml", "help.command.embed.actionRow.command.title"), "command")
                        .withDescription(getLangMessage("command/system/help.yml", "help.command.embed.actionRow.command.description"))
                        .withEmoji(Emoji.fromUnicode("⌨️")))
                .build();

        return ActionRow.of(menu);
    }
}