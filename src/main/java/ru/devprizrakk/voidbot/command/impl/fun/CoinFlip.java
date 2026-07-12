package ru.devprizrakk.voidbot.command.impl.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;

import java.util.List;
import java.util.Random;

public class CoinFlip extends BaseCommand {

    private final Random random = new Random();

    @Override
    public String getName() {
        return "coinflip";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("coinflip.description.command"
        );
    }

    @Override
    public List<OptionData> getOptions() {
        //return List.of();
        return null;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public void onExecute() {
        boolean nextBoolean = random.nextBoolean();
        String result;
        if (nextBoolean) {
            result = getLangManager(event).getDescriptionLocale("coinflip.flip.eagle"
            );
        } else {
            result = getLangManager(event).getDescriptionLocale("coinflip.flip.tails"
            );
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(
                getLangManager(event).getDescriptionLocale("coinflip.embed.title"
                )
        );
        embedBuilder.setDescription(
                getLangManager(event).getDescriptionLocale("coinflip.embed.description"
                        )
                        .replace("%coinflip%", result)
        );
        embedBuilder.setFooter(
                getLangManager(event).getDescriptionLocale("coinflip.embed.footer"
                )
        );
        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
