package ru.devprizrakk.voidbot.events;

import net.dv8tion.jda.api.JDA;
import ru.devprizrakk.voidbot.events.discord.EventHandler;

public class EventManager {
    private static JDA jda;
    public EventManager(JDA jda) {
        EventManager.jda = jda;
    }
    public EventManager() {

    }
    public static JDA getJDA() {
        return jda;
    }
    public static EventHandler getEventHandler() {
        return new EventHandler(jda);
    }

}
