package ru.devprizrakk.voidbot.command.impl.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;
import ru.devprizrakk.voidbot.language.LangMessage;

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
        return getLangManager(event).getInfoLocale(
                LangMessage.Commands.Fun.CoinFlip.FILE,
                LangMessage.Commands.Fun.CoinFlip.Description.COMMAND
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
            result = getLangManager(event).getDescriptionLocale(
                    LangMessage.Commands.Fun.CoinFlip.FILE,
                    LangMessage.Commands.Fun.CoinFlip.Flip.EAGLE
            );
        } else {
            result = getLangManager(event).getDescriptionLocale(
                    LangMessage.Commands.Fun.CoinFlip.FILE,
                    LangMessage.Commands.Fun.CoinFlip.Flip.TAILS
            );
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(
                getLangManager(event).getDescriptionLocale(
                        LangMessage.Commands.Fun.CoinFlip.FILE,
                        LangMessage.Commands.Fun.CoinFlip.Embed.TITLE
                )
        );
        embedBuilder.setDescription(
                getLangManager(event).getDescriptionLocale(
                                LangMessage.Commands.Fun.CoinFlip.FILE,
                                LangMessage.Commands.Fun.CoinFlip.Embed.DESCRIPTION
                        )
                        .replace("%coinflip%", result)
        );
        embedBuilder.setFooter(
                getLangManager(event).getDescriptionLocale(
                        LangMessage.Commands.Fun.CoinFlip.FILE,
                        LangMessage.Commands.Fun.CoinFlip.Embed.FOOTER
                )
        );
        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
