package ru.devprizrakk.voidbot.api.events;

import net.dv8tion.jda.api.JDA;
import ru.devprizrakk.voidbot.api.events.discord.EventHandler;

public class EventManager {
    private static JDA jda;

    public static void setJDA(JDA jda) {
        EventManager.jda = jda;
    }
    public static JDA getJDA() {
        return jda;
    }
    public static EventHandler getEventHandler() {
        return new EventHandler(jda);
    }

}
