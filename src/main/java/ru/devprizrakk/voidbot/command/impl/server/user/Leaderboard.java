package ru.devprizrakk.voidbot.command.impl.server.user;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;
import ru.devprizrakk.voidbot.events.LeaderboardListener;

import java.awt.Color;
import java.util.List;

public class Leaderboard extends BaseCommand {

    @Override
    public String getName() {
        return "leaderboard";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("leaderboard.description.command");
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "sort",
                        getLangManager(event).getInfoLocale("leaderboard.description.option.sort"), false)
                        .addChoice("Total XP", "xp")
                        .addChoice("Level", "level")
                        .addChoice("Voice Time", "voice")
        );
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SERVER;
    }

    @Override
    public void onExecute() {
        if (event.getGuild() == null) {
            event.reply(getLangManager(event).getInfoLocale("system.guild-only")).setEphemeral(true).queue();
            return;
        }

        String sort = event.getOption("sort") != null ? event.getOption("sort").getAsString() : "xp";
        long guildId = event.getGuild().getIdLong();

        EmbedBuilder embed = LeaderboardListener.buildPage(event.getGuild(), sort, 0);
        long total = LeaderboardListener.getTotalEntries(guildId, sort);
        int pages = (int) Math.max(1, Math.ceil((double) total / LeaderboardListener.PAGE_SIZE));

        Button prev = Button.secondary("lb_prev:" + sort + ":0", "◀")
                .withDisabled(true);
        Button next = Button.secondary("lb_next:" + sort + ":1", "▶")
                .withDisabled(pages <= 1);
        Button refresh = Button.primary("lb_refresh:" + sort + ":0", "↻")
                .withEmoji(Emoji.fromUnicode("🔄"));

        event.replyEmbeds(embed.build())
                .addComponents(ActionRow.of(prev, refresh, next))
                .queue();
    }
}