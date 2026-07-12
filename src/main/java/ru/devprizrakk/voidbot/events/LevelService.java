package ru.devprizrakk.voidbot.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import ru.devprizrakk.voidbot.database.model.ExperienceModel;
import ru.devprizrakk.voidbot.database.repository.ExperienceRepository;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class LevelService {
    private LevelService() {
    }

    private static long baseXp() {
        return Utils.getConfig().getLong("levels.base-xp", 100L);
    }

    private static double growth() {
        return Utils.getConfig().getDouble("levels.growth", 1.2D);
    }

    private static long xpPerMessage() {
        return Utils.getConfig().getLong("levels.xp-per-message", 5L);
    }

    private static long xpPerVoiceMinute() {
        return Utils.getConfig().getLong("levels.xp-per-voice-minute", 5L);
    }

    public static long requiredXpForLevel(long level) {
        return Math.max(1L, (long) Math.floor(baseXp() * Math.pow(growth(), level)));
    }

    private static ExperienceRepository repo() {
        return Utils.getDatabaseManager().getRepositoryManager().getExperience();
    }

    public static void addExperience(User user, Guild guild, long amount) {
        if (user == null || user.isBot() || guild == null || amount <= 0) {
            return;
        }

        long discordId = user.getIdLong();
        long guildId = guild.getIdLong();

        try {
            ExperienceModel exp = repo().findByDiscordIdAndGuild(discordId, guildId)
                    .orElseGet(() -> new ExperienceModel(0, discordId, guildId, 0, 0, 0, null));

            long startLevel = exp.getLevel();
            exp.setExperience(exp.getExperience() + amount);
            exp.setTotalExperience(exp.getTotalExperience() + amount);
            exp.setUpdatedAt(now());

            while (exp.getExperience() >= requiredXpForLevel(exp.getLevel())) {
                exp.setExperience(exp.getExperience() - requiredXpForLevel(exp.getLevel()));
                exp.setLevel(exp.getLevel() + 1);
            }

            repo().save(exp);

            if (exp.getLevel() > startLevel) {
                notifyLevelUp(user, guild, exp);
            }
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "LEVEL", "Failed to add experience", e);
        }
    }

    public static void rewardMessage(User user, Guild guild) {
        addExperience(user, guild, xpPerMessage());
    }

    public static void rewardVoiceMinutes(User user, Guild guild, long minutes) {
        if (minutes <= 0) {
            return;
        }
        addExperience(user, guild, minutes * xpPerVoiceMinute());
    }

    private static void notifyLevelUp(User user, Guild guild, ExperienceModel exp) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("server-name", guild.getName());
        replacements.put("level", String.valueOf(exp.getLevel()));
        replacements.put("required-experience", String.valueOf(requiredXpForLevel(exp.getLevel())));

        String title = Utils.getLangManager().getInfoLocale(
                "level.up.embed.title", replacements);
        String description = Utils.getLangManager().getInfoLocale(
                "level.up.embed.description", replacements);
        String footer = Utils.getLangManager().getInfoLocale(
                "level.up.embed.footer", replacements);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.setDescription(description);
        embed.setFooter(footer);

        String channelId = Utils.getConfig().getString("levels.channel", "");
        if (channelId != null && !channelId.isEmpty()) {
            TextChannel channel = guild.getTextChannelById(channelId);
            if (channel != null) {
                channel.sendMessage(user.getAsMention()).setEmbeds(embed.build()).queue();
                return;
            }
        }

        user.openPrivateChannel().queue(
                pc -> pc.sendMessageEmbeds(embed.build()).queue(
                        _ -> {
                        },
                        _ -> {
                        }
                ),
                _ -> {
                }
        );
    }

    private static Timestamp now() {
        return new Timestamp(new Date().getTime());
    }

    public static Optional<ExperienceModel> get(User user, Guild guild) {
        if (user == null || guild == null) {
            return Optional.empty();
        }
        try {
            return repo().findByDiscordIdAndGuild(user.getIdLong(), guild.getIdLong());
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "LEVEL", "Failed to load experience", e);
            return Optional.empty();
        }
    }
}