package ru.devprizrakk.voidbot.events;

import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.devprizrakk.voidbot.core.logging.LogType;
import ru.devprizrakk.voidbot.core.logging.Logger;

import java.sql.Timestamp;
import java.util.Date;

public class MessageDeleteListener extends ListenerAdapter {

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        if (!event.isFromGuild()) return;
        long messageId = event.getMessageIdLong();
        try {
            var repo = ru.devprizrakk.voidbot.core.utils.Utils.getDatabaseManager()
                    .getRepositoryManager().getMessages();
            var existing = repo.findByMessageId(messageId);
            if (existing.isPresent()) {
                repo.markDeleted(messageId, new Timestamp(new Date().getTime()));
                StatisticsService.onMessageDeleted(existing.get().getUserId());
            }
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "EVENTS", "Failed mark message deleted " + messageId, e);
        }
    }
}