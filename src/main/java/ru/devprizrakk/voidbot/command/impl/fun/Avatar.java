package ru.devprizrakk.voidbot.command.impl.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;

import java.util.List;
import java.util.Objects;

public class Avatar extends BaseCommand {

    @Override
    public String getName() {
        return "avatar";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("avatar.description.command"
        );
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER,
                        "user",
                        getLangManager(event).getInfoLocale("avatar.description.option.user"),
                        false));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public void onExecute() {
        var user = event.getOption("user") != null ? Objects.requireNonNull(event.getOption("user")).getAsUser() : event.getUser();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (event.getOption("name") == null) {
            embedBuilder.setTitle(
                    getLangManager(event).getDescriptionLocale("avatar.embed-no-mentioned.title"
                    ).replace("%user-author%", user.getEffectiveName()));
            embedBuilder.setDescription(
                    getLangManager(event).getDescriptionLocale("avatar.embed-no-mentioned.description"
                    )
            );
            embedBuilder.setImage(user.getAvatarUrl());
            embedBuilder.setFooter(
                    getLangManager(event).getDescriptionLocale("avatar.embed-no-mentioned.footer"
                    )
            );
        } else {
            embedBuilder.setTitle(
                    getLangManager(event).getDescriptionLocale("avatar.embed-mentioned.title"
                    ).replace("%user-author%", user.getEffectiveName()));
            embedBuilder.setDescription(
                    getLangManager(event).getDescriptionLocale("avatar.embed-mentioned.description"
                    )
            );
            embedBuilder.setImage(user.getAvatarUrl());
            embedBuilder.setFooter(
                    getLangManager(event).getDescriptionLocale("avatar.embed-mentioned.footer"
                    )
            );
        }
        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
