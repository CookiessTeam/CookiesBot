package ru.devprizrakk.voidbot;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static ru.devprizrakk.voidbot.utils.UtilsManager.getLogger;


public class OnReady extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        getLogger().info("The bot is ready to work");
    }
}