package ru.devprizrakk.voidbot.database.models;

public class GuildData {
    private int id;
    private String guildId;
    private String configJson;

    public GuildData(int id, String guildId, String configJson) {
        this.id = id;
        this.guildId = guildId;
        this.configJson = configJson;
    }

    public int getId() { return id; }
    public String getGuildId() { return guildId; }
    public String getConfigJson() { return configJson; }
}
