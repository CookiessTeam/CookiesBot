package ru.devprizrakk.voidbot.command.impl.server.system;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;
import ru.devprizrakk.voidbot.database.model.ExperienceModel;
import ru.devprizrakk.voidbot.database.model.MemberEventType;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

import java.awt.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

public class ServerStats extends BaseCommand {

    @Override
    public String getName() {
        return "serverstats";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("serverstats.description.command"
        );
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING,
                        "period",
                        getLangManager(event).getInfoLocale("serverstats.description.option.period"),
                        false)
                        .addChoice("today", "today")
                        .addChoice("week", "week")
                        .addChoice("all", "all")
        );
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SERVER;
    }

    @Override
    public void onExecute() {
        Guild guild = event.getGuild();
        if (guild == null) {
            event.reply(getLangManager(event).getInfoLocale("system.guild-only")).setEphemeral(true).queue();
            return;
        }

        String period = event.getOption("period") != null
                ? event.getOption("period").getAsString()
                : "all";

        Timestamp since = periodStart(period);

        try {
            var rm = Utils.getDatabaseManager().getRepositoryManager();

            long joins = rm.getMemberEvents().countByTypeSince(MemberEventType.JOIN, since);
            long leaves = rm.getMemberEvents().countByTypeSince(MemberEventType.LEAVE, since);
            long bans = rm.getMemberEvents().countByTypeSince(MemberEventType.BAN, since);
            long kicks = rm.getMemberEvents().countByTypeSince(MemberEventType.KICK, since);
            long unbans = rm.getMemberEvents().countByTypeSince(MemberEventType.UNBAN, since);

            long messages;
            long deletedMessages;
            long totalVoiceSeconds;
            long totalWarns;
            long activeBans;
            long activeMutes;

            if ("all".equals(period)) {
                messages = rm.getMessages().countAll();
                deletedMessages = rm.getMessages().countDeleted();
                totalVoiceSeconds = rm.getVoiceSessions().sumAllDurationSeconds();
                totalWarns = rm.getWarns().countAll();
                activeBans = rm.getBans().countActive();
                activeMutes = rm.getMutes().countActive();
            } else {
                messages = rm.getMessages().countSince(since);
                deletedMessages = 0L;
                totalVoiceSeconds = 0L;
                totalWarns = 0L;
                activeBans = rm.getBans().countActive();
                activeMutes = rm.getMutes().countActive();
            }

            List<ExperienceModel> top = rm.getExperience().getTopByGuild(guild.getIdLong(), 5);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(110, 220, 90));
            embed.setTitle(getLangManager(event).getInfoLocale("serverstats.embed.title").replace("%server-name%", guild.getName()));
            embed.setThumbnail(guild.getIconUrl());

            embed.addField(getLangManager(event).getInfoLocale("serverstats.field.members-title"),
                    getLangManager(event).getInfoLocale("serverstats.field.members-total").replace("%count%", String.valueOf(guild.getMemberCount())) +
                            getLangManager(event).getInfoLocale("serverstats.field.members-new").replace("%count%", String.valueOf(joins)) +
                            getLangManager(event).getInfoLocale("serverstats.field.members-left").replace("%count%", String.valueOf(leaves)) +
                            getLangManager(event).getInfoLocale("serverstats.field.members-kicks").replace("%count%", String.valueOf(kicks)) +
                            getLangManager(event).getInfoLocale("serverstats.field.members-bans").replace("%count%", String.valueOf(bans)) +
                            getLangManager(event).getInfoLocale("serverstats.field.members-unbans").replace("%count%", String.valueOf(unbans)),
                    true);

            embed.addField(getLangManager(event).getInfoLocale("serverstats.field.activity-title"),
                    getLangManager(event).getInfoLocale("serverstats.field.activity-messages").replace("%count%", String.valueOf(messages)) +
                            getLangManager(event).getInfoLocale("serverstats.field.activity-deleted").replace("%count%", String.valueOf(deletedMessages)) +
                            getLangManager(event).getInfoLocale("serverstats.field.activity-voice").replace("%count%", formatTime(totalVoiceSeconds)) +
                            getLangManager(event).getInfoLocale("serverstats.field.activity-sessions").replace("%count%", String.valueOf(rm.getVoiceSessions().countAllSessions())),
                    true);

            embed.addField(getLangManager(event).getInfoLocale("serverstats.field.moderation-title"),
                    getLangManager(event).getInfoLocale("serverstats.field.moderation-warns").replace("%count%", String.valueOf(totalWarns)) +
                            getLangManager(event).getInfoLocale("serverstats.field.moderation-active-bans").replace("%count%", String.valueOf(activeBans)) +
                            getLangManager(event).getInfoLocale("serverstats.field.moderation-active-mutes").replace("%count%", String.valueOf(activeMutes)),
                    true);

            StringBuilder lb = new StringBuilder();
            int rank = 1;
            for (ExperienceModel exp : top) {
                Member m = guild.getMemberById(exp.getDiscordId());
                String name = m != null ? m.getEffectiveName() : "<@" + exp.getDiscordId() + ">";
                lb.append(getLangManager(event).getInfoLocale("serverstats.field.leaderboard-entry")
                        .replace("%rank%", String.valueOf(rank))
                        .replace("%name%", name)
                        .replace("%level%", String.valueOf(exp.getLevel()))
                        .replace("%xp%", String.valueOf(exp.getTotalExperience()))).append("\n");
                rank++;
            }
            if (lb.isEmpty()) {
                lb.append(getLangManager(event).getInfoLocale("serverstats.field.leaderboard-empty"));
            }
            embed.addField(getLangManager(event).getInfoLocale("serverstats.field.leaderboard-title"), lb.toString(), false);

            embed.setFooter(getLangManager(event).getInfoLocale("serverstats.footer").replace("%period%", periodLabel(period)));
            event.replyEmbeds(embed.build()).queue();

        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "command", "Failed to build server stats", e);
            event.reply(getLangManager(event).getInfoLocale("serverstats.error.render")).setEphemeral(true).queue();
        }
    }

    private Timestamp periodStart(String period) {
        Calendar cal = Calendar.getInstance();
        switch (period) {
            case "today" -> {
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
            }
            case "week" -> cal.add(Calendar.DAY_OF_YEAR, -7);
            default -> {
                return new Timestamp(0);
            }
        }
        return new Timestamp(cal.getTimeInMillis());
    }

    private String periodLabel(String period) {
        return switch (period) {
            case "today" -> getLangManager(event).getInfoLocale("serverstats.period.today");
            case "week" -> getLangManager(event).getInfoLocale("serverstats.period.week");
            default -> getLangManager(event).getInfoLocale("serverstats.period.all");
        };
    }

    private String formatTime(long seconds) {
        if (seconds <= 0) return "0" + ru.devprizrakk.voidbot.language.LangManager.get("ru", "system.time.minute");
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long mins = (seconds % 3600) / 60;
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append(ru.devprizrakk.voidbot.language.LangManager.get("ru", "system.time.day")).append(" ");
        if (hours > 0) sb.append(hours).append(ru.devprizrakk.voidbot.language.LangManager.get("ru", "system.time.hour")).append(" ");
        if (mins > 0) sb.append(mins).append(ru.devprizrakk.voidbot.language.LangManager.get("ru", "system.time.minute")).append(" ");
        if (sb.isEmpty()) sb.append(seconds % 60).append(ru.devprizrakk.voidbot.language.LangManager.get("ru", "system.time.second"));
        return sb.toString().trim();
    }
}