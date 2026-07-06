package ru.devprizrakk.voidbot.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.devprizrakk.voidbot.core.config.Config;
import ru.devprizrakk.voidbot.core.database.model.MessageRecord;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageEventListener extends ListenerAdapter {
    private final Map<String, Long> cooldowns = new ConcurrentHashMap<>();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.isFromGuild()) return;
        Member member = event.getMember();
        if (member == null || member.getUser().isBot()) return;

        Guild guild = event.getGuild();
        long userId = member.getIdLong();

        try {
            MessageRecord record = new MessageRecord();
            record.setMessageId(event.getMessageIdLong());
            record.setUserId(userId);
            record.setChannelId(event.getChannel().getIdLong());
            record.setCreatedAt(new Timestamp(new Date().getTime()));
            record.setDeletedAt(null);
            ru.devprizrakk.voidbot.core.utils.Utils.getDatabaseManager()
                    .getRepositoryManager().getMessages().save(record);
        } catch (Exception e) {
            ru.devprizrakk.voidbot.core.logging.Logger.getLogger()
                    .log(ru.devprizrakk.voidbot.core.logging.LogType.ERROR, "EVENTS",
                            "Failed to save message record", e);
        }

        StatisticsService.onMessage(userId);

        long cdSeconds = getConfig().getLong("levels.message-cooldown-seconds", 60L);
        long now = System.currentTimeMillis();
        String key = guild.getIdLong() + ":" + userId;
        Long last = cooldowns.get(key);
        if (last != null && (now - last) < cdSeconds * 1000L) {
            return;
        }
        cooldowns.put(key, now);
        LevelService.rewardMessage(member.getUser(), guild);
    }

    private Config getConfig() {
        return ru.devprizrakk.voidbot.core.utils.Utils.getConfigManager().getConfig();
    }
}