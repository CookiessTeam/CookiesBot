package ru.devprizrakk.voidbot.core.database.model;

import java.sql.Timestamp;

public class Experience {
    private long id;
    private long discordId;
    private long guildId;
    private long level;
    private long experience;
    private long totalExperience;
    private Timestamp updatedAt;

    public Experience() {
    }

    public Experience(long id, long discordId, long guildId, long level, long experience, long totalExperience, Timestamp updatedAt) {
        this.id = id;
        this.discordId = discordId;
        this.guildId = guildId;
        this.level = level;
        this.experience = experience;
        this.totalExperience = totalExperience;
        this.updatedAt = updatedAt;
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

    public long getGuildId() {
        return guildId;
    }

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }

    public long getTotalExperience() {
        return totalExperience;
    }

    public void setTotalExperience(long totalExperience) {
        this.totalExperience = totalExperience;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}