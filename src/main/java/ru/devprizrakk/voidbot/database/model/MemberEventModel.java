package ru.devprizrakk.voidbot.database.model;

import java.sql.Timestamp;

public class MemberEventModel {

    private long id;
    private long userId;
    private MemberEventType eventType;
    private Timestamp createdAt;

    public MemberEventModel() {
    }

    public MemberEventModel(long id, long userId, MemberEventType eventType, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.eventType = eventType;
        this.createdAt = createdAt;
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

    public MemberEventType getEventType() {
        return eventType;
    }

    public void setEventType(MemberEventType eventType) {
        this.eventType = eventType;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
