package ru.devprizrakk.voidbot.core.utils.applicationinfo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ApplicationInfo {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    public static String getCurrentTime() {
        return dateFormat.format(new Date());
    }
    public static ModuleInfo getModule(Module moduleName) {
        return new ModuleInfo(moduleName);
    }
}
