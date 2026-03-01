package ru.devprizrakk.voidbot.module.fun.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.api.command.discord.BaseCommand;
import ru.devprizrakk.voidbot.api.command.discord.CommandCategory;


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
        return getLangManager(event).getInfoLocale("command/fun/coinflip.yml", "coinflip.description.command");
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
        boolean nextBoolean = random.nextBoolean();
        String result;
        if (nextBoolean) {
            result = getLangManager(event).getDescriptionLocale("command/fun/coinflip.yml", "coinflip.flip.eagle");
        } else {
            result = getLangManager(event).getDescriptionLocale("command/fun/coinflip.yml", "coinflip.flip.tails");
        }
        event.reply("Результат подбрасывания: **" + result + "**").queue();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(
                getLangManager(event).getDescriptionLocale("command/fun/coinflip.yml", "coinflip.embed.title")
        );
        embedBuilder.setDescription(
                getLangManager(event).getDescriptionLocale("command/fun/coinflip.yml", "coinflip.embed.description")
                        .replace("%coinflip%", result)
        );
        embedBuilder.setFooter(
                getLangManager(event).getDescriptionLocale("command/fun/coinflip.yml", "coinflip.embed.footer")
        );
        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
