package ru.devprizrakk.voidbot.database.models;

public class RoleData {
    private int id;
    private String name;
    private String displayName;
    private String perms;
    private boolean discordSync;
    private String discordSyncRoleId;

    public RoleData(int id, String name, String displayName, String perms, boolean discordSync, String discordSyncRoleId) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.perms = perms;
        this.discordSync = discordSync;
        this.discordSyncRoleId = discordSyncRoleId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public String getPerms() { return perms; }
    public boolean isDiscordSync() { return discordSync; }
    public String getDiscordSyncRoleId() { return discordSyncRoleId; }
}
