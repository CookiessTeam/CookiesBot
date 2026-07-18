package ru.devprizrakk.voidbot.logging;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.InterfacedEventManager;
import org.jspecify.annotations.NonNull;

public class DiscordEventManager extends InterfacedEventManager {

    @Override
    public void handle(@NonNull GenericEvent event) {
        for (Object listener : getRegisteredListeners()) {
            if (!(listener instanceof EventListener)) continue;
            try {
                ((EventListener) listener).onEvent(event);
            } catch (Throwable throwable) {
                Logger.getLogger().log(LogType.ERROR, "DISCORD",
                        "Exception in listener " + listener.getClass().getSimpleName()
                                + " while handling " + event.getClass().getSimpleName(), throwable);
            }
        }
    }
}