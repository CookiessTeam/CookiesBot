package ru.devprizrakk.voidbot.loader;

import net.dv8tion.jda.api.JDA;
import ru.devprizrakk.voidbot.OnReady;
import ru.devprizrakk.voidbot.utils.UtilsManager;

public class EventsLoader extends UtilsManager {
    JDA jda;
    public EventsLoader(JDA jda) {
        this.jda = jda;
        onSystemLoader();
        //onUserLoader();
    }
    private void onSystemLoader() {
        getLogger().info("loader","jda-events", "Подгружаю системные события");

        jda.addEventListener(new OnReady());
    }
    private void onUserLoader() {
        getLogger().info("loader","jda-events", "Подгружаю пользовательские события");
    }
}
