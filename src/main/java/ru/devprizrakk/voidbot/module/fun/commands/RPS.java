package ru.devprizrakk.voidbot.module.fun.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.api.command.discord.BaseCommand;
import ru.devprizrakk.voidbot.api.command.discord.CommandCategory;


import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RPS extends BaseCommand {
    private final Random random = new Random();

    @Override
    public String getName() {
        return "rps";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("command/fun/rps.yml" , "rps.description.command");
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING, "choice", getLangManager(event).getInfoLocale("command/fun/rps.yml", "rps.description.option.choice"), true)
                .addChoice(getLangManager(event).getInfoLocale("command/fun/rps.yml", "rps.type.rock"),"rock")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/rps.yml", "rps.type.paper"),"paper")
                .addChoice(getLangManager(event).getInfoLocale("command/fun/rps.yml", "rps.type.scissors"),"scissors")
        );
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
        String[] options = new String[3];
        options[0] = getLangManager(event).getDescriptionLocale("command/fun/rps.yml", "rps.type.rock");
        options[1] = getLangManager(event).getDescriptionLocale("command/fun/rps.yml", "rps.type.paper");
        options[2] = getLangManager(event).getDescriptionLocale("command/fun/rps.yml", "rps.type.scissors");
        String botChoice = options[random.nextInt(options.length)];
        String userChoice = Objects.requireNonNull(event.getOption("choice")).getAsString().toLowerCase();

        String botChoiceLocal;
        String userChoiceLocal;

        switch (botChoice) {
            case "rock" -> botChoiceLocal = getLangManager(event).getDescriptionLocale("command/fun/rps.yml", "rps.type.rock");
            case "paper" -> botChoiceLocal = getLangManager(event).getDescriptionLocale("command/fun/rps.yml", "rps.type.paper");
            case "scissors" -> botChoiceLocal = getLangManager(event).getDescriptionLocale("command/fun/rps.yml", "rps.type.scissors");
            default -> botChoiceLocal = "undefiled";
        }
        switch (userChoice) {
            case "rock" -> userChoiceLocal = getLangManager(event).getDescriptionLocale("command/fun/rps.yml", "rps.type.rock");
            case "paper" -> userChoiceLocal = getLangManager(event).getDescriptionLocale("command/fun/rps.yml", "rps.type.paper");
            case "scissors" -> userChoiceLocal = getLangManager(event).getDescriptionLocale("command/fun/rps.yml", "rps.type.scissors");
            default -> userChoiceLocal = "undefiled";
        }
        if (userChoiceLocal.equals("undefied")) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(getLangManager(event).getDescriptionLocale("command/fun/rps.yml", "system.wrong-error.embed.title"));
            embed.setDescription(getLangManager(event).getDescriptionLocale("command/fun/rps.yml", "system.wrong-error.embed.description")
                    .replace("%error-code%", getLangManager(event).getDescriptionLocale("command/fun/rps.yml", "rps.error.undefied-choice")));
            embed.setFooter(getLangManager(event).getDescriptionLocale("command/fun/rps.yml", "system.wrong-error.embed.footer"));
            event.replyEmbeds(embed.build()).queue();
            return;
        }

        String result;
        if (userChoice.equals(botChoice)) result = getLangManager(event).getDescriptionLocale("command/fun/rps.yml", "rps.status.draw");
        else if ((userChoice.equals("rock") && botChoice.equals("scissors")) ||
                (userChoice.equals("scissors") && botChoice.equals("paper")) ||
                (userChoice.equals("paper") && botChoice.equals("rock")))
            result = getLangManager(event).getDescriptionLocale("command/fun/rps.yml", "rps.status.win");
        else
            result = getLangManager(event).getDescriptionLocale("command/fun/rps.yml", "rps.status.lose");

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(
                getLangManager(event).getDescriptionLocale("command/fun/rps.yml", "rps.embed.title")
        );
        embedBuilder.setDescription(
                getLangManager(event).getDescriptionLocale("command/fun/rps.yml", "rps.embed.description")
                        .replace("%user-choice%", userChoiceLocal)
                        .replace("%bot-choice%", botChoiceLocal)
                        .replace("%result%", result)
        );
        embedBuilder.setFooter(getLangManager(event).getDescriptionLocale("command/fun/rps.yml", "rps.embed.footer"));
        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
