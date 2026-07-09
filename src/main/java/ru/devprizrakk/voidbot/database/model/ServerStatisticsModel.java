package ru.devprizrakk.voidbot.database.model;

import java.sql.Date;

public class ServerStatisticsModel {

    private Date date;
    private long memberCount;
    private long onlineCount;
    private long boostCount;
    private long channelCount;
    private long roleCount;

    public ServerStatisticsModel(Date date, long memberCount, long onlineCount, long boostCount, long channelCount, long roleCount) {
        this.date = date;
        this.memberCount = memberCount;
        this.onlineCount = onlineCount;
        this.boostCount = boostCount;
        this.channelCount = channelCount;
        this.roleCount = roleCount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(long memberCount) {
        this.memberCount = memberCount;
    }

    public long getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(long onlineCount) {
        this.onlineCount = onlineCount;
    }

    public long getBoostCount() {
        return boostCount;
    }

    public void setBoostCount(long boostCount) {
        this.boostCount = boostCount;
    }

    public long getChannelCount() {
        return channelCount;
    }

    public void setChannelCount(long channelCount) {
        this.channelCount = channelCount;
    }

    public long getRoleCount() {
        return roleCount;
    }

    public void setRoleCount(long roleCount) {
        this.roleCount = roleCount;
    }
}
