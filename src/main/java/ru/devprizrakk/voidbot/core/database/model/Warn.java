package ru.devprizrakk.voidbot.core.database.model;

import java.sql.Timestamp;

public class Warn {
    private long id;
    private long userId;
    private long moderatorId;
    private String reason;
    private Timestamp createdAt;
    private Timestamp expiredAt;

    public Warn() {
    }

    public Warn(long id, long userId, long moderatorId, String reason, Timestamp createdAt, Timestamp expiredAt) {
        this.id = id;
        this.userId = userId;
        this.moderatorId = moderatorId;
        this.reason = reason;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
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

    public long getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(long moderatorId) {
        this.moderatorId = moderatorId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Timestamp expiredAt) {
        this.expiredAt = expiredAt;
    }
}
