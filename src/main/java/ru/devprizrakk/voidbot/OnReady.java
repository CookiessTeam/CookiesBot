package ru.devprizrakk.voidbot;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.devprizrakk.voidbot.core.system.logger.LogType;
import ru.devprizrakk.voidbot.core.system.logger.Logger;


public class OnReady extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        Logger.getLogger().log(LogType.INFO,"loader", "Подключение к API Discord прошло успешно");
    }
}