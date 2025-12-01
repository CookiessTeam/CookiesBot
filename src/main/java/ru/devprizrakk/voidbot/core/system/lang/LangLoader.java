package ru.devprizrakk.voidbot.core.system.lang;

import org.yaml.snakeyaml.Yaml;
import ru.devprizrakk.voidbot.core.system.logger.*;

import java.io.*;
import java.util.*;
import java.nio.file.*;

public class LangLoader {

    static final Map<String, Map<String, String>> CACHE = new HashMap<>();
    private static final Yaml YAML = new Yaml();

    public static void loadAllLanguages() {
        CACHE.clear();

        File langRoot = new File(LangManager.LANG_DIR);
        File[] langDirs = langRoot.listFiles(File::isDirectory);

        if (langDirs == null || langDirs.length == 0) {
            Logger.getLogger().log(LogType.ERROR,"loader","language",
                    "Не найдено ни одной локали в /language/");
            return;
        }

        for (File langDir : langDirs) {
            String langCode = langDir.getName().toLowerCase(Locale.ROOT);
            Map<String, String> flat = new HashMap<>();

            loadRecursive(langDir, "", flat);

            CACHE.put(langCode, flat);
            Logger.getLogger().log(LogType.INFO,"loader","language",
                    "Загружено " + flat.size() + " ключей для языка " + langCode);
        }
    }

    private static void loadRecursive(File folder, String prefix, Map<String, String> out) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.isDirectory()) {
                loadRecursive(f, prefix + f.getName() + ".", out);
                continue;
            }

            if (!f.getName().endsWith(".yml")) continue;

            String fileKey = prefix + f.getName().replace(".yml", "") + ".";

            try (InputStream is = new FileInputStream(f)) {
                Map<String, Object> yaml = YAML.load(is);
                if (yaml != null) flatten(fileKey, yaml, out);
            } catch (Exception e) {
                Logger.getLogger().log(LogType.ERROR,"loader","language",
                        "Ошибка загрузки файла " + f.getPath(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void flatten(String prefix, Map<?, ?> src, Map<String, String> out) {
        for (var entry : src.entrySet()) {
            String key = prefix + entry.getKey();
            Object val = entry.getValue();

            if (val instanceof Map<?,?> map) {
                flatten(key + ".", map, out);
            } else if (val instanceof List<?> list) {
                out.put(key, String.join("\n",
                        list.stream().map(Object::toString).toList()));
            } else if (val != null) {
                out.put(key, val.toString());
            }
        }
    }
}
