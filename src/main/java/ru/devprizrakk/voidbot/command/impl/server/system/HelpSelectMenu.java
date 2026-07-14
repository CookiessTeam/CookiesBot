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
import ru.devprizrakk.voidbot.command.api.BaseSubCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;
import ru.devprizrakk.voidbot.command.api.CommandRegister;
import ru.devprizrakk.voidbot.utils.Utils;

import java.awt.*;
import java.util.List;

public class HelpSelectMenu extends ListenerAdapter {

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
                embed.setTitle(Utils.getLangManager(event).getDescriptionLocale("help.interacts.info.embed.title"));
                embed.setDescription(Utils.getLangManager(event).getDescriptionLocale("help.interacts.info.embed.description"));
                embed.setFooter(Utils.getLangManager(event).getDescriptionLocale("help.interacts.info.embed.footer"));
                embed.addField(
                        Utils.getLangManager(event).getDescriptionLocale("help.interacts.info.embed.fields.developers.title"),
                        Utils.getLangManager(event).getDescriptionLocale("help.interacts.info.embed.fields.developers.description"),
                        true);
                embed.addField(
                        Utils.getLangManager(event).getDescriptionLocale("help.interacts.info.embed.fields.programLang.title"),
                        Utils.getLangManager(event).getDescriptionLocale("help.interacts.info.embed.fields.programLang.description"),
                        true);
                embed.addField(
                        Utils.getLangManager(event).getDescriptionLocale("help.interacts.info.embed.fields.discordLibs.title"),
                        Utils.getLangManager(event).getDescriptionLocale("help.interacts.info.embed.fields.discordLibs.description"),
                        true);
                embed.addField(
                        Utils.getLangManager(event).getDescriptionLocale("help.interacts.info.embed.fields.version.title"),
                        Utils.getLangManager(event).getDescriptionLocale("help.interacts.info.embed.fields.version.description"),
                        true);
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
            }
            case "command" -> {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(255, 104, 0));
                embed.setTitle(Utils.getLangManager(event).getDescriptionLocale("help.interacts.command.embed.title"));
                embed.setDescription(Utils.getLangManager(event).getDescriptionLocale("help.interacts.command.embed.description"));
                embed.setFooter(Utils.getLangManager(event).getDescriptionLocale("help.interacts.command.embed.footer"));
                event.replyEmbeds(embed.build())
                        .addComponents(createCategorySelect(event))
                        .setEphemeral(true)
                        .queue();
            }
        }
    }

    private void handleHelpCommand(StringSelectInteractionEvent event) {
        String selectedValue = event.getValues().getFirst();

        CommandCategory category;
        String categoryLocal;

        switch (selectedValue) {
            case "server" -> { category = CommandCategory.SERVER; categoryLocal = "server"; }
            case "admin" -> { category = CommandCategory.ADMINISTRATION; categoryLocal = "admin"; }
            case "fun" -> { category = CommandCategory.FUN; categoryLocal = "fun"; }
            case "music" -> { category = CommandCategory.MUSIC; categoryLocal = "music"; }
            case "user" -> { category = CommandCategory.USER; categoryLocal = "user"; }
            case "other" -> { category = CommandCategory.OTHER; categoryLocal = "other"; }
            default -> {
                category = null;
                categoryLocal = "notFoundCategory";
            }
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255, 104, 0));
        embed.setTitle(Utils.getLangManager(event)
                .getDescriptionLocale("help.interacts.command.embed.title")
                .replace("%command-category%",
                        Utils.getLangManager(event).getDescriptionLocale(
                                "help.interacts.command.interact.embed.category." + categoryLocal)));
        embed.setFooter(Utils.getLangManager(event).getDescriptionLocale(
                "help.interacts.command.interact.embed.footer"));

        if (category != null && CommandRegister.getInstance() != null) {
            for (BaseCommand command : CommandRegister.getInstance().getCommands()) {
                if (command.getCategory() != category || command.isHidden()) continue;
                appendCommandInfo(embed, event, command);
            }
        }

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }

    private void appendCommandInfo(EmbedBuilder embed, StringSelectInteractionEvent event, BaseCommand command) {
        String nameKey = "help.interacts.command.interact.embed.fields.name.title";
        String nameDesc = "help.interacts.command.interact.embed.fields.name.description";
        String descKey = "help.interacts.command.interact.embed.fields.description.title";
        String descDesc = "help.interacts.command.interact.embed.fields.description.description";
        String optKey = "help.interacts.command.interact.embed.fields.options.title";
        String optDesc = "help.interacts.command.interact.embed.fields.options.description";
        String noOpt = "help.interacts.command.interact.embed.fields.options.not-option";
        String subKey = "help.interacts.command.interact.embed.fields.subcommands.title";
        String subDesc = "help.interacts.command.interact.embed.fields.subcommands.description";
        String noSub = "help.interacts.command.interact.embed.fields.subcommands.not-subcommands";

        embed.addField(
                Utils.getLangManager(event).getDescriptionLocale(nameKey),
                Utils.getLangManager(event).getDescriptionLocale(nameDesc).replace("%name-command%", command.getName()),
                false);

        embed.addField(
                Utils.getLangManager(event).getDescriptionLocale(descKey),
                Utils.getLangManager(event).getDescriptionLocale(descDesc).replace("%description-command%", command.getDescription()),
                true);

        StringBuilder optionsText = new StringBuilder();
        List<OptionData> options = command.getOptions();
        if (options != null) {
            for (OptionData option : options) {
                optionsText.append(option.getName()).append(": ").append(option.getDescription()).append("\n");
            }
        }
        embed.addField(
                Utils.getLangManager(event).getDescriptionLocale(optKey),
                Utils.getLangManager(event).getDescriptionLocale(optDesc)
                        .replace("%option-command%",
                                optionsText.isEmpty()
                                        ? Utils.getLangManager(event).getDescriptionLocale(noOpt)
                                        : optionsText.toString()),
                true);

        List<BaseSubCommand> subs = command.getSubCommands();
        StringBuilder subsText = new StringBuilder();
        if (subs != null && !subs.isEmpty()) {
            for (BaseSubCommand sub : subs) {
                subsText.append(sub.getName()).append(": ").append(sub.getDescription()).append("\n");
            }
        }
        embed.addField(
                Utils.getLangManager(event).getDescriptionLocale(subKey),
                Utils.getLangManager(event).getDescriptionLocale(subDesc)
                        .replace("%subcommands%",
                                subsText.isEmpty()
                                        ? Utils.getLangManager(event).getDescriptionLocale(noSub)
                                        : subsText.toString()),
                true);
    }

    private ActionRow createCategorySelect(StringSelectInteractionEvent event) {
        StringSelectMenu menu = StringSelectMenu.create("helpcommand")
                .addOptions(
                        SelectOption.of(
                                Utils.getLangManager(event).getDescriptionLocale("help.interacts.command.embed.actionRow.server.title"),
                                "server")
                                .withDescription(Utils.getLangManager(event).getDescriptionLocale("help.interacts.command.embed.actionRow.server.description"))
                                .withEmoji(Emoji.fromUnicode("🏠")),
                        SelectOption.of(
                                Utils.getLangManager(event).getDescriptionLocale("help.interacts.command.embed.actionRow.admin.title"),
                                "admin")
                                .withDescription(Utils.getLangManager(event).getDescriptionLocale("help.interacts.command.embed.actionRow.admin.description"))
                                .withEmoji(Emoji.fromUnicode("🔧")),
                        SelectOption.of(
                                Utils.getLangManager(event).getDescriptionLocale("help.interacts.command.embed.actionRow.fun.title"),
                                "fun")
                                .withDescription(Utils.getLangManager(event).getDescriptionLocale("help.interacts.command.embed.actionRow.fun.description"))
                                .withEmoji(Emoji.fromUnicode("🎉")),
                        SelectOption.of(
                                Utils.getLangManager(event).getDescriptionLocale("help.interacts.command.embed.actionRow.music.title"),
                                "music")
                                .withDescription(Utils.getLangManager(event).getDescriptionLocale("help.interacts.command.embed.actionRow.music.description"))
                                .withEmoji(Emoji.fromUnicode("🎵")),
                        SelectOption.of(
                                Utils.getLangManager(event).getDescriptionLocale("help.interacts.command.embed.actionRow.user.title"),
                                "user")
                                .withDescription(Utils.getLangManager(event).getDescriptionLocale("help.interacts.command.embed.actionRow.user.description"))
                                .withEmoji(Emoji.fromUnicode("🔤")),
                        SelectOption.of(
                                Utils.getLangManager(event).getDescriptionLocale("help.interacts.command.embed.actionRow.other.title"),
                                "other")
                                .withDescription(Utils.getLangManager(event).getDescriptionLocale("help.interacts.command.embed.actionRow.other.description"))
                                .withEmoji(Emoji.fromUnicode("❓")))
                .build();
        return ActionRow.of(menu);
    }
}