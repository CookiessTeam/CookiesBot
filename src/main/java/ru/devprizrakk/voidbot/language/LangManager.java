package ru.devprizrakk.voidbot.language;

import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

import java.util.Locale;
import java.util.Map;

public class LangManager {

    public static final String LANG_DIR = "language";
    public static final String DEFAULT_LANG = "ru";

    public static void init() {
        Logger.getLogger().log(LogType.INFO, "loader", "Инициализация языковой системы...");

        // 1 - скачать локализации из GitHub
        if (isRemoteSyncEnabled()) {
            LangRepositorySync.sync();
        } else {
            Logger.getLogger().log(LogType.INFO, "loader", "Удалённая синхронизация локализаций отключена (runtime=" + getRuntimeProfile() + ")");
        }

        // 2 - загрузить
        LangLoader.loadAllLanguages();

        // 3 - включить отслеживание изменений
        LangWatcher.startWatcher();
    }

    public static void reload() {
        Logger.getLogger().log(LogType.INFO, "loader", "Перезагрузка локалей...");
        LangLoader.loadAllLanguages();
    }

    public static String getRuntimeProfile() {
        return Utils.getConfigManager().getConfig().getString("system.runtime.profile", "RELEASE");
    }

    public static boolean isRemoteSyncEnabled() {
        String profile = getRuntimeProfile();
        if (profile == null || profile.isBlank()) {
            return true;
        }
        return !profile.equalsIgnoreCase("dev");
    }

    public static String get(String lang, String file, String key) {
        lang = lang.toLowerCase(Locale.ROOT);
        file = file.toLowerCase(Locale.ROOT);

        Map<String, Map<String, String>> langData = LangLoader.CACHE.get(lang);
        if (langData == null) langData = LangLoader.CACHE.get(DEFAULT_LANG);

        if (langData == null) return "§c[No language loaded]";

        Map<String, String> fileData = langData.get(file);
        if (fileData == null)
            return "§cMissing file: " + file;

        return fileData.getOrDefault(key, "§cMissing key: " + key);
    }

    public static String get(String lang, String key) {
        lang = lang.toLowerCase(Locale.ROOT);

        Map<String, String> flat = LangLoader.FLAT_CACHE.get(lang);
        if (flat == null) flat = LangLoader.FLAT_CACHE.get(DEFAULT_LANG);

        if (flat == null) return "§c[No language loaded]";

        String value = flat.get(key);
        if (value == null) return "§cMissing key: " + key;
        return value;
    }
}
