package ru.devprizrakk.voidbot.command.impl.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;

import java.util.Random;

public class Joke extends BaseCommand {

    private final Random random = new Random();

    @Override
    public String getName() {
        return "joke";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("joke.description.command"
        );
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public void onExecute() {
        int length = random.nextInt(Integer.parseInt(
                getLangManager(event).getDescriptionLocale("joke.jokes.length"
                )));
        String joke = getLangManager(event).getDescriptionLocale("joke.jokes.%number%".replace("%number%", length + ""));

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(getLangManager(event).getDescriptionLocale("joke.embed.title"
                )
                .replace("%number%", length + ""));
        embedBuilder.setDescription(getLangManager(event).getDescriptionLocale("joke.embed.description"
                )
                .replace("%joke%", joke));
        embedBuilder.setFooter(getLangManager(event).getDescriptionLocale("joke.embed.footer"));

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
