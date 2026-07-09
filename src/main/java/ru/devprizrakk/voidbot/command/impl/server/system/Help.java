package ru.devprizrakk.voidbot.command.impl.server.system;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;
import ru.devprizrakk.voidbot.language.LangMessage;

import java.awt.*;
import java.sql.SQLException;

public class Help extends BaseCommand {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Description.COMMAND);
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SERVER;
    }

    @Override
    public void onExecute() throws SQLException {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255, 104, 0));
        embed.setTitle(getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Command.Embed.TITLE));
        embed.setDescription(getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Command.Embed.DESCRIPTION));
        embed.setFooter(getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Command.Embed.FOOTER));
        event.replyEmbeds(embed.build()).addComponents(createSelectMenu(event)).queue();
    }

    public static ActionRow createSelectMenu(SlashCommandInteractionEvent event) {
        StringSelectMenu menu = StringSelectMenu.create("helpmenu")
                .setPlaceholder(getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Command.Embed.ActionRow.PLACEHOLDER))
                .addOptions(SelectOption.of(getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Command.Embed.ActionRow.Info.TITLE), "info")
                        .withDescription(getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Command.Embed.ActionRow.Info.DESCRIPTION))
                        .withEmoji(Emoji.fromUnicode("ℹ️")))
                .addOptions(SelectOption.of(getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Command.Embed.ActionRow.CommandA.TITLE), "command")
                        .withDescription(getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Command.Embed.ActionRow.CommandA.DESCRIPTION))
                        .withEmoji(Emoji.fromUnicode("⌨️")))
                .build();

        return ActionRow.of(menu);
    }
}