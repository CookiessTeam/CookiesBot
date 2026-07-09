package ru.devprizrakk.voidbot.database.model;

public enum DailyStatisticsColumn {
    MESSAGES("messages"),
    NEW_MEMBERS("new_members"),
    LEFT_MEMBERS("left_members"),
    ACTIVE_MEMBERS("active_members"),
    VOICE_MINUTES("voice_minutes"),
    WARNS("warns"),
    DELETED_MESSAGES("deleted_messages");

    private final String column;

    DailyStatisticsColumn(String column) {
        this.column = column;
    }

    public String getColumn() {
        return column;
    }
}
