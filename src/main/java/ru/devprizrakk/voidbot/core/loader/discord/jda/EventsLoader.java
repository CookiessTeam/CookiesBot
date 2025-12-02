package ru.devprizrakk.voidbot.core.loader.discord.jda;

import net.dv8tion.jda.api.JDA;
import ru.devprizrakk.voidbot.OnReady;
import ru.devprizrakk.voidbot.core.Utils;
import ru.devprizrakk.voidbot.core.system.logger.LogType;
import ru.devprizrakk.voidbot.core.system.logger.Logger;

public class EventsLoader extends Utils {
    public EventsLoader(JDA jda) {
        onSystemLoader(jda);
        //onUserLoader();
    }
    private void onSystemLoader(JDA jda) {
        Logger.getLogger().log(LogType.INFO,"loader", "Подгружаю системные события");

        jda.addEventListener(new OnReady());
    }
    private void onUserLoader() {
        Logger.getLogger().log(LogType.INFO,"loader", "Подгружаю пользовательские события");
    }
}
