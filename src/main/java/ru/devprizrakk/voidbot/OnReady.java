package ru.devprizrakk.voidbot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.devprizrakk.voidbot.events.VoiceStateListener;
import ru.devprizrakk.voidbot.events.voiceroom.VoiceRoomListener;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;

public class OnReady extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Logger.getLogger().log(LogType.INFO, "loader", "Подключение к API Discord прошло успешно");

        Logger.getLogger().log(LogType.INFO, "loader", "Анализ голосовых каналов и синхронизация сессий...");
        VoiceStateListener.cleanupOrphanedSessions();
        for (Guild guild : event.getJDA().getGuilds()) {
            VoiceStateListener.syncOnStartup(guild);
            VoiceRoomListener.reconcile(guild);
            VoiceRoomListener.postPanelIfMissing(guild);
        }
        Logger.getLogger().log(LogType.INFO, "loader", "Синхронизация голосовых каналов завершена.");
    }
}