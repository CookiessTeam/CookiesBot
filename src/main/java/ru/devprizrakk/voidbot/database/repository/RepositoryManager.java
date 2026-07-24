package ru.devprizrakk.voidbot.database.repository;

import ru.devprizrakk.voidbot.database.DatabaseManager;

public class RepositoryManager {

    private final BanRepository bans;
    private final DailyStatisticsRepository dailyStatistics;
    private final ExperienceRepository experience;
    private final MemberEventRepository memberEvents;
    private final MessageRepository messages;
    private final MuteRepository mutes;
    private final ServerStatisticsRepository serverStatistics;
    private final UserDailyStatisticsRepository userDailyStatistics;
    private final UserRepository users;
    private final VoiceSessionRepository voiceSessions;
    private final WarnRepository warns;

    public RepositoryManager(DatabaseManager databaseManager) {
        this.bans = new BanRepository(databaseManager);
        this.dailyStatistics = new DailyStatisticsRepository(databaseManager);
        this.experience = new ExperienceRepository(databaseManager);
        this.memberEvents = new MemberEventRepository(databaseManager);
        this.messages = new MessageRepository(databaseManager);
        this.mutes = new MuteRepository(databaseManager);
        this.serverStatistics = new ServerStatisticsRepository(databaseManager);
        this.userDailyStatistics = new UserDailyStatisticsRepository(databaseManager);
        this.users = new UserRepository(databaseManager);
        this.voiceSessions = new VoiceSessionRepository(databaseManager);
        this.warns = new WarnRepository(databaseManager);
    }

    public BanRepository getBans() {
        return bans;
    }

    public DailyStatisticsRepository getDailyStatistics() {
        return dailyStatistics;
    }

    public ExperienceRepository getExperience() {
        return experience;
    }

    public MemberEventRepository getMemberEvents() {
        return memberEvents;
    }

    public MessageRepository getMessages() {
        return messages;
    }

    public MuteRepository getMutes() {
        return mutes;
    }

    public ServerStatisticsRepository getServerStatistics() {
        return serverStatistics;
    }

    public UserDailyStatisticsRepository getUserDailyStatistics() {
        return userDailyStatistics;
    }

    public UserRepository getUsers() {
        return users;
    }

    public VoiceSessionRepository getVoiceSessions() {
        return voiceSessions;
    }

    public WarnRepository getWarns() {
        return warns;
    }
}
