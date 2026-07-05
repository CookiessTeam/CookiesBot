package ru.devprizrakk.voidbot.core.database.model;

import java.sql.Date;

public class UserDailyStatistics {
    private Date date;
    private long userId;
    private long messages;
    private long voiceMinutes;
    private long reactions;
    private long commands;

    public UserDailyStatistics() {
    }

    public UserDailyStatistics(Date date, long userId, long messages, long voiceMinutes, long reactions, long commands) {
        this.date = date;
        this.userId = userId;
        this.messages = messages;
        this.voiceMinutes = voiceMinutes;
        this.reactions = reactions;
        this.commands = commands;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getMessages() {
        return messages;
    }

    public void setMessages(long messages) {
        this.messages = messages;
    }

    public long getVoiceMinutes() {
        return voiceMinutes;
    }

    public void setVoiceMinutes(long voiceMinutes) {
        this.voiceMinutes = voiceMinutes;
    }

    public long getReactions() {
        return reactions;
    }

    public void setReactions(long reactions) {
        this.reactions = reactions;
    }

    public long getCommands() {
        return commands;
    }

    public void setCommands(long commands) {
        this.commands = commands;
    }
}
