package ru.devprizrakk.voidbot.command.impl.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;
import ru.devprizrakk.voidbot.language.LangMessage;

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
        return getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Rps.FILE, LangMessage.Commands.Fun.Rps.Description.COMMAND);
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING, "choice", getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Rps.FILE, LangMessage.Commands.Fun.Rps.Description.Option.CHOICE), true)
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Rps.FILE, LangMessage.Commands.Fun.Rps.Type.ROCK), "rock")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Rps.FILE, LangMessage.Commands.Fun.Rps.Type.PAPER), "paper")
                .addChoice(getLangManager(event).getInfoLocale(LangMessage.Commands.Fun.Rps.FILE, LangMessage.Commands.Fun.Rps.Type.SCISSORS), "scissors")
        );
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public void onExecute() {
        String[] options = new String[3];
        options[0] = "rock";
        options[1] = "paper";
        options[2] = "scissors";
        String botChoice = options[random.nextInt(options.length)];
        String userChoice = Objects.requireNonNull(event.getOption("choice")).getAsString().toLowerCase();

        String botChoiceLocal;
        String userChoiceLocal;

        switch (botChoice) {
            case "rock" ->
                    botChoiceLocal = getLangManager(event).getDescriptionLocale(LangMessage.Commands.Fun.Rps.FILE, LangMessage.Commands.Fun.Rps.Type.ROCK);
            case "paper" ->
                    botChoiceLocal = getLangManager(event).getDescriptionLocale(LangMessage.Commands.Fun.Rps.FILE, LangMessage.Commands.Fun.Rps.Type.PAPER);
            case "scissors" ->
                    botChoiceLocal = getLangManager(event).getDescriptionLocale(LangMessage.Commands.Fun.Rps.FILE, LangMessage.Commands.Fun.Rps.Type.SCISSORS);
            default -> botChoiceLocal = "undefined";
        }
        switch (userChoice) {
            case "rock" ->
                    userChoiceLocal = getLangManager(event).getDescriptionLocale(LangMessage.Commands.Fun.Rps.FILE, LangMessage.Commands.Fun.Rps.Type.ROCK);
            case "paper" ->
                    userChoiceLocal = getLangManager(event).getDescriptionLocale(LangMessage.Commands.Fun.Rps.FILE, LangMessage.Commands.Fun.Rps.Type.PAPER);
            case "scissors" ->
                    userChoiceLocal = getLangManager(event).getDescriptionLocale(LangMessage.Commands.Fun.Rps.FILE, LangMessage.Commands.Fun.Rps.Type.SCISSORS);
            default -> userChoiceLocal = "undefined";
        }
        if (userChoiceLocal.equals("undefied")) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(getLangManager(event).getDescriptionLocale(LangMessage.Commands.Fun.Rps.FILE, "system.wrong-error.embed.title"));
            embed.setDescription(getLangManager(event).getDescriptionLocale(LangMessage.Commands.Fun.Rps.FILE, "system.wrong-error.embed.description")
                    .replace("%error-code%", getLangManager(event).getDescriptionLocale(LangMessage.Commands.Fun.Rps.FILE, "rps.error.undefied-choice")));
            embed.setFooter(getLangManager(event).getDescriptionLocale(LangMessage.Commands.Fun.Rps.FILE, "system.wrong-error.embed.footer"));
            event.replyEmbeds(embed.build()).queue();
            return;
        }

        String result;
        if (userChoice.equals(botChoice))
            result = getLangManager(event).getDescriptionLocale(LangMessage.Commands.Fun.Rps.FILE, LangMessage.Commands.Fun.Rps.Status.DRAW);
        else if ((userChoice.equals("rock") && botChoice.equals("scissors")) ||
                (userChoice.equals("scissors") && botChoice.equals("paper")) ||
                (userChoice.equals("paper") && botChoice.equals("rock")))
            result = getLangManager(event).getDescriptionLocale(LangMessage.Commands.Fun.Rps.FILE, LangMessage.Commands.Fun.Rps.Status.WIN);
        else
            result = getLangManager(event).getDescriptionLocale(LangMessage.Commands.Fun.Rps.FILE, LangMessage.Commands.Fun.Rps.Status.LOSE);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(
                getLangManager(event).getDescriptionLocale(LangMessage.Commands.Fun.Rps.FILE, LangMessage.Commands.Fun.Rps.Embed.TITLE)
        );
        embedBuilder.setDescription(
                getLangManager(event).getDescriptionLocale(LangMessage.Commands.Fun.Rps.FILE, LangMessage.Commands.Fun.Rps.Embed.DESCRIPTION)
                        .replace("%user-choice%", userChoiceLocal)
                        .replace("%bot-choice%", botChoiceLocal)
                        .replace("%result%", result)
        );
        embedBuilder.setFooter(getLangManager(event).getDescriptionLocale(LangMessage.Commands.Fun.Rps.FILE, LangMessage.Commands.Fun.Rps.Embed.FOOTER));
        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
