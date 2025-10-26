package ru.devprizrakk.voidbot.database.models;

public class UserData {
    private int id;
    private String uuid;
    private int warn;
    private String role;

    public UserData(int id, String uuid, int warn, String role) {
        this.id = id;
        this.uuid = uuid;
        this.warn = warn;
        this.role = role;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public String getUuid() { return uuid; }
    public int getWarn() { return warn; }
    public String getRole() { return role; }

    public void setWarn(int warn) { this.warn = warn; }
    public void setRole(String role) { this.role = role; }
}
