package ru.devprizrakk.voidbot.core.database.model;

import java.sql.Timestamp;

public class VoiceSession {
    private long id;
    private long userId;
    private long channelId;
    private Timestamp joinedAt;
    private Timestamp leftAt;
    private long durationSeconds;

    public VoiceSession() {
    }

    public VoiceSession(long id, long userId, long channelId, Timestamp joinedAt, Timestamp leftAt, long durationSeconds) {
        this.id = id;
        this.userId = userId;
        this.channelId = channelId;
        this.joinedAt = joinedAt;
        this.leftAt = leftAt;
        this.durationSeconds = durationSeconds;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public Timestamp getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Timestamp joinedAt) {
        this.joinedAt = joinedAt;
    }

    public Timestamp getLeftAt() {
        return leftAt;
    }

    public void setLeftAt(Timestamp leftAt) {
        this.leftAt = leftAt;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
}
