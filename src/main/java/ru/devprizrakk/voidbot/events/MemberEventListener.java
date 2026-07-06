package ru.devprizrakk.voidbot.events;

import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.devprizrakk.voidbot.core.database.model.MemberEvent;
import ru.devprizrakk.voidbot.core.database.model.MemberEventType;
import ru.devprizrakk.voidbot.core.logging.LogType;
import ru.devprizrakk.voidbot.core.logging.Logger;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MemberEventListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        User user = event.getUser();
        record(event.getUser(), event.getGuild().getIdLong(), MemberEventType.JOIN);
        upsertUserOnJoin(user);
        StatisticsService.onMemberJoin(user.getIdLong());
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        User user = event.getUser();
        boolean kicked = detectKick(event);
        MemberEventType type = kicked ? MemberEventType.KICK : MemberEventType.LEAVE;
        record(user, event.getGuild().getIdLong(), type);
        markUserLeft(user);
        StatisticsService.onMemberLeft(user.getIdLong());
    }

    @Override
    public void onGuildBan(@NotNull GuildBanEvent event) {
        User user = event.getUser();
        record(user, event.getGuild().getIdLong(), MemberEventType.BAN);
        markUserLeft(user);
        StatisticsService.onMemberLeft(user.getIdLong());
    }

    @Override
    public void onGuildUnban(@NotNull GuildUnbanEvent event) {
        record(event.getUser(), event.getGuild().getIdLong(), MemberEventType.UNBAN);
    }

    private boolean detectKick(GuildMemberRemoveEvent event) {
        try {
            List<AuditLogEntry> logs = event.getGuild()
                    .retrieveAuditLogs().type(ActionType.KICK).limit(5).complete();
            long targetId = event.getUser().getIdLong();
            long now = System.currentTimeMillis();
            for (AuditLogEntry entry : logs) {
                if (entry.getTargetIdLong() == targetId
                        && entry.getTimeCreated().toInstant().toEpochMilli() > now - 60_000L) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private void record(User user, long guildId, MemberEventType type) {
        if (user == null || user.isBot()) {
            return;
        }
        try {
            MemberEvent event = new MemberEvent();
            event.setUserId(user.getIdLong());
            event.setEventType(type);
            event.setCreatedAt(new Timestamp(new Date().getTime()));
            ru.devprizrakk.voidbot.core.utils.Utils.getDatabaseManager()
                    .getRepositoryManager().getMemberEvents().save(event);
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "EVENTS", "Failed to save member event " + type, e);
        }
    }

    private void upsertUserOnJoin(User user) {
        if (user == null || user.isBot()) return;
        try {
            var repo = ru.devprizrakk.voidbot.core.utils.Utils.getDatabaseManager().getRepositoryManager().getUsers();
            Optional<ru.devprizrakk.voidbot.core.database.model.User> existing = repo.findByDiscordId(user.getIdLong());
            Timestamp now = new Timestamp(new Date().getTime());
            if (existing.isPresent()) {
                var u = existing.get();
                u.setUsername(user.getEffectiveName());
                u.setJoinedAt(now);
                u.setLeftAt(null);
                repo.save(u);
            } else {
                var u = new ru.devprizrakk.voidbot.core.database.model.User();
                u.setDiscordId(user.getIdLong());
                u.setUsername(user.getEffectiveName());
                u.setJoinedAt(now);
                u.setLeftAt(null);
                u.setBot(user.isBot());
                repo.save(u);
            }
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "EVENTS", "Failed upsert user on join", e);
        }
    }

    private void markUserLeft(User user) {
        if (user == null || user.isBot()) return;
        try {
            var repo = ru.devprizrakk.voidbot.core.utils.Utils.getDatabaseManager().getRepositoryManager().getUsers();
            Optional<ru.devprizrakk.voidbot.core.database.model.User> existing = repo.findByDiscordId(user.getIdLong());
            if (existing.isPresent()) {
                var u = existing.get();
                u.setLeftAt(new Timestamp(new Date().getTime()));
                repo.save(u);
            }
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "EVENTS", "Failed mark user left", e);
        }
    }
}