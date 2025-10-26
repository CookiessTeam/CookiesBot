package ru.devprizrakk.voidbot.database.models;

public class ExperienceData {
    private int id;
    private String uuid;
    private int level;
    private int exp;
    private int maxExp;
    private long voiceTime;

    public ExperienceData(int id, String uuid, int level, int exp, int maxExp, long voiceTime) {
        this.id = id;
        this.uuid = uuid;
        this.level = level;
        this.exp = exp;
        this.maxExp = maxExp;
        this.voiceTime = voiceTime;
    }

    public int getId() { return id; }
    public String getUuid() { return uuid; }
    public int getLevel() { return level; }
    public int getExp() { return exp; }
    public int getMaxExp() { return maxExp; }
    public long getVoiceTime() { return voiceTime; }
}
