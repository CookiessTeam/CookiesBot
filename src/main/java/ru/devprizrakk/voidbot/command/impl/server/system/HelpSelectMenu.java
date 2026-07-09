package ru.devprizrakk.voidbot.command.impl.server.system;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;
import ru.devprizrakk.voidbot.language.LangMessage;
import ru.devprizrakk.voidbot.utils.Utils;

import java.awt.*;
import java.util.List;

public class HelpSelectMenu extends ListenerAdapter {

    private final List<BaseCommand> commands;

    public HelpSelectMenu(List<BaseCommand> commands) {
        this.commands = commands;
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("helpmenu")) {
            handleHelpMenuRoot(event);
            return;
        }

        if (event.getComponentId().equals("helpcommand")) {
            handleHelpCommand(event);
        }
    }

    private void handleHelpMenuRoot(StringSelectInteractionEvent event) {
        String selectedValue = event.getValues().getFirst();

        switch (selectedValue) {
            case "info" -> {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Interacts.Info.Embed.TITLE));
                embed.setDescription(Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Interacts.Info.Embed.DESCRIPTION));
                embed.setFooter(Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Interacts.Info.Embed.FOOTER));
                embed.addField(
                        Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Interacts.Info.Embed.Fields.Developers.TITLE),
                        Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Interacts.Info.Embed.Fields.Developers.DESCRIPTION),
                        true
                );
                embed.addField(
                        Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Interacts.Info.Embed.Fields.ProgramLang.TITLE),
                        Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Interacts.Info.Embed.Fields.ProgramLang.DESCRIPTION),
                        true
                );
                embed.addField(
                        Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Interacts.Info.Embed.Fields.DiscordLibs.TITLE),
                        Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Interacts.Info.Embed.Fields.DiscordLibs.DESCRIPTION),
                        true
                );
                embed.addField(
                        Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Interacts.Info.Embed.Fields.Version.TITLE),
                        Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Interacts.Info.Embed.Fields.Version.DESCRIPTION),
                        true
                );
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
            }
            case "command" -> {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(255, 104, 0));
                embed.setTitle(Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Interacts.Command.Embed.TITLE));
                embed.setDescription(Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Interacts.Command.Embed.DESCRIPTION));
                embed.setFooter(Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE, LangMessage.Commands.System.Help.Interacts.Command.Embed.FOOTER));
                event.replyEmbeds(embed.build())
                        .addComponents(createActionRow(event))
                        .setEphemeral(true)
                        .queue();
            }
        }
    }

    private void handleHelpCommand(StringSelectInteractionEvent event) {
        String selectedValue = event.getValues().getFirst();
        EmbedBuilder embed = new EmbedBuilder();

        CommandCategory category;
        String categoryLocal;

        switch (selectedValue) {
            case "server" -> {
                category = CommandCategory.SERVER;
                categoryLocal = "server";
            }
            case "admin" -> {
                category = CommandCategory.ADMINISTRATION;
                categoryLocal = "admin";
            }
            case "fun" -> {
                category = CommandCategory.FUN;
                categoryLocal = "fun";
            }
            case "music" -> {
                category = CommandCategory.MUSIC;
                categoryLocal = "music";
            }
            case "user" -> {
                category = CommandCategory.USER;
                categoryLocal = "user";
            }
            case "other" -> {
                category = CommandCategory.OTHER;
                categoryLocal = "other";
            }
            default -> {
                embed.setDescription(Utils.getLangManager(event)
                        .getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                                LangMessage.Commands.System.Help.Interacts.Command.Interact.Embed.Category.NOT_FOUND_CATEGORY));
                category = null;
                categoryLocal = "notFoundCategory";
            }
        }

        embed.setColor(new Color(255, 104, 0));
        embed.setTitle(
                Utils.getLangManager(event)
                        .getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                                LangMessage.Commands.System.Help.Interacts.Command.Embed.TITLE)
                        .replace(
                                "%command-category%",
                                Utils.getLangManager(event)
                                        .getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                                                LangMessage.Commands.System.Help.Interacts.Command.Interact.Embed.Category.BASE_PATH + categoryLocal)
                        )
        );
        embed.setFooter(Utils.getLangManager(event)
                .getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                        LangMessage.Commands.System.Help.Interacts.Command.Interact.Embed.FOOTER
                ));

        if (category != null) {
            for (BaseCommand command : commands) {
                if (command.getCategory() == category) {
                    appendCommandInfo(embed, event, command);
                }
            }
        }

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }

    private void appendCommandInfo(EmbedBuilder embed, StringSelectInteractionEvent event, BaseCommand command) {
        StringBuilder optionsDescription = new StringBuilder();
        List<OptionData> options = command.getOptions();

        if (options != null) {
            for (OptionData option : options) {
                optionsDescription.append(option.getName())
                        .append(": ")
                        .append(option.getDescription())
                        .append("\n");
            }
        }

        embed.addField(
                Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                        LangMessage.Commands.System.Help.Interacts.Command.Interact.Embed.Fields.Name.TITLE),
                Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                                LangMessage.Commands.System.Help.Interacts.Command.Interact.Embed.Fields.Name.DESCRIPTION)
                        .replace("%name-command%", command.getName()),
                false
        );

        embed.addField(
                Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                        LangMessage.Commands.System.Help.Interacts.Command.Interact.Embed.Fields.Description.TITLE),
                Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                                LangMessage.Commands.System.Help.Interacts.Command.Interact.Embed.Fields.Description.DESCRIPTION)
                        .replace("%description-command%", command.getDescription()),
                true
        );

        String optionsText = !optionsDescription.isEmpty()
                ? optionsDescription.toString()
                : Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                LangMessage.Commands.System.Help.Interacts.Command.Interact.Embed.Fields.Option.NOT_OPTION);

        embed.addField(
                Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                        LangMessage.Commands.System.Help.Interacts.Command.Interact.Embed.Fields.Option.TITLE),
                Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                                LangMessage.Commands.System.Help.Interacts.Command.Interact.Embed.Fields.Option.DESCRIPTION)
                        .replace("%option-command%", optionsText),
                true
        );
    }

    private ActionRow createActionRow(StringSelectInteractionEvent event) {
        StringSelectMenu stringSelectMenu = StringSelectMenu.create("helpcommand")
                .addOptions(
                        SelectOption.of(
                                        Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                                                LangMessage.Commands.System.Help.Interacts.Command.Embed.ActionRow.Server.TITLE),
                                        "server")
                                .withDescription(Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                                        LangMessage.Commands.System.Help.Interacts.Command.Embed.ActionRow.Server.DESCRIPTION))
                                .withEmoji(Emoji.fromUnicode("🏠")),
                        SelectOption.of(
                                        Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                                                LangMessage.Commands.System.Help.Interacts.Command.Embed.ActionRow.Admin.TITLE),
                                        "admin")
                                .withDescription(Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                                        LangMessage.Commands.System.Help.Interacts.Command.Embed.ActionRow.Admin.DESCRIPTION))
                                .withEmoji(Emoji.fromUnicode("🔧")),
                        SelectOption.of(
                                        Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                                                LangMessage.Commands.System.Help.Interacts.Command.Embed.ActionRow.Fun.TITLE),
                                        "fun")
                                .withDescription(Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                                        LangMessage.Commands.System.Help.Interacts.Command.Embed.ActionRow.Fun.DESCRIPTION))
                                .withEmoji(Emoji.fromUnicode("🎉")),
                        SelectOption.of(
                                        Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                                                LangMessage.Commands.System.Help.Interacts.Command.Embed.ActionRow.Music.TITLE),
                                        "music")
                                .withDescription(Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                                        LangMessage.Commands.System.Help.Interacts.Command.Embed.ActionRow.Music.DESCRIPTION))
                                .withEmoji(Emoji.fromUnicode("🎵")),
                        SelectOption.of(
                                        Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                                                LangMessage.Commands.System.Help.Interacts.Command.Embed.ActionRow.User.TITLE),
                                        "user")
                                .withDescription(Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                                        LangMessage.Commands.System.Help.Interacts.Command.Embed.ActionRow.User.DESCRIPTION))
                                .withEmoji(Emoji.fromUnicode("🔤")),
                        SelectOption.of(
                                        Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                                                LangMessage.Commands.System.Help.Interacts.Command.Embed.ActionRow.Other.TITLE),
                                        "other")
                                .withDescription(Utils.getLangManager(event).getDescriptionLocale(LangMessage.Commands.System.Help.FILE,
                                        LangMessage.Commands.System.Help.Interacts.Command.Embed.ActionRow.Other.DESCRIPTION))
                                .withEmoji(Emoji.fromUnicode("❓"))
                )
                .build();

        return ActionRow.of(stringSelectMenu);
    }
}
