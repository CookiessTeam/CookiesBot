package ru.devprizrakk.voidbot.events;

import ru.devprizrakk.voidbot.core.logging.LogType;
import ru.devprizrakk.voidbot.core.logging.Logger;
import ru.devprizrakk.voidbot.core.utils.Utils;

import java.sql.Date;

public final class StatisticsService {
    private StatisticsService() {
    }

    public static void onMessage(long userId) {
        Date today = today();
        try {
            Utils.getDatabaseManager().getRepositoryManager().getDailyStatistics().increment(today, "messages", 1);
            Utils.getDatabaseManager().getRepositoryManager().getUserDailyStatistics().increment(today, userId, "messages", 1);
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "STATS", "Failed increment messages", e);
        }
    }

    public static void onMessageDeleted(long userId) {
        Date today = today();
        try {
            Utils.getDatabaseManager().getRepositoryManager().getDailyStatistics().increment(today, "deleted_messages", 1);
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "STATS", "Failed increment deleted_messages", e);
        }
    }

    public static void onMemberJoin(long userId) {
        Date today = today();
        try {
            Utils.getDatabaseManager().getRepositoryManager().getDailyStatistics().increment(today, "new_members", 1);
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "STATS", "Failed increment new_members", e);
        }
    }

    public static void onMemberLeft(long userId) {
        Date today = today();
        try {
            Utils.getDatabaseManager().getRepositoryManager().getDailyStatistics().increment(today, "left_members", 1);
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "STATS", "Failed increment left_members", e);
        }
    }

    public static void onVoiceMinutes(long userId, long minutes) {
        if (minutes <= 0) return;
        Date today = today();
        try {
            Utils.getDatabaseManager().getRepositoryManager().getDailyStatistics().increment(today, "voice_minutes", minutes);
            Utils.getDatabaseManager().getRepositoryManager().getUserDailyStatistics().increment(today, userId, "voice_minutes", minutes);
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "STATS", "Failed increment voice_minutes", e);
        }
    }

    public static void onWarn(long userId) {
        Date today = today();
        try {
            Utils.getDatabaseManager().getRepositoryManager().getDailyStatistics().increment(today, "warns", 1);
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "STATS", "Failed increment warns", e);
        }
    }

    public static void onCommand(long userId) {
        Date today = today();
        try {
            Utils.getDatabaseManager().getRepositoryManager().getUserDailyStatistics().increment(today, userId, "commands", 1);
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "STATS", "Failed increment commands", e);
        }
    }

    private static Date today() {
        return new Date(System.currentTimeMillis());
    }
}