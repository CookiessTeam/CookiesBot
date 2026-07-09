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
import ru.devprizrakk.voidbot.database.model.MuteModel;
import ru.devprizrakk.voidbot.exceptions.discord.WrongErrorEmbedFactory;
import ru.devprizrakk.voidbot.language.LangMessage;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mute extends BaseSubCommand {

    private static final long MAX_TIMEOUT_SECONDS = Duration.ofDays(27).plusHours(23).plusMinutes(59).getSeconds();
    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)\\s*(mount|month|mon|w|week|d|day|h|hr|hour|m|min|minute|s|sec|second)", Pattern.CASE_INSENSITIVE);

    @Override
    public String getName() {
        return "mute";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale(
                LangMessage.Commands.Moderation.Mute.FILE,
                LangMessage.Commands.Moderation.Mute.Description.COMMAND
        );
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "target-user", getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Moderation.Mute.FILE,
                LangMessage.Commands.Moderation.Mute.Description.Option.TARGET_USER), true));
        options.add(new OptionData(OptionType.STRING, "reason", getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Moderation.Mute.FILE,
                LangMessage.Commands.Moderation.Mute.Description.Option.REASON), true));
        options.add(new OptionData(OptionType.STRING, "time", getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Moderation.Mute.FILE,
                LangMessage.Commands.Moderation.Mute.Description.Option.TIME), false));
        return options;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of(Permission.MODERATE_MEMBERS);
    }

    @Override
    public void onExecute() {
        Guild guild = event.getGuild();
        Member authorMember = event.getMember();
        if (guild == null || authorMember == null) {
            replyOther("GUILD_OR_MEMBER_NULL");
            return;
        }

        User author = event.getUser();
        Member targetMember = event.getOption("target-user", OptionMapping::getAsMember);
        User targetUser = event.getOption("target-user", OptionMapping::getAsUser);
        if (targetMember == null || targetUser == null) {
            replyError(LangMessage.Commands.Moderation.Mute.Error.USER_NOT_FOUND);
            return;
        }

        Member selfMember = guild.getSelfMember();
        if (!authorMember.canInteract(targetMember)) {
            replyError(LangMessage.Commands.Moderation.Mute.Error.LowLevelPermission.AUTHOR);
            return;
        }
        if (!selfMember.canInteract(targetMember)) {
            replyError(LangMessage.Commands.Moderation.Mute.Error.LowLevelPermission.BOT);
            return;
        }

        String reason = event.getOption("reason", OptionMapping::getAsString);
        if (reason == null || reason.isBlank()) {
            reason = "-";
        }

        String rawTime = event.getOption("time", OptionMapping::getAsString);
        if (rawTime == null || rawTime.isBlank()) {
            String finalReason = reason;
            targetMember.timeoutFor(Duration.ofSeconds(MAX_TIMEOUT_SECONDS))
                    .reason(reason)
                    .queue(
                            success -> {
                                persistMute(targetUser.getIdLong(), author.getIdLong(), finalReason, null);
                                replySuccess(author, targetUser, finalReason, null);
                            },
                            failure -> replyOther("TIMEOUT_FOREVER_FAILED", failure)
                    );
            return;
        }

        ParsedDuration duration = parseDuration(rawTime);
        if (duration == null || duration.totalSeconds <= 0) {
            replyError(LangMessage.Commands.Moderation.Mute.Error.NOT_CORRECTED);
            return;
        }
        if (duration.totalSeconds > MAX_TIMEOUT_SECONDS) {
            replyOther("TIME_EXCEEDS_28_DAYS");
            return;
        }

        String finalReason = reason;
        Date expiresAt = new Date(System.currentTimeMillis() + duration.totalSeconds * 1000L);
        targetMember.timeoutFor(Duration.ofSeconds(duration.totalSeconds))
                .reason(reason)
                .queue(
                        success -> {
                            persistMute(targetUser.getIdLong(), author.getIdLong(), finalReason, expiresAt);
                            replySuccess(author, targetUser, finalReason, duration);
                        },
                        failure -> replyOther("TIMEOUT_FAILED", failure)
                );
    }

    private void persistMute(long targetId, long moderatorId, String reason, Date expiresAt) {
        try {
            MuteModel mute = new MuteModel(0, targetId, moderatorId, reason, new Date(), expiresAt);
            Utils.getDatabaseManager().getRepositoryManager().getMutes().save(mute);
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "MOD", "Failed to persist mute", e);
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
                    LangMessage.Commands.Moderation.Mute.FILE,
                    LangMessage.Commands.Moderation.Mute.Status.FOREVER
            );
        } else {
            status = getLangManager(event).getDescriptionLocale(
                    LangMessage.Commands.Moderation.Mute.FILE,
                    LangMessage.Commands.Moderation.Mute.Status.TIME
            ).replace("%time%", formatDuration(duration));
        }

        status = status
                .replace("%target-user%", targetUser.getAsMention())
                .replace("%author%", author.getAsMention())
                .replace("%reason%", reason);

        String embedDescription = getLangManager(event).getDescriptionLocale(
                        LangMessage.Commands.Moderation.Mute.FILE,
                        LangMessage.Commands.Moderation.Mute.Embed.DESCRIPTION
                ).replace("%mute-description%", status)
                .replace("%mute-description", status);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255, 104, 0));
        embed.setTitle(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Moderation.Mute.FILE,
                LangMessage.Commands.Moderation.Mute.Embed.TITLE
        ));
        embed.setDescription(embedDescription);
        embed.setFooter(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Moderation.Mute.FILE,
                LangMessage.Commands.Moderation.Mute.Embed.FOOTER
        ));
        event.replyEmbeds(embed.build()).queue();
    }

    private void replyError(String key) {
        new WrongErrorEmbedFactory(event).
                wrongError(getLangManager(event).
                        getDescriptionLocale(
                                LangMessage.Commands.Moderation.Mute.FILE,
                                key
                        ));
    }

    private void replyOther(String code) {
        new WrongErrorEmbedFactory(event).wrongError(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Moderation.Mute.FILE,
                LangMessage.Commands.Moderation.Mute.Error.OTHER).replace("%code%", code));
    }

    private void replyOther(String code, Throwable throwable) {
        String detail = throwable == null
                ? "unknown"
                : throwable.getClass().getSimpleName() +
                (throwable.getMessage() == null || throwable.getMessage().isBlank() ? "" : ": " + throwable.getMessage());
        replyOther(code + " | " + detail);
    }

    private String formatDuration(ParsedDuration duration) {
        List<String> parts = new ArrayList<>();
        addTimePart(parts, duration.months,
                LangMessage.Commands.Moderation.Mute.LocalTime.MOUNT,
                LangMessage.Commands.Moderation.Mute.LocalTime.MOUNTS);
        addTimePart(parts, duration.weeks,
                LangMessage.Commands.Moderation.Mute.LocalTime.WEEK,
                LangMessage.Commands.Moderation.Mute.LocalTime.WEEKS);
        addTimePart(parts, duration.days,
                LangMessage.Commands.Moderation.Mute.LocalTime.DAY,
                LangMessage.Commands.Moderation.Mute.LocalTime.DAYS);
        addTimePart(parts, duration.hours,
                LangMessage.Commands.Moderation.Mute.LocalTime.HOUR,
                LangMessage.Commands.Moderation.Mute.LocalTime.HOURS);
        addTimePart(parts, duration.minutes,
                LangMessage.Commands.Moderation.Mute.LocalTime.MINUTE,
                LangMessage.Commands.Moderation.Mute.LocalTime.MINUTES);
        addTimePart(parts, duration.seconds,
                LangMessage.Commands.Moderation.Mute.LocalTime.SECOND,
                LangMessage.Commands.Moderation.Mute.LocalTime.SECONDS);
        return String.join(" ", parts);
    }

    private void addTimePart(List<String> parts, long value, String singularKey, String pluralKey) {
        if (value <= 0) {
            return;
        }

        String unit = value == 1
                ? getLangManager(event).getDescriptionLocale(LangMessage.Commands.Moderation.Mute.FILE, singularKey)
                : getLangManager(event).getDescriptionLocale(LangMessage.Commands.Moderation.Mute.FILE, pluralKey);

        parts.add(value + " " + unit);
    }

    private record ParsedDuration(long totalSeconds, long months, long weeks, long days, long hours, long minutes,
                                  long seconds) {
    }
}
