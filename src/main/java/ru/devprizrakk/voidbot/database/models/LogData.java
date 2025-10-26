package ru.devprizrakk.voidbot.database.models;

public class LogData {
    private int id;
    private String uuid;
    private String body;
    private String createdTime;

    public LogData(int id, String uuid, String body, String createdTime) {
        this.id = id;
        this.uuid = uuid;
        this.body = body;
        this.createdTime = createdTime;
    }

    public int getId() { return id; }
    public String getUuid() { return uuid; }
    public String getBody() { return body; }
    public String getCreatedTime() { return createdTime; }
}
