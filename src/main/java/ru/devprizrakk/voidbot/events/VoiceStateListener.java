package ru.devprizrakk.voidbot.events;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.devprizrakk.voidbot.core.database.model.VoiceSession;
import ru.devprizrakk.voidbot.core.database.repository.VoiceSessionRepository;
import ru.devprizrakk.voidbot.core.logging.LogType;
import ru.devprizrakk.voidbot.core.logging.Logger;
import ru.devprizrakk.voidbot.core.utils.Utils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

public class VoiceStateListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        Member member = event.getMember();
        if (member == null || member.getUser().isBot()) {
            return;
        }

        AudioChannelUnion joined = event.getChannelJoined();
        AudioChannelUnion left = event.getChannelLeft();

        if (left != null) {
            closeOpenSession(member, event);
        }
        if (joined != null) {
            openSession(member, joined.getIdLong());
        }
    }

    private void openSession(Member member, long channelId) {
        VoiceSession session = new VoiceSession();
        session.setUserId(member.getIdLong());
        session.setChannelId(channelId);
        session.setJoinedAt(new Timestamp(new Date().getTime()));
        session.setDurationSeconds(0);

        try {
            repo().save(session);
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "EVENTS",
                    "Failed to open voice session for user " + member.getIdLong(), e);
        }
    }

    private void closeOpenSession(Member member, GuildVoiceUpdateEvent event) {
        long userId = member.getIdLong();
        try {
            Optional<VoiceSession> openOpt = repo().findOpenByUser(userId);
            if (openOpt.isEmpty()) {
                Logger.getLogger().log(LogType.WARN, "EVENTS",
                        "No open voice session to close for user " + userId);
                return;
            }
            VoiceSession session = openOpt.get();

            long now = new Date().getTime();
            long durationSeconds = Math.max(0L, (now - session.getJoinedAt().getTime()) / 1000L);
            Timestamp leftAt = new Timestamp(now);

            repo().closeSession(session.getId(), leftAt, durationSeconds);

            long minutes = (durationSeconds + 59L) / 60L;
            if (minutes >= 1L) {
                LevelService.rewardVoiceMinutes(member.getUser(), event.getGuild(), minutes);
                StatisticsService.onVoiceMinutes(userId, minutes);
            }
            Logger.getLogger().log(LogType.INFO, "EVENTS",
                    "Closed voice session for user " + userId + " duration=" + durationSeconds + "s");
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "EVENTS",
                    "Failed to close voice session for user " + userId, e);
        }
    }

    public static void cleanupOrphanedSessions() {
        try {
            int closed = repo().closeOrphanedSessions(new Timestamp(new Date().getTime()));
            if (closed > 0) {
                Logger.getLogger().log(LogType.WARN, "EVENTS",
                        "Closed " + closed + " orphaned open voice session(s) left from previous run.");
            }
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "EVENTS",
                    "Failed to cleanup orphaned voice sessions", e);
        }
    }

    private static VoiceSessionRepository repo() {
        return Utils.getDatabaseManager().getRepositoryManager().getVoiceSessions();
    }
}