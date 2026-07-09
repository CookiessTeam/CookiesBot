package ru.devprizrakk.voidbot.events;

import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.devprizrakk.voidbot.database.model.MemberEventModel;
import ru.devprizrakk.voidbot.database.model.MemberEventType;
import ru.devprizrakk.voidbot.database.model.UserModel;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MemberEventListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        User user = event.getUser();
        record(event.getUser(), MemberEventType.JOIN);
        upsertUserOnJoin(user);
        StatisticsService.onMemberJoin();
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        User user = event.getUser();
        boolean kicked = detectKick(event);
        MemberEventType type = kicked ? MemberEventType.KICK : MemberEventType.LEAVE;
        record(user, type);
        markUserLeft(user);
        StatisticsService.onMemberLeft();
    }

    @Override
    public void onGuildBan(@NotNull GuildBanEvent event) {
        User user = event.getUser();
        record(user, MemberEventType.BAN);
        markUserLeft(user);
        StatisticsService.onMemberLeft();
    }

    @Override
    public void onGuildUnban(@NotNull GuildUnbanEvent event) {
        record(event.getUser(), MemberEventType.UNBAN);
    }

    private boolean detectKick(GuildMemberRemoveEvent event) {
        try {
            List<AuditLogEntry> logs = event.getGuild().retrieveAuditLogs().type(ActionType.KICK).limit(5).complete();
            long targetId = event.getUser().getIdLong();
            long now = System.currentTimeMillis();
            for (AuditLogEntry entry : logs) {
                if (entry.getTargetIdLong() == targetId && entry.getTimeCreated().toInstant().toEpochMilli() > now - 60_000L) {
                    return true;
                }
            }
        } catch ( Exception ignored ) {
        }
        return false;
    }

    private void record(User user, MemberEventType type) {
        if (user == null || user.isBot()) {
            return;
        }

        try {
            MemberEventModel event = new MemberEventModel();
            event.setUserId(user.getIdLong());
            event.setEventType(type);
            event.setCreatedAt(new Timestamp(new Date().getTime()));
            Utils.getDatabaseManager().getRepositoryManager().getMemberEvents().save(event);
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "EVENTS", "Failed to save member event " + type, e);
        }
    }

    private void upsertUserOnJoin(User discordUser) {
        if (discordUser == null || discordUser.isBot()) return;

        try {
            var repo = Utils.getDatabaseManager().getRepositoryManager().getUsers();
            Optional<UserModel> existing = repo.findByDiscordId(discordUser.getIdLong());
            Timestamp now = new Timestamp(new Date().getTime());

            UserModel user;
            if (existing.isPresent()) {
                user = existing.get();
                user.setUsername(discordUser.getEffectiveName());
                user.setJoinedAt(now);
                user.setLeftAt(null);
            } else {
                user = new UserModel();
                user.setDiscordId(discordUser.getIdLong());
                user.setUsername(discordUser.getEffectiveName());
                user.setJoinedAt(now);
                user.setLeftAt(null);
                user.setBot(discordUser.isBot());
            }
            repo.save(user);
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "EVENTS", "Failed upsert user on join", e);
        }
    }

    private void markUserLeft(User discordUser) {
        if (discordUser == null || discordUser.isBot()) return;

        try {
            var repo = Utils.getDatabaseManager().getRepositoryManager().getUsers();
            Optional<UserModel> existing = repo.findByDiscordId(discordUser.getIdLong());
            if (existing.isPresent()) {
                var user = existing.get();
                user.setLeftAt(new Timestamp(new Date().getTime()));
                repo.save(user);
            }
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "EVENTS", "Failed mark user left", e);
        }
    }
}