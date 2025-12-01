package ru.devprizrakk.voidbot.core.system.lang;

import ru.devprizrakk.voidbot.core.system.logger.LogType;
import ru.devprizrakk.voidbot.core.system.logger.Logger;

import java.util.Locale;
import java.util.Map;

public class LangManager {

    public static final String LANG_DIR = "language";
    public static final String DEFAULT_LANG = "ru";

    public static void init() {
        Logger.getLogger().log(LogType.INFO,"loader","language","Инициализация языковой системы...");

        // 1 — скачать локализации из GitHub
        LangRepositorySync.sync();

        // 2 — загрузить
        LangLoader.loadAllLanguages();

        // 3 — включить отслеживание изменений
        LangWatcher.startWatcher();
    }

    public static void reload() {
        Logger.getLogger().log(LogType.INFO,"loader","language","Перезагрузка локалей...");
        LangLoader.loadAllLanguages();
    }

    public static String get(String lang, String key) {
        Map<String, String> data = LangLoader.CACHE.get(lang.toLowerCase(Locale.ROOT));

        if (data == null) {
            Logger.getLogger().log(LogType.WARN,"loader","language",
                    "Язык не найден: " + lang + ", использую " + DEFAULT_LANG);
            data = LangLoader.CACHE.get(DEFAULT_LANG);
        }

        if (data == null) return "§c[No language loaded]";

        return data.getOrDefault(key, "§cMissing key: " + key);
    }

    public static String get(String lang, String key, Map<String, String> placeholders) {
        String msg = get(lang, key);
        for (var e : placeholders.entrySet()) {
            msg = msg.replace("%" + e.getKey() + "%", e.getValue());
        }
        return msg;
    }
}
