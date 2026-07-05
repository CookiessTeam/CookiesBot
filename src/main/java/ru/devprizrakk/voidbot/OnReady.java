package ru.devprizrakk.voidbot;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ru.devprizrakk.voidbot.core.logging.LogType;
import ru.devprizrakk.voidbot.core.logging.Logger;
import ru.devprizrakk.voidbot.events.VoiceStateListener;


public class OnReady extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Logger.getLogger().log(LogType.INFO,"loader", "Подключение к API Discord прошло успешно");
        VoiceStateListener.cleanupOrphanedSessions();
    }
}