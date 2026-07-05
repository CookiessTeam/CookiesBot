package ru.devprizrakk.voidbot.core.database.model;

import java.util.Date;

public class Mute {
    public Mute(long id, long userId, long moderatorId, String description, Date createdAt, Date expiredAt) {
        this.id = id;
        this.userId = userId;
        this.moderatorId = moderatorId;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Date expiredAt) {
        this.expiredAt = expiredAt;
    }

    private long id;
    private long userId;
    private long moderatorId;
    private String description;
    private Date createdAt;
    private Date expiredAt;
}
