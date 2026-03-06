package ru.devprizrakk.voidbot.commands.fun.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.core.command.discord.BaseCommand;
import ru.devprizrakk.voidbot.core.command.discord.CommandCategory;
import ru.devprizrakk.voidbot.core.language.LangMessage;


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
        return getLangManager(event).getInfoLocale(
                LangMessage.Commands.Fun.Joke.FILE, LangMessage.Commands.Fun.Joke.Description.COMMAND
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
    public List<Permission> getRequiredPermissions() {
        return List.of();
    }

    @Override
    public void onExecute() {
        int length = random.nextInt(Integer.parseInt(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Fun.Joke.FILE, LangMessage.Commands.Fun.Joke.Jokes.LENGTH)));
        String joke = getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Fun.Joke.FILE, LangMessage.Commands.Fun.Joke.Jokes.JOKE_STRING.replace("%number%", length + ""));
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Fun.Joke.FILE, LangMessage.Commands.Fun.Joke.Embed.TITLE)
                .replace("%number%", length + ""));
        embedBuilder.setDescription(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Fun.Joke.FILE, LangMessage.Commands.Fun.Joke.Embed.DESCRIPTION)
                .replace("%joke%", joke));
        embedBuilder.setFooter(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Fun.Joke.FILE, LangMessage.Commands.Fun.Joke.Embed.FOOTER));

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
