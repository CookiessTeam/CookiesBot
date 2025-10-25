package ru.devprizrakk.voidbot.commands.discord.system.help;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.commands.discord.loader.CommandCategory;
import ru.devprizrakk.voidbot.commands.discord.loader.CommandManager;
import ru.devprizrakk.voidbot.commands.discord.loader.ICommand;
import ru.devprizrakk.voidbot.utils.UtilsManager;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class HelpSelectelMenu extends ListenerAdapter {
    public List<ICommand> commands = CommandManager.commands; // Список всех команд

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("helpmenu")) {
            String selectedValue = event.getValues().get(0); // Получение первого выбранного значения
            switch (selectedValue) {
                case "info" -> {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.info.embed.title"));
                    embed.setDescription(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.info.embed.description"));
                    embed.setFooter(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.info.embed.footer"));
                    embed.addField(
                            UtilsManager.getLangMessage("command/system/help.yml", "help.interact.info.embed.fields.developers.title"),
                            UtilsManager.getLangMessage("command/system/help.yml", "help.interact.info.embed.fields.developers.description"),
                            true);
                    embed.addField(
                            UtilsManager.getLangMessage("command/system/help.yml", "help.interact.info.embed.fields.programLang.title"),
                            UtilsManager.getLangMessage("command/system/help.yml", "help.interact.info.embed.fields.programLang.description"),
                            true);
                    embed.addField(
                            UtilsManager.getLangMessage("command/system/help.yml", "help.interact.info.embed.fields.discordLibs.title"),
                            UtilsManager.getLangMessage("command/system/help.yml", "help.interact.info.embed.fields.discordLibs.description"),
                            true);
                    embed.addField(
                            UtilsManager.getLangMessage("command/system/help.yml", "help.interact.info.embed.fields.version.title"),
                            UtilsManager.getLangMessage("command/system/help.yml", "help.interact.info.embed.fields.version.description").replace("%version%", UtilsManager.getVersion()),
                            true);
                    event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                }
                case "command" -> {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(new Color(255, 104, 0));
                    embed.setTitle(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.embed.title"));
                    embed.setDescription(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.embed.description"));
                    embed.setFooter(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.embed.footer"));
                    event.replyEmbeds(embed.build()).addComponents(createActionRow()).setEphemeral(true).queue();
                }
            }
        }
        if (event.getComponentId().equals("helpcommand")) {
            String selectedValue = event.getValues().get(0);
            EmbedBuilder embed = new EmbedBuilder();// Получение первого выбранного значения
            CommandCategory category;
            String categoryLocal;
            switch (selectedValue) {
                case "server":
                    category = CommandCategory.SERVER;
                    categoryLocal = "server";
                    break;
                case "admin":
                    category = CommandCategory.ADMINISTRATION;
                    categoryLocal = "admin";
                    break;
                case "fun":
                    category = CommandCategory.FUN;
                    categoryLocal = "fun";
                    break;
                case "music":
                    category = CommandCategory.MUSIC;
                    categoryLocal = "music";
                    break;
                case "user":
                    category = CommandCategory.USER;
                    categoryLocal = "user";
                    break;
                case "other":
                    category = CommandCategory.OTHER;
                    categoryLocal = "other";
                    break;
                default:
                    embed.setDescription(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.interact.embed.category.notFoundCategory"));
                    categoryLocal = "notFoundCategory";
                    category = null;
                    break;
            }
            embed.setColor(new Color(255, 104, 0));
            embed.setTitle(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.interact.embed.title")
                    .replace("%command-category%", UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.interact.embed.category." + categoryLocal)));
            embed.setFooter(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.interact.embed.footer"));


            if (category != null) {
                for (ICommand command : commands) {
                    if (command.getCategory() == category) {

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
                        embed.addField(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.interact.embed.field.name.title"),
                                UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.interact.embed.field.name.description")
                                        .replace("%name-command%",command.getName()), false);
                        embed.addField(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.interact.embed.field.description.title"),
                                UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.interact.embed.field.description.description")
                                        .replace("%description-command%", command.getDescription()), true);
                        embed.addField(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.interact.embed.field.option.title"),
                                UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.interact.embed.field.option.description")
                                        .replace("%option-command%", (optionsDescription.length() > 0 ? optionsDescription.toString() : UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.interact.embed.field.option.not-option"))) , true);
                    }
                }
            }

            event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        }
    }
    private ActionRow createActionRow() {
        StringSelectMenu stringSelectMenu = StringSelectMenu.create("helpcommand")
                .addOptions(
                        SelectOption.of(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.embed.actionRow.server.title"), "server")
                                .withDescription(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.embed.actionRow.server.description"))
                                .withEmoji(Emoji.fromUnicode("🏠")),
                        SelectOption.of(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.embed.actionRow.admin.title"), "admin")
                                .withDescription(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.embed.actionRow.admin.description"))
                                .withEmoji(Emoji.fromUnicode("🔧")),
                        SelectOption.of(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.embed.actionRow.fun.title"), "fun")
                                .withDescription(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.embed.actionRow.fun.description"))
                                .withEmoji(Emoji.fromUnicode("🎉")),
                        SelectOption.of(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.embed.actionRow.music.title"), "music")
                                .withDescription(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.embed.actionRow.music.description"))
                                .withEmoji(Emoji.fromUnicode("🎵")),
                        SelectOption.of(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.embed.actionRow.user.title"), "user")
                                .withDescription(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.embed.actionRow.user.description"))
                                .withEmoji(Emoji.fromUnicode("🔤")),
                        SelectOption.of(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.embed.actionRow.other.title"), "other")
                                .withDescription(UtilsManager.getLangMessage("command/system/help.yml", "help.interact.command.embed.actionRow.other.description"))
                                .withEmoji(Emoji.fromUnicode("❓"))
                ).build();
        return ActionRow.of(stringSelectMenu);

    }
}
