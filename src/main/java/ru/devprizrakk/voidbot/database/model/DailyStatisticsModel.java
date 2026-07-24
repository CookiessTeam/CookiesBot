package ru.devprizrakk.voidbot.database.model;

import java.sql.Date;

public class DailyStatisticsModel {

    private Date date;
    private long messages;
    private long newMembers;
    private long leftMembers;
    private long activeMembers;
    private long voiceMinutes;
    private long warns;
    private long deletedMessages;

    public DailyStatisticsModel(Date date, long messages, long newMembers, long leftMembers, long activeMembers,
                                long voiceMinutes, long warns, long deletedMessages) {
        this.date = date;
        this.messages = messages;
        this.newMembers = newMembers;
        this.leftMembers = leftMembers;
        this.activeMembers = activeMembers;
        this.voiceMinutes = voiceMinutes;
        this.warns = warns;
        this.deletedMessages = deletedMessages;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getMessages() {
        return messages;
    }

    public void setMessages(long messages) {
        this.messages = messages;
    }

    public long getNewMembers() {
        return newMembers;
    }

    public void setNewMembers(long newMembers) {
        this.newMembers = newMembers;
    }

    public long getLeftMembers() {
        return leftMembers;
    }

    public void setLeftMembers(long leftMembers) {
        this.leftMembers = leftMembers;
    }

    public long getActiveMembers() {
        return activeMembers;
    }

    public void setActiveMembers(long activeMembers) {
        this.activeMembers = activeMembers;
    }

    public long getVoiceMinutes() {
        return voiceMinutes;
    }

    public void setVoiceMinutes(long voiceMinutes) {
        this.voiceMinutes = voiceMinutes;
    }

    public long getWarns() {
        return warns;
    }

    public void setWarns(long warns) {
        this.warns = warns;
    }

    public long getDeletedMessages() {
        return deletedMessages;
    }

    public void setDeletedMessages(long deletedMessages) {
        this.deletedMessages = deletedMessages;
    }
}
