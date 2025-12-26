package ru.devprizrakk.voidbot.language;

import org.yaml.snakeyaml.Yaml;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;

import java.io.*;
import java.util.*;

public class LangLoader {

    // CACHE[lang][filePath][key] = value
    static final Map<String, Map<String, Map<String, String>>> CACHE = new HashMap<>();
    private static final Yaml YAML = new Yaml();

    public static void loadAllLanguages() {
        CACHE.clear();

        File langRoot = new File(LangManager.LANG_DIR);
        File[] langDirs = langRoot.listFiles(File::isDirectory);

        if (langDirs == null || langDirs.length == 0) {
            Logger.getLogger().log(LogType.ERROR, "loader",
                    "Не найдено ни одной локали в /language/");
            return;
        }

        for (File langDir : langDirs) {
            String langCode = langDir.getName().toLowerCase(Locale.ROOT);

            Map<String, Map<String, String>> langFiles = new HashMap<>();

            loadRecursive(langDir, "", langFiles);

            CACHE.put(langCode, langFiles);
            Logger.getLogger().log(LogType.INFO, "loader",
                    "Загружено " + langFiles.size() + " файлов локалей для языка " + langCode);
        }
    }

    private static void loadRecursive(File folder, String relativePath,
                                      Map<String, Map<String, String>> target) {

        for (File f : folder.listFiles()) {

            String newPath = relativePath.isEmpty()
                    ? f.getName()
                    : relativePath + "/" + f.getName();

            if (f.isDirectory()) {
                loadRecursive(f, newPath, target);
                continue;
            }

            if (!f.getName().endsWith(".yml")) continue;

            try (InputStream is = new FileInputStream(f)) {
                Map<String, Object> parsed = YAML.load(is);
                Map<String, String> flat = new HashMap<>();

                if (parsed != null) flatten("", parsed, flat);

                target.put(newPath.toLowerCase(Locale.ROOT), flat);

            } catch (Exception e) {
                Logger.getLogger().log(LogType.ERROR,"loader",
                        "Ошибка загрузки файла " + f.getPath(), e);
            }
        }
    }

    private static void flatten(String prefix, Map<?, ?> src, Map<String, String> out) {
        for (var e : src.entrySet()) {
            String key = prefix.isEmpty() ? e.getKey().toString()
                    : prefix + "." + e.getKey();

            Object val = e.getValue();

            if (val instanceof Map<?,?> map) {
                flatten(key, map, out);
            } else {
                out.put(key, val.toString());
            }
        }
    }
}
