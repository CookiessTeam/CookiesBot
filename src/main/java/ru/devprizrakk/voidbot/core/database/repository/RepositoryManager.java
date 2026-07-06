package ru.devprizrakk.voidbot.core.database.repository;

import ru.devprizrakk.voidbot.core.database.DatabaseManager;

public class RepositoryManager {
    private final UserRepository users;
    private final MessageRepository messages;
    private final VoiceSessionRepository voiceSessions;
    private final MemberEventRepository memberEvents;
    private final WarnRepository warns;
    private final BanRepository bans;
    private final MuteRepository mutes;
    private final DailyStatisticsRepository dailyStatistics;
    private final UserDailyStatisticsRepository userDailyStatistics;
    private final ServerStatisticsRepository serverStatistics;
    private final ExperienceRepository experience;

    public RepositoryManager(DatabaseManager databaseManager) {
        this.users = new UserRepository(databaseManager);
        this.messages = new MessageRepository(databaseManager);
        this.voiceSessions = new VoiceSessionRepository(databaseManager);
        this.memberEvents = new MemberEventRepository(databaseManager);
        this.warns = new WarnRepository(databaseManager);
        this.bans = new BanRepository(databaseManager);
        this.mutes = new MuteRepository(databaseManager);
        this.dailyStatistics = new DailyStatisticsRepository(databaseManager);
        this.userDailyStatistics = new UserDailyStatisticsRepository(databaseManager);
        this.serverStatistics = new ServerStatisticsRepository(databaseManager);
        this.experience = new ExperienceRepository(databaseManager);
    }

    public UserRepository getUsers() {
        return users;
    }

    public MessageRepository getMessages() {
        return messages;
    }

    public VoiceSessionRepository getVoiceSessions() {
        return voiceSessions;
    }

    public MemberEventRepository getMemberEvents() {
        return memberEvents;
    }

    public WarnRepository getWarns() {
        return warns;
    }

    public DailyStatisticsRepository getDailyStatistics() {
        return dailyStatistics;
    }

    public UserDailyStatisticsRepository getUserDailyStatistics() {
        return userDailyStatistics;
    }

    public ServerStatisticsRepository getServerStatistics() {
        return serverStatistics;
    }

    public ExperienceRepository getExperience() {
        return experience;
    }

    public BanRepository getBans() {
        return bans;
    }

    public MuteRepository getMutes() {
        return mutes;
    }
}
