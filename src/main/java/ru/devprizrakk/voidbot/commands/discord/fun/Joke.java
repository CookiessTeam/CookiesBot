package ru.devprizrakk.voidbot.commands.discord.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import ru.devprizrakk.voidbot.core.loader.discord.loader.BaseCommand;
import ru.devprizrakk.voidbot.core.loader.discord.loader.CommandCategory;

import java.util.List;
import java.util.Random;

import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.Permission;

public class Joke extends BaseCommand {
    private final Random random = new Random();

    @Override
    public String getName() {
        return "joke";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("command/fun/joke.yml", "joke.description.command");
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
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
        int length = random.nextInt(Integer.parseInt(getLangManager(event).getDescriptionLocale("command/fun/joke.yml", "joke.joke.length")));
        String joke = getLangManager(event).getDescriptionLocale("command/fun/joke.yml", "joke.joke." + length);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(getLangManager(event).getDescriptionLocale("command/fun/joke.yml", "joke.embed.title")
                .replace("%number%", length + ""));
        embedBuilder.setDescription(getLangManager(event).getDescriptionLocale("command/fun/joke.yml", "joke.embed.description")
                .replace("%joke%", joke));
        embedBuilder.setFooter(getLangManager(event).getDescriptionLocale("command/fun/joke.yml", "joke.embed.footer"));

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
