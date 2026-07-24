package ru.devprizrakk.voidbot.database.model;

public enum UserDailyStatisticsColumn {
    MESSAGES("messages"),
    VOICE_MINUTES("voice_minutes"),
    REACTIONS("reactions"),
    COMMANDS("commands");

    private final String column;

    UserDailyStatisticsColumn(String column) {
        this.column = column;
    }

    public String getColumn() {
        return column;
    }
}
