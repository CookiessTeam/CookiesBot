package ru.devprizrakk.voidbot.database.model;

import java.sql.Timestamp;

public class MessageRecordModel {

    private long id;
    private long messageId;
    private long userId;
    private long channelId;
    private Timestamp createdAt;
    private Timestamp deletedAt;

    public MessageRecordModel() {
    }

    public MessageRecordModel(long id, long messageId, long userId, long channelId, Timestamp createdAt, Timestamp deletedAt) {
        this.id = id;
        this.messageId = messageId;
        this.userId = userId;
        this.channelId = channelId;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }
}
