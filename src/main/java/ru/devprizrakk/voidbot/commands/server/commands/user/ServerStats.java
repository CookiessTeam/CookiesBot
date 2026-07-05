package ru.devprizrakk.voidbot.commands.server.commands.user;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.core.command.discord.BaseCommand;
import ru.devprizrakk.voidbot.core.command.discord.CommandCategory;
import ru.devprizrakk.voidbot.core.database.model.Experience;
import ru.devprizrakk.voidbot.core.database.model.MemberEventType;
import ru.devprizrakk.voidbot.core.language.LangMessage;
import ru.devprizrakk.voidbot.core.logging.LogType;
import ru.devprizrakk.voidbot.core.logging.Logger;
import ru.devprizrakk.voidbot.core.utils.Utils;

import java.awt.Color;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ServerStats extends BaseCommand {

    @Override
    public String getName() {
        return "serverstats";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale(
                LangMessage.Commands.Server.ServerStats.FILE,
                LangMessage.Commands.Server.ServerStats.Description.COMMAND
        );
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING,
                        "period",
                        getLangManager(event).getInfoLocale(
                                LangMessage.Commands.Server.ServerStats.FILE,
                                LangMessage.Commands.Server.ServerStats.Description.Option.PERIOD),
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
            event.reply("Команда доступна только на сервере.").setEphemeral(true).queue();
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

            List<Experience> top = rm.getExperience().topByGuild(guild.getIdLong(), 5);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(110, 220, 90));
            embed.setTitle("Аналитика сервера «" + guild.getName() + "»");
            embed.setThumbnail(guild.getIconUrl());

            embed.addField("Участники",
                    "Всего: **" + guild.getMemberCount() + "**\n" +
                            "Новых за период: **" + joins + "**\n" +
                            "Вышло: **" + leaves + "**\n" +
                            "Киков: **" + kicks + "**\n" +
                            "Банов: **" + bans + "**\n" +
                            "Разбанов: **" + unbans + "**",
                    true);

            embed.addField("Активность",
                    "Сообщений: **" + messages + "**\n" +
                            "Удалено: **" + deletedMessages + "**\n" +
                            "Войс всего: **" + formatTime(totalVoiceSeconds) + "**\n" +
                            "Сессий войса: **" + rm.getVoiceSessions().countAllSessions() + "**",
                    true);

            embed.addField("Модерация",
                    "Варнов всего: **" + totalWarns + "**\n" +
                            "Активных банов: **" + activeBans + "**\n" +
                            "Активных мутов: **" + activeMutes + "**",
                    true);

            StringBuilder lb = new StringBuilder();
            int rank = 1;
            for (Experience exp : top) {
                Member m = guild.getMemberById(exp.getDiscordId());
                String name = m != null ? m.getEffectiveName() : "<@" + exp.getDiscordId() + ">";
                lb.append(rank++).append(". ").append(name)
                        .append(" — Ур. ").append(exp.getLevel())
                        .append(" (").append(exp.getTotalExperience()).append(" XP)\n");
            }
            if (lb.length() == 0) {
                lb.append("Нет данных");
            }
            embed.addField("Топ по опыту", lb.toString(), false);

            embed.setFooter("Период: " + periodLabel(period) + " | VoidBot");
            event.replyEmbeds(embed.build()).queue();

        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "command", "Failed to build server stats", e);
            event.reply("Не удалось собрать аналитику.").setEphemeral(true).queue();
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
            case "today" -> "сегодня";
            case "week" -> "последние 7 дней";
            default -> "всё время";
        };
    }

    private String formatTime(long seconds) {
        if (seconds <= 0) return "0м";
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long mins = (seconds % 3600) / 60;
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("д ");
        if (hours > 0) sb.append(hours).append("ч ");
        if (mins > 0) sb.append(mins).append("м ");
        if (sb.length() == 0) sb.append(seconds % 60).append("с");
        return sb.toString().trim();
    }
}