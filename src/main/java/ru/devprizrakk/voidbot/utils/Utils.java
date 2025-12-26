package ru.devprizrakk.voidbot.utils;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import ru.devprizrakk.voidbot.core.config.ConfigManager;
import ru.devprizrakk.voidbot.core.system.applicationinfo.ApplicationInfo;
import ru.devprizrakk.voidbot.core.system.lang.LangHelper;
import ru.devprizrakk.voidbot.config.ConfigManager;
import ru.devprizrakk.voidbot.utils.applicationinfo.ApplicationInfo;
import ru.devprizrakk.voidbot.language.LangHelper;

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

    public static ErrorMessage getErrorMessage(IReplyCallback event) {
        return new ErrorMessage(event);
    }
}
