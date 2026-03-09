package ru.devprizrakk.voidbot.core.language;

import ru.devprizrakk.voidbot.core.logging.LogType;
import ru.devprizrakk.voidbot.core.logging.Logger;

import java.util.Locale;
import java.util.Map;

public class LangManager {

    public static final String LANG_DIR = "language";
    public static final String DEFAULT_LANG = "ru";

    public static void init() {
        Logger.getLogger().log(LogType.INFO,"loader", "Инициализация языковой системы...");

        // 1 — скачать локализации из GitHub
        LangRepositorySync.sync();

        // 2 — загрузить
        LangLoader.loadAllLanguages();

        // 3 — включить отслеживание изменений
        LangWatcher.startWatcher();
    }

    public static void reload() {
        Logger.getLogger().log(LogType.INFO,"loader", "Перезагрузка локалей...");
        LangLoader.loadAllLanguages();
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
}
