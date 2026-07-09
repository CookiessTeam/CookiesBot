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
import ru.devprizrakk.voidbot.language.LangMessage;
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
        return getLangManager(event).getInfoLocale(
                LangMessage.Commands.Moderation.Ban.FILE,
                LangMessage.Commands.Moderation.Ban.Description.COMMAND
        );
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "target-user", getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Moderation.Ban.FILE,
                LangMessage.Commands.Moderation.Ban.Description.Option.TARGET_USER), true));
        options.add(new OptionData(OptionType.STRING, "reason", getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Moderation.Ban.FILE,
                LangMessage.Commands.Moderation.Ban.Description.Option.REASON), true));
        options.add(new OptionData(OptionType.STRING, "time", getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Moderation.Ban.FILE,
                LangMessage.Commands.Moderation.Ban.Description.Option.TIME), false));
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
            replyError(LangMessage.Commands.Moderation.Ban.Error.OTHER);
            return;
        }

        User author = event.getUser();
        Member targetMember = event.getOption("target-user", OptionMapping::getAsMember);
        User targetUser = event.getOption("target-user", OptionMapping::getAsUser);
        if (targetMember == null || targetUser == null) {
            replyError(LangMessage.Commands.Moderation.Ban.Error.USER_NOT_FOUND);
            return;
        }

        Member selfMember = guild.getSelfMember();
        if (!authorMember.canInteract(targetMember)) {
            replyError(LangMessage.Commands.Moderation.Ban.Error.LowLevelPermission.AUTHOR);
            return;
        }
        if (!selfMember.canInteract(targetMember)) {
            replyError(LangMessage.Commands.Moderation.Ban.Error.LowLevelPermission.BOT);
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
                            failure -> replyError(LangMessage.Commands.Moderation.Ban.Error.OTHER)
                    );
            return;
        }

        ParsedDuration duration = parseDuration(rawTime);
        if (duration == null || duration.totalSeconds <= 0) {
            replyError(LangMessage.Commands.Moderation.Ban.Error.NOT_CORRECTED);
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
                        failure -> replyError(LangMessage.Commands.Moderation.Ban.Error.OTHER)
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
            status = getLangManager(event).getDescriptionLocale(
                    LangMessage.Commands.Moderation.Ban.FILE,
                    LangMessage.Commands.Moderation.Ban.Status.FOREVER
            );
        } else {
            status = getLangManager(event).getDescriptionLocale(
                    LangMessage.Commands.Moderation.Ban.FILE,
                    LangMessage.Commands.Moderation.Ban.Status.TIME
            ).replace("%time%", formatDuration(duration));
        }

        status = status
                .replace("%target-user%", targetUser.getAsMention())
                .replace("%author%", author.getAsMention())
                .replace("%reason%", reason);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255, 104, 0));
        embed.setTitle(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Moderation.Ban.FILE,
                LangMessage.Commands.Moderation.Ban.Embed.TITLE
        ));
        embed.setDescription(getLangManager(event).getDescriptionLocale(
                        LangMessage.Commands.Moderation.Ban.FILE,
                        LangMessage.Commands.Moderation.Ban.Embed.DESCRIPTION
                )
                .replace("%ban-description%", status));
        embed.setFooter(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Moderation.Ban.FILE,
                LangMessage.Commands.Moderation.Ban.Embed.FOOTER
        ));
        event.replyEmbeds(embed.build()).queue();
    }

    private void replyError(String key) {
        new WrongErrorEmbedFactory(event).wrongError(key);
    }

    private String formatDuration(ParsedDuration duration) {
        List<String> parts = new ArrayList<>();
        addTimePart(parts, duration.months,
                LangMessage.Commands.Moderation.Ban.LocalTime.MOUNT,
                LangMessage.Commands.Moderation.Ban.LocalTime.MOUNTS);
        addTimePart(parts, duration.weeks,
                LangMessage.Commands.Moderation.Ban.LocalTime.WEEK,
                LangMessage.Commands.Moderation.Ban.LocalTime.WEEKS);
        addTimePart(parts, duration.days,
                LangMessage.Commands.Moderation.Ban.LocalTime.DAY,
                LangMessage.Commands.Moderation.Ban.LocalTime.DAYS);
        addTimePart(parts, duration.hours,
                LangMessage.Commands.Moderation.Ban.LocalTime.HOUR,
                LangMessage.Commands.Moderation.Ban.LocalTime.HOURS);
        addTimePart(parts, duration.minutes,
                LangMessage.Commands.Moderation.Ban.LocalTime.MINUTE,
                LangMessage.Commands.Moderation.Ban.LocalTime.MINUTES);
        addTimePart(parts, duration.seconds,
                LangMessage.Commands.Moderation.Ban.LocalTime.SECOND,
                LangMessage.Commands.Moderation.Ban.LocalTime.SECONDS);
        return String.join(" ", parts);
    }

    private void addTimePart(List<String> parts, long value, String singularKey, String pluralKey) {
        if (value <= 0) {
            return;
        }

        String unit = value == 1
                ? getLangManager(event).getDescriptionLocale(LangMessage.Commands.Moderation.Ban.FILE, singularKey)
                : getLangManager(event).getDescriptionLocale(LangMessage.Commands.Moderation.Ban.FILE, pluralKey);

        parts.add(value + " " + unit);
    }

    private record ParsedDuration(long totalSeconds, long months, long weeks, long days, long hours, long minutes,
                                  long seconds) {
    }
}
