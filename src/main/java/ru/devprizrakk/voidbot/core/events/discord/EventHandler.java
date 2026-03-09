package ru.devprizrakk.voidbot.core.events.discord;

import net.dv8tion.jda.api.JDA;

public class EventHandler {
    private final JDA jda;
    public EventHandler(JDA jda) {
        this.jda = jda;
    }
    public void register(Object eventListener) {
        jda.addEventListener(eventListener);
    }
    public void unregister(Object eventListener) {
        jda.removeEventListener(eventListener);
    }
}
