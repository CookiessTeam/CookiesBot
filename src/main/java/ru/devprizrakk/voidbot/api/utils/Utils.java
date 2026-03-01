package ru.devprizrakk.voidbot.api.utils;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import ru.devprizrakk.voidbot.api.config.ConfigManager;
import ru.devprizrakk.voidbot.api.utils.applicationinfo.ApplicationInfo;
import ru.devprizrakk.voidbot.api.language.LangHelper;

public class Utils {

    private static final ConfigManager configManager = new ConfigManager();
    private static final ApplicationInfo applicationInfo = new ApplicationInfo();

    public static LangHelper getLangManager(IReplyCallback event) {
        return new LangHelper(event);
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }
    public static ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }


}
