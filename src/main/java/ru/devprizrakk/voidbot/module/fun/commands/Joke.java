package ru.devprizrakk.voidbot.module.fun.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.api.command.discord.BaseCommand;
import ru.devprizrakk.voidbot.api.command.discord.CommandCategory;


import java.util.List;
import java.util.Random;

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
        //return List.of();
        return null;
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
