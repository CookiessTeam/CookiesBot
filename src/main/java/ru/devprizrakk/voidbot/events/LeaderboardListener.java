package ru.devprizrakk.voidbot.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.devprizrakk.voidbot.command.impl.server.user.RankCardRenderer;
import ru.devprizrakk.voidbot.database.model.ExperienceModel;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LeaderboardListener extends ListenerAdapter {

    public static final int PAGE_SIZE = 10;
    private static final Map<String, Long> pageCache = new ConcurrentHashMap<>();

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String id = event.getComponentId();
        if (!id.startsWith("lb_")) return;

        String[] parts = id.split(":");
        if (parts.length != 3) return;

        String action = parts[0];
        String sort = parts[1];
        int page;
        try {
            page = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return;
        }

        if (event.getGuild() == null) return;

        switch (action) {
            case "lb_prev" -> page = Math.max(0, page - 1);
            case "lb_next" -> page++;
            case "lb_refresh" -> { /* keep page */ }
            default -> { return; }
        }

        EmbedBuilder embed = buildPage(event.getGuild(), sort, page);
        if (embed == null) {
            event.reply("Не удалось построить таблицу лидеров.").setEphemeral(true).queue();
            return;
        }

        long total = getTotalEntries(event.getGuild().getIdLong(), sort);
        int totalPages = (int) Math.max(1, Math.ceil((double) total / PAGE_SIZE));

        if (page >= totalPages) page = totalPages - 1;

        var prevBtn = net.dv8tion.jda.api.components.buttons.Button.secondary(
                "lb_prev:" + sort + ":" + page, "◀").withDisabled(page <= 0);
        var nextBtn = net.dv8tion.jda.api.components.buttons.Button.secondary(
                "lb_next:" + sort + ":" + page, "▶").withDisabled(page >= totalPages - 1);
        var refreshBtn = net.dv8tion.jda.api.components.buttons.Button.primary(
                "lb_refresh:" + sort + ":" + page, "↻");

        event.editMessageEmbeds(embed.build())
                .setComponents(ActionRow.of(prevBtn, refreshBtn, nextBtn))
                .queue();
    }

    public static EmbedBuilder buildPage(Guild guild, String sort, int page) {
        long guildId = guild.getIdLong();
        int offset = page * PAGE_SIZE;
        int limit = PAGE_SIZE;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255, 165, 0));
        embed.setTitle(Utils.getLangManager().getInfoLocale("leaderboard.embed.title"));
        embed.setFooter("Страница " + (page + 1) + " | Сортировка: " + sortLabel(sort) + " | VoidBot");

        try {
            var rm = Utils.getDatabaseManager().getRepositoryManager();

            switch (sort) {
                case "level" -> {
                    List<ExperienceModel> all = rm.getExperience().getTopByLevel(guildId, offset + limit);
                    List<ExperienceModel> pageItems = all.subList(Math.min(offset, all.size()), Math.min(offset + limit, all.size()));
                    for (int i = 0; i < pageItems.size(); i++) {
                        ExperienceModel exp = pageItems.get(i);
                        int rank = offset + i + 1;
                        String name = resolveName(guild, exp.getDiscordId());
                        embed.addField("#" + rank + " " + name,
                                "Ур. **" + exp.getLevel() + "** · " + exp.getTotalExperience() + " XP", false);
                    }
                }
                case "voice" -> {
                    List<long[]> all = rm.getVoiceSessions().topUsersByVoiceTime(offset + limit);
                    List<long[]> pageItems = all.subList(Math.min(offset, all.size()), Math.min(offset + limit, all.size()));
                    for (int i = 0; i < pageItems.size(); i++) {
                        long[] row = pageItems.get(i);
                        int rank = offset + i + 1;
                        String name = resolveName(guild, row[0]);
                        embed.addField("#" + rank + " " + name,
                                "Войс: **" + RankCardRenderer.formatTime(row[1]) + "**", false);
                    }
                }
                default -> {
                    List<ExperienceModel> all = rm.getExperience().getTopByGuild(guildId, offset + limit);
                    List<ExperienceModel> pageItems = all.subList(Math.min(offset, all.size()), Math.min(offset + limit, all.size()));
                    for (int i = 0; i < pageItems.size(); i++) {
                        ExperienceModel exp = pageItems.get(i);
                        int rank = offset + i + 1;
                        String name = resolveName(guild, exp.getDiscordId());
                        embed.addField("#" + rank + " " + name,
                                "Ур. **" + exp.getLevel() + "** · " + exp.getTotalExperience() + " XP", false);
                    }
                }
            }

            if (embed.getFields().isEmpty()) {
                embed.setDescription(Utils.getLangManager().getInfoLocale("leaderboard.empty"));
            } else {
                embed.setDescription(Utils.getLangManager().getInfoLocale("leaderboard.embed.description"));
            }
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "LEADERBOARD", "Failed to build page", e);
            return null;
        }

        return embed;
    }

    public static long getTotalEntries(long guildId, String sort) {
        try {
            var rm = Utils.getDatabaseManager().getRepositoryManager();
            return switch (sort) {
                case "voice" -> Math.max(1, rm.getVoiceSessions().countDistinctUsers());
                default -> Math.max(1, rm.getExperience().countByGuild(guildId));
            };
        } catch (Exception e) {
            return 1;
        }
    }

    private static String resolveName(Guild guild, long userId) {
        if (guild != null) {
            Member member = guild.getMemberById(userId);
            if (member != null) return member.getEffectiveName();
        }
        return "<@" + userId + ">";
    }

    private static String sortLabel(String sort) {
        return switch (sort) {
            case "level" -> "Уровень";
            case "voice" -> "Войс";
            default -> "XP";
        };
    }
}