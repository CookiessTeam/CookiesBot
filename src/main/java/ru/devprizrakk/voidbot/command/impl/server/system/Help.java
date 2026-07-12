package ru.devprizrakk.voidbot.command.impl.server.system;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;

import java.awt.*;
import java.sql.SQLException;

public class Help extends BaseCommand {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("help.description.command");
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SERVER;
    }

    @Override
    public void onExecute() throws SQLException {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255, 104, 0));
        embed.setTitle(getLangManager(event).getDescriptionLocale("help.command.embed.title"));
        embed.setDescription(getLangManager(event).getDescriptionLocale("help.command.embed.description"));
        embed.setFooter(getLangManager(event).getDescriptionLocale("help.command.embed.footer"));
        event.replyEmbeds(embed.build()).addComponents(createSelectMenu(event)).queue();
    }

    public static ActionRow createSelectMenu(SlashCommandInteractionEvent event) {
        StringSelectMenu menu = StringSelectMenu.create("helpmenu")
                .setPlaceholder(getLangManager(event).getDescriptionLocale("help.command.embed.actionRow.placeholder"))
                .addOptions(SelectOption.of(getLangManager(event).getDescriptionLocale("help.command.embed.actionRow.info.title"), "info")
                        .withDescription(getLangManager(event).getDescriptionLocale("help.command.embed.actionRow.info.description"))
                        .withEmoji(Emoji.fromUnicode("ℹ️")))
                .addOptions(SelectOption.of(getLangManager(event).getDescriptionLocale("help.command.embed.actionRow.command.title"), "command")
                        .withDescription(getLangManager(event).getDescriptionLocale("help.command.embed.actionRow.command.description"))
                        .withEmoji(Emoji.fromUnicode("⌨️")))
                .build();

        return ActionRow.of(menu);
    }
}