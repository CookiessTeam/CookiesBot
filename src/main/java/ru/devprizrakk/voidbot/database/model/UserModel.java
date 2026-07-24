package ru.devprizrakk.voidbot.database.model;

import java.sql.Timestamp;

public class UserModel {

    private long id;
    private long discordId;
    private String username;
    private Timestamp joinedAt;
    private Timestamp leftAt;
    private boolean bot;
    private boolean levelUpDmEnabled = true;

    public UserModel() {
    }

    public UserModel(long id, long discordId, String username, Timestamp joinedAt, Timestamp leftAt, boolean bot) {
        this.id = id;
        this.discordId = discordId;
        this.username = username;
        this.joinedAt = joinedAt;
        this.leftAt = leftAt;
        this.bot = bot;
        this.levelUpDmEnabled = true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDiscordId() {
        return discordId;
    }

    public void setDiscordId(long discordId) {
        this.discordId = discordId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public boolean isBot() {
        return bot;
    }

    public void setBot(boolean bot) {
        this.bot = bot;
    }

    public boolean isLevelUpDmEnabled() {
        return levelUpDmEnabled;
    }

    public void setLevelUpDmEnabled(boolean levelUpDmEnabled) {
        this.levelUpDmEnabled = levelUpDmEnabled;
    }
}
