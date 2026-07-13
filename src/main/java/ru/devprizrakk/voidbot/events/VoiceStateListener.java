package ru.devprizrakk.voidbot.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.devprizrakk.voidbot.database.model.VoiceSessionModel;
import ru.devprizrakk.voidbot.database.repository.VoiceSessionRepository;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VoiceStateListener extends ListenerAdapter {

    private static final ConcurrentHashMap<String, VoiceSessionModel> active = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService xpScheduler = Executors.newScheduledThreadPool(1);

    static {
        xpScheduler.scheduleAtFixedRate(VoiceStateListener::tickRealtimeXp, 60, 60, TimeUnit.SECONDS);
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        Member member = event.getMember();
        if (member == null || member.getUser().isBot()) return;

        AudioChannelUnion joined = event.getChannelJoined();
        AudioChannelUnion left = event.getChannelLeft();

        if (left != null) {
            closeSession(member, event.getGuild());
        }
        if (joined != null) {
            tryOpenSession(member, joined, event.getGuild());
        }
    }

    private void tryOpenSession(Member member, AudioChannelUnion channel, Guild guild) {
        long userId = member.getIdLong();
        long channelId = channel.getIdLong();

        if (!isValidForXp(member, channel)) {
            Logger.getLogger().log(LogType.DEBUG, "EVENTS",
                    "Skipping voice session for user " + userId + " (alone or muted/deafened)");
            return;
        }

        try {
            VoiceSessionModel session = new VoiceSessionModel();
            session.setUserId(userId);
            session.setChannelId(channelId);
            session.setJoinedAt(new Timestamp(System.currentTimeMillis()));
            session.setDurationSeconds(0);
            repo().save(session);
            active.put(key(guild.getIdLong(), userId), session);
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "EVENTS",
                    "Failed to open voice session for user " + userId, e);
        }
    }

    private void closeSession(Member member, Guild guild) {
        long userId = member.getIdLong();
        String k = key(guild.getIdLong(), userId);

        VoiceSessionModel session = active.remove(k);
        if (session == null) {
            try {
                Optional<VoiceSessionModel> openOpt = repo().findOpenByUser(userId);
                if (openOpt.isPresent()) {
                    session = openOpt.get();
                } else {
                    Logger.getLogger().log(LogType.WARN, "EVENTS",
                            "No open voice session to close for user " + userId);
                    return;
                }
            } catch (Exception e) {
                Logger.getLogger().log(LogType.ERROR, "EVENTS",
                        "Failed to find orphaned session for user " + userId, e);
                return;
            }
        }

        try {
            long now = System.currentTimeMillis();
            long durationSeconds = Math.max(0L, (now - session.getJoinedAt().getTime()) / 1000L);
            Timestamp leftAt = new Timestamp(now);
            repo().closeSession(session.getId(), leftAt, durationSeconds);
            Logger.getLogger().log(LogType.INFO, "EVENTS",
                    "Closed voice session for user " + userId + " duration=" + durationSeconds + "s");
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "EVENTS",
                    "Failed to close voice session for user " + userId, e);
        }
    }

    private static void tickRealtimeXp() {
        if (active.isEmpty()) return;

        var jda = ru.devprizrakk.voidbot.bootstrap.discord.JDALoader.getJDA();
        if (jda == null) return;

        for (var entry : active.entrySet()) {
            String k = entry.getKey();
            VoiceSessionModel session = entry.getValue();

            try {
                String[] parts = k.split(":");
                long guildId = Long.parseLong(parts[0]);
                long userId = Long.parseLong(parts[1]);

                Guild guild = jda.getGuildById(guildId);
                if (guild == null) {
                    active.remove(k);
                    repo().closeSession(session.getId(), new Timestamp(System.currentTimeMillis()), 0);
                    continue;
                }

                Member member = guild.getMemberById(userId);
                if (member == null || member.getVoiceState() == null
                        || member.getVoiceState().getChannel() == null) {
                    active.remove(k);
                    long now = System.currentTimeMillis();
                    long dur = Math.max(0L, (now - session.getJoinedAt().getTime()) / 1000L);
                    repo().closeSession(session.getId(), new Timestamp(now), dur);
                    continue;
                }

                var vs = member.getVoiceState();
                var channel = vs.getChannel();
                if (!isValidVoiceState(member, channel)) {
                    continue;
                }

                LevelService.rewardVoiceMinutes(member.getUser(), guild, 1);
                StatisticsService.onVoiceMinutes(userId, 1);

                session.setDurationSeconds(session.getDurationSeconds() + 60);
                repo().update(session);
            } catch (Exception e) {
                Logger.getLogger().log(LogType.ERROR, "EVENTS",
                        "Error in realtime XP tick for session " + k, e);
            }
        }
    }

    private static boolean isValidForXp(Member member, AudioChannelUnion channel) {
        if (channel == null) return false;
        var members = channel.getMembers();
        if (members.size() <= 1) return false;
        return checkMuteDeafen(member);
    }

    private static boolean isValidVoiceState(Member member, AudioChannelUnion channel) {
        if (channel == null) return false;
        var members = channel.getMembers();
        long nonBots = members.stream().filter(m -> !m.getUser().isBot()).count();
        if (nonBots <= 1) return false;
        return checkMuteDeafen(member);
    }

    private static boolean checkMuteDeafen(Member member) {
        var vs = member.getVoiceState();
        if (vs == null) return false;
        if (vs.isGuildMuted()) return false;
        if (vs.isSelfMuted()) return false;
        if (vs.isGuildDeafened()) return false;
        if (vs.isSelfDeafened()) return false;
        return true;
    }

    private static String key(long guildId, long userId) {
        return guildId + ":" + userId;
    }

    public static void syncOnStartup(Guild guild) {
        for (VoiceChannel vc : guild.getVoiceChannels()) {
            for (Member member : vc.getMembers()) {
                if (member.getUser().isBot()) continue;
                long nonBots = vc.getMembers().stream().filter(m -> !m.getUser().isBot()).count();
                if (nonBots <= 1) continue;
                if (!checkMuteDeafen(member)) continue;

                try {
                    Optional<VoiceSessionModel> existing = repo().findOpenByUser(member.getIdLong());
                    if (existing.isPresent()) {
                        active.put(key(guild.getIdLong(), member.getIdLong()), existing.get());
                    } else {
                        VoiceSessionModel session = new VoiceSessionModel();
                        session.setUserId(member.getIdLong());
                        session.setChannelId(vc.getIdLong());
                        session.setJoinedAt(new Timestamp(System.currentTimeMillis()));
                        session.setDurationSeconds(0);
                        repo().save(session);
                        active.put(key(guild.getIdLong(), member.getIdLong()), session);
                    }
                } catch (Exception e) {
                    Logger.getLogger().log(LogType.ERROR, "EVENTS",
                            "Failed to sync voice session on startup for user " + member.getIdLong(), e);
                }
            }
        }
    }

    public static void cleanupOrphanedSessions() {
        try {
            int closed = repo().closeOrphanedSessions(new Timestamp(System.currentTimeMillis()));
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