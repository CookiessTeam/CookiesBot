package ru.devprizrakk.voidbot.core;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import ru.devprizrakk.voidbot.core.config.ConfigManager;
import ru.devprizrakk.voidbot.core.loader.discord.message.ErrorMessage;
import ru.devprizrakk.voidbot.core.system.applicationinfo.ApplicationInfo;
import ru.devprizrakk.voidbot.core.system.lang.LangHelper;

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
