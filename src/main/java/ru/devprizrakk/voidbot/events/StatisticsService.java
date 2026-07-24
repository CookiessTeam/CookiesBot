package ru.devprizrakk.voidbot.events;

import ru.devprizrakk.voidbot.database.model.DailyStatisticsColumn;
import ru.devprizrakk.voidbot.database.model.UserDailyStatisticsColumn;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

import java.sql.Date;

public final class StatisticsService {
    private StatisticsService() {
    }

    public static void onMessage(long userId) {
        Date today = today();
        try {
            Utils.getDatabaseManager().getRepositoryManager().getDailyStatistics().increment(today, DailyStatisticsColumn.MESSAGES, 1);
            Utils.getDatabaseManager().getRepositoryManager().getUserDailyStatistics().increment(today, userId, UserDailyStatisticsColumn.MESSAGES, 1);
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "STATS", "Failed increment messages", e);
        }
    }

    public static void onMessageDeleted() {
        Date today = today();
        try {
            Utils.getDatabaseManager().getRepositoryManager().getDailyStatistics().increment(today, DailyStatisticsColumn.DELETED_MESSAGES, 1);
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "STATS", "Failed increment deleted_messages", e);
        }
    }

    public static void onMemberJoin() {
        Date today = today();
        try {
            Utils.getDatabaseManager().getRepositoryManager().getDailyStatistics().increment(today, DailyStatisticsColumn.NEW_MEMBERS, 1);
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "STATS", "Failed increment new_members", e);
        }
    }

    public static void onMemberLeft() {
        Date today = today();
        try {
            Utils.getDatabaseManager().getRepositoryManager().getDailyStatistics().increment(today, DailyStatisticsColumn.LEFT_MEMBERS, 1);
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "STATS", "Failed increment left_members", e);
        }
    }

    public static void onVoiceMinutes(long userId, long minutes) {
        if (minutes <= 0) return;

        Date today = today();
        try {
            Utils.getDatabaseManager().getRepositoryManager().getDailyStatistics().increment(today, DailyStatisticsColumn.VOICE_MINUTES, minutes);
            Utils.getDatabaseManager().getRepositoryManager().getUserDailyStatistics().increment(today, userId, UserDailyStatisticsColumn.VOICE_MINUTES, minutes);
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "STATS", "Failed increment voice_minutes", e);
        }
    }

    public static void onWarn() {
        Date today = today();
        try {
            Utils.getDatabaseManager().getRepositoryManager().getDailyStatistics().increment(today, DailyStatisticsColumn.WARNS, 1);
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "STATS", "Failed increment warns", e);
        }
    }

    // TODO: Подключить подсчет команд
    public static void onCommand(long userId) {
        Date today = today();
        try {
            Utils.getDatabaseManager().getRepositoryManager().getUserDailyStatistics().increment(today, userId, UserDailyStatisticsColumn.COMMANDS, 1);
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "STATS", "Failed increment commands", e);
        }
    }

    private static Date today() {
        return new Date(System.currentTimeMillis());
    }
}