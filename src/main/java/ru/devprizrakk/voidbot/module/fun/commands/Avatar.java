package ru.devprizrakk.voidbot.module.fun.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.api.command.discord.BaseCommand;
import ru.devprizrakk.voidbot.api.command.discord.CommandCategory;
import ru.devprizrakk.voidbot.api.language.LangMessage;


import java.util.List;
import java.util.Objects;

public class Avatar extends BaseCommand {

    @Override
    public String getName() {
        return "avatar";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale(
                LangMessage.Commands.Fun.Avatar.FILE,
                LangMessage.Commands.Fun.Avatar.Description.COMMAND
        );
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER,
                        "user",
                        getLangManager(event).getInfoLocale(
                                LangMessage.Commands.Fun.Avatar.FILE,
                                LangMessage.Commands.Fun.Avatar.Description.Option.USER),
                        false));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of();
    }

    @Override
    public void onExecute() {
        var user = event.getOption("user") != null ? Objects.requireNonNull(event.getOption("user")).getAsUser() : event.getUser();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (event.getOption("name") == null) {
            embedBuilder.setTitle(
                    getLangManager(event).getDescriptionLocale(
                            LangMessage.Commands.Fun.Avatar.FILE,
                            LangMessage.Commands.Fun.Avatar.EmbedNoMentioned.TITLE
                            ).replace("%user-author%", user.getEffectiveName()));
            embedBuilder.setDescription(
                    getLangManager(event).getDescriptionLocale(
                            LangMessage.Commands.Fun.Avatar.FILE,
                            LangMessage.Commands.Fun.Avatar.EmbedNoMentioned.DESCRIPTION
                    )
            );
            embedBuilder.setImage(user.getAvatarUrl());
            embedBuilder.setFooter(
                    getLangManager(event).getDescriptionLocale(
                            LangMessage.Commands.Fun.Avatar.FILE,
                            LangMessage.Commands.Fun.Avatar.EmbedNoMentioned.FOOTER
                    )
            );
        } else {
            embedBuilder.setTitle(
                    getLangManager(event).getDescriptionLocale(
                            LangMessage.Commands.Fun.Avatar.FILE,
                                    LangMessage.Commands.Fun.Avatar.EmbedMentioned.TITLE
                            ).replace("%user-author%", user.getEffectiveName()));
            embedBuilder.setDescription(
                    getLangManager(event).getDescriptionLocale(
                            LangMessage.Commands.Fun.Avatar.FILE,
                            LangMessage.Commands.Fun.Avatar.EmbedMentioned.DESCRIPTION
                    )
            );
            embedBuilder.setImage(user.getAvatarUrl());
            embedBuilder.setFooter(
                    getLangManager(event).getDescriptionLocale(
                            LangMessage.Commands.Fun.Avatar.FILE,
                            LangMessage.Commands.Fun.Avatar.EmbedMentioned.FOOTER
                    )
            );
        }
        event.replyEmbeds(embedBuilder.build()).queue();

    }
}
