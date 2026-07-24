package ru.devprizrakk.voidbot.command.impl.server.moderation.subcommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.command.api.BaseSubCommand;
import ru.devprizrakk.voidbot.database.model.BanModel;
import ru.devprizrakk.voidbot.exceptions.discord.WrongErrorEmbedFactory;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ban extends BaseSubCommand {

    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)\\s*(mount|month|mon|w|week|d|day|h|hr|hour|m|min|minute|s|sec|second)", Pattern.CASE_INSENSITIVE);

    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("ban.description.command"
        );
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "target-user", getLangManager(event).getDescriptionLocale("ban.description.option.target-user"), true));
        options.add(new OptionData(OptionType.STRING, "reason", getLangManager(event).getDescriptionLocale("ban.description.option.reason"), true));
        options.add(new OptionData(OptionType.STRING, "time", getLangManager(event).getDescriptionLocale("ban.description.option.time"), false));
        return options;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of(Permission.BAN_MEMBERS);
    }

    @Override
    public void onExecute() {
        Guild guild = event.getGuild();
        Member authorMember = event.getMember();
        if (guild == null || authorMember == null) {
            replyError("ban.error.other");
            return;
        }

        User author = event.getUser();
        Member targetMember = event.getOption("target-user", OptionMapping::getAsMember);
        User targetUser = event.getOption("target-user", OptionMapping::getAsUser);
        if (targetMember == null || targetUser == null) {
            replyError("ban.error.user-not-found");
            return;
        }

        Member selfMember = guild.getSelfMember();
        if (!authorMember.canInteract(targetMember)) {
            replyError("ban.error.low-level-permission.author");
            return;
        }
        if (!selfMember.canInteract(targetMember)) {
            replyError("ban.error.low-level-permission.bot");
            return;
        }

        String reason = event.getOption("reason", OptionMapping::getAsString);
        if (reason == null || reason.isBlank()) {
            reason = "-";
        }

        String rawTime = event.getOption("time", OptionMapping::getAsString);
        if (rawTime == null || rawTime.isBlank()) {
            String finalReason = reason;
            guild.ban(targetUser, 0, TimeUnit.DAYS)
                    .reason(reason)
                    .queue(
                            success -> {
                                persistBan(targetUser.getIdLong(), author.getIdLong(), finalReason, null);
                                replySuccess(author, targetUser, finalReason, null);
                            },
                            failure -> replyError("ban.error.other")
                    );
            return;
        }

        ParsedDuration duration = parseDuration(rawTime);
        if (duration == null || duration.totalSeconds <= 0) {
            replyError("ban.error.not-corrected");
            return;
        }

        String finalReason = reason;
        guild.ban(targetUser, 0, TimeUnit.DAYS)
                .reason(reason)
                .queue(
                        success -> {
                            event.getJDA().getRateLimitPool().schedule(
                                    () -> guild.unban(targetUser).reason("Temporary ban expired").queue(),
                                    duration.totalSeconds,
                                    TimeUnit.SECONDS
                            );
                            Date expiresAt = new Date(System.currentTimeMillis() + duration.totalSeconds * 1000L);
                            persistBan(targetUser.getIdLong(), author.getIdLong(), finalReason, expiresAt);
                            replySuccess(author, targetUser, finalReason, duration);
                        },
                        failure -> replyError("ban.error.other")
                );
    }

    private void persistBan(long targetId, long moderatorId, String reason, java.util.Date expiresAt) {
        try {
            BanModel ban = new BanModel(0, targetId, moderatorId, reason, new Date(), expiresAt);
            Utils.getDatabaseManager().getRepositoryManager().getBans().save(ban);
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "MOD", "Failed to persist ban", e);
        }
    }

    private ParsedDuration parseDuration(String input) {
        String normalized = input.toLowerCase().replaceAll("\\s+", "");
        Matcher matcher = DURATION_PATTERN.matcher(normalized);
        int consumed = 0;
        long totalSeconds = 0L;
        long months = 0L;
        long weeks = 0L;
        long days = 0L;
        long hours = 0L;
        long minutes = 0L;
        long seconds = 0L;

        while (matcher.find()) {
            if (matcher.start() != consumed) {
                return null;
            }
            consumed = matcher.end();

            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();

            switch (unit) {
                case "mount", "month", "mon" -> {
                    months += value;
                    totalSeconds += value * 30L * 24L * 60L * 60L;
                }
                case "w", "week" -> {
                    weeks += value;
                    totalSeconds += value * 7L * 24L * 60L * 60L;
                }
                case "d", "day" -> {
                    days += value;
                    totalSeconds += value * 24L * 60L * 60L;
                }
                case "h", "hr", "hour" -> {
                    hours += value;
                    totalSeconds += value * 60L * 60L;
                }
                case "m", "min", "minute" -> {
                    minutes += value;
                    totalSeconds += value * 60L;
                }
                case "s", "sec", "second" -> {
                    seconds += value;
                    totalSeconds += value;
                }
                default -> {
                    return null;
                }
            }
        }

        if (consumed != normalized.length() || totalSeconds <= 0) {
            return null;
        }
        return new ParsedDuration(totalSeconds, months, weeks, days, hours, minutes, seconds);
    }

    private void replySuccess(User author, User targetUser, String reason, ParsedDuration duration) {
        String status;
        if (duration == null) {
            status = getLangManager(event).getDescriptionLocale("ban.status.forever"
            );
        } else {
            status = getLangManager(event).getDescriptionLocale("ban.status.time"
            ).replace("%time%", formatDuration(duration));
        }

        status = status
                .replace("%target-user%", targetUser.getAsMention())
                .replace("%author%", author.getAsMention())
                .replace("%reason%", reason);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255, 104, 0));
        embed.setTitle(getLangManager(event).getDescriptionLocale("ban.embed.title"
        ));
        embed.setDescription(getLangManager(event).getDescriptionLocale("ban.embed.description"
                )
                .replace("%ban-description%", status));
        embed.setFooter(getLangManager(event).getDescriptionLocale("ban.embed.footer"
        ));
        event.replyEmbeds(embed.build()).queue();
    }

    private void replyError(String key) {
        new WrongErrorEmbedFactory(event).wrongError(key);
    }

    private String formatDuration(ParsedDuration duration) {
        List<String> parts = new ArrayList<>();
        addTimePart(parts, duration.months,
                "ban.local-time.mount",
                "ban.local-time.mounts");
        addTimePart(parts, duration.weeks,
                "ban.local-time.week",
                "ban.local-time.weeks");
        addTimePart(parts, duration.days,
                "ban.local-time.day",
                "ban.local-time.days");
        addTimePart(parts, duration.hours,
                "ban.local-time.hour",
                "ban.local-time.hours");
        addTimePart(parts, duration.minutes,
                "ban.local-time.minute",
                "ban.local-time.minutes");
        addTimePart(parts, duration.seconds,
                "ban.local-time.second",
                "ban.local-time.seconds");
        return String.join(" ", parts);
    }

    private void addTimePart(List<String> parts, long value, String singularKey, String pluralKey) {
        if (value <= 0) {
            return;
        }

        String unit = value == 1
                ? getLangManager(event).getDescriptionLocale(singularKey)
                : getLangManager(event).getDescriptionLocale(pluralKey);

        parts.add(value + " " + unit);
    }

    private record ParsedDuration(long totalSeconds, long months, long weeks, long days, long hours, long minutes,
                                  long seconds) {
    }
}
