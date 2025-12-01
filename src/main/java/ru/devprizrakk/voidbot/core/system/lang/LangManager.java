package ru.devprizrakk.voidbot.utils;

import net.dv8tion.jda.api.entities.Member;
import org.yaml.snakeyaml.Yaml;
import ru.devprizrakk.voidbot.core.Utils;
import ru.devprizrakk.voidbot.core.system.logger.LogType;
import ru.devprizrakk.voidbot.core.system.logger.Logger;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

/**
 * 🌐 Глобальный менеджер локалей для VoidBot.
 *
 * 📦 Поддерживает структуру:
 * language/<lang>/(system.yml, command/system/help.yml, ...)
 *
 * - Если нет папки language рядом — копирует из ресурсов (resources/language)
 * - Загружает все файлы в память (автоматическое объединение ключей)
 * - Поддерживает placeholder'ы (%name%)
 * - Можно просто вызывать:
 *   LangManager.get("command.system.help.title");
 */
public class LangManager extends Utils {

    private static final String LANG_DIR = "language";
    private static final String DEFAULT_LANG = "ru";
    private static final Yaml YAML = new Yaml();

    // Кэш: язык → ключ → значение
    private static final Map<String, Map<String, String>> CACHE = new HashMap<>();

    // -------------------- INIT --------------------

    public static void init() {
        File langFolder = new File(LANG_DIR);
        if (!langFolder.exists() || Objects.requireNonNull(langFolder.listFiles()).length == 0) {
            getLogger().warn("loader","language","Папка language пуста — копирую языки из ресурсов...");
            copyLanguagesFromResources();
        }
        loadAllLanguages();
        watchForChanges();
    }

    // -------------------- LOAD --------------------

    private static void loadAllLanguages() {
        CACHE.clear();
        File[] langDirs = new File(LANG_DIR).listFiles(File::isDirectory);
        if (langDirs == null || langDirs.length == 0) {
            getLogger().error("loader","language","Не найдено ни одной локали в /language/");
            return;
        }

        for (File langDir : langDirs) {
            String langCode = langDir.getName().toLowerCase(Locale.ROOT);
            Map<String, String> flatData = new HashMap<>();
            loadRecursively(langDir, "", flatData);
            CACHE.put(langCode, flatData);
            getLogger().info("loader","language","Загружено " + flatData.size() + " ключей для языка " + langCode);
        }
    }

    private static void loadRecursively(File folder, String prefix, Map<String, String> data) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                loadRecursively(file, prefix + file.getName() + ".", data);
                continue;
            }
            if (!file.getName().endsWith(".yml")) continue;

            String fileKey = prefix + file.getName().replace(".yml", "") + ".";
            try (InputStream is = new FileInputStream(file)) {
                Map<String, Object> yamlData = YAML.load(is);
                if (yamlData != null) flattenMap(fileKey, yamlData, data);
            } catch (IOException e) {
                getLogger().error("loader","language","Ошибка при загрузке " + file.getPath(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void flattenMap(String prefix, Map<String, Object> src, Map<String, String> target) {
        for (var entry : src.entrySet()) {
            String key = prefix + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map<?, ?> map) {
                flattenMap(key + ".", (Map<String, Object>) map, target);
            } else if (value instanceof List<?> list) {
                target.put(key, String.join("\n", list.stream().map(Object::toString).toList()));
            } else if (value != null) {
                target.put(key, value.toString());
            }
        }
    }

    // -------------------- GET --------------------

    public static String get(String key) {
        return get(DEFAULT_LANG, key);
    }

    public static String get(Member member, String key) {
        return get(DEFAULT_LANG, key);
    }
    public static String get(String lang, String filePath, String key) {
        File file = Paths.get(LANG_DIR, lang, filePath).toFile();
        if (!file.exists()) return "§cFile not found: " + filePath;

        try (InputStream is = new FileInputStream(file)) {
            Map<String, Object> yamlData = YAML.load(is);
            if (yamlData == null) return "§cEmpty file: " + filePath;
            Object result = getNestedValue(yamlData, key);
            return result != null ? result.toString() : "§cMissing key: " + key;
        } catch (Exception e) {
            getLogger().error("loader","language","Ошибка чтения " + filePath, e);
            return "§cError reading: " + filePath;
        }
    }
    @SuppressWarnings("unchecked")
    private static Object getNestedValue(Map<String, Object> data, String key) {
        String[] parts = key.split("\\.");
        Object current = data;

        for (String part : parts) {
            if (!(current instanceof Map<?, ?> map)) return null;
            current = map.get(part);
            if (current == null) return null;
        }
        return current;
    }

    public static String get(String lang, String key) {
        Map<String, String> langData = CACHE.get(lang.toLowerCase(Locale.ROOT));
        if (langData == null) {
            getLogger().warn("loader","language","Язык не найден: " + lang + ", использую " + DEFAULT_LANG);
            langData = CACHE.get(DEFAULT_LANG);
        }
        if (langData == null) return "§c[No language loaded]";
        return langData.getOrDefault(key, "§cMissing key: " + key);
    }

    public static String get(String lang, String key, Map<String, String> placeholders) {
        String message = get(lang, key);
        for (var entry : placeholders.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return message;
    }

    // -------------------- RELOAD --------------------

    public static void reload() {
        getLogger().info("loader","language","Перезагружаю языки...");
        loadAllLanguages();
    }

    // -------------------- WATCHER --------------------

    private static void watchForChanges() {
        new Thread(() -> {
            try {
                WatchService watcher = FileSystems.getDefault().newWatchService();
                Path langPath = Paths.get(LANG_DIR);
                langPath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);

                while (true) {
                    WatchKey key = watcher.take();
                    boolean needReload = false;

                    for (WatchEvent<?> event : key.pollEvents()) {
                        String fileName = event.context().toString();
                        if (fileName.endsWith(".yml")) {
                            getLogger().info("loader","language","Изменён файл локали: " + fileName + " — перезагрузка...");
                            needReload = true;
                        }
                    }

                    if (needReload) loadAllLanguages();
                    key.reset();
                }
            } catch (Exception e) {
                getLogger().error("loader","language","Ошибка при отслеживании изменений в языках", e);
            }
        }, "LangWatcher").start();
    }

    // -------------------- COPY DEFAULT --------------------

    private static void copyLanguagesFromResources() {
        try {
            ClassLoader cl = LangManager.class.getClassLoader();
            List<String> langs = List.of("ru", "en");

            for (String lang : langs) {
                Enumeration<URL> resources = cl.getResources("language/" + lang);
                if (!resources.hasMoreElements()) continue;

                getLogger().info("loader","language","Копирую язык из ресурсов: " + lang);
                copyDirectoryFromResources("language/" + lang, Paths.get(LANG_DIR, lang));
            }
        } catch (IOException e) {
            getLogger().error("loader","language","Не удалось скопировать языки из ресурсов", e);
        }
    }

    private static void copyDirectoryFromResources(String resourcePath, Path targetDir) throws IOException {
        ClassLoader cl = LangManager.class.getClassLoader();
        Files.createDirectories(targetDir);

        // ⚠️ Если ты собираешь через Maven — явно укажи список файлов.
        String[] files = {
                "system.yml",
                "command/system/help.yml",
                "command/fun/emote.yml"
        };

        for (String file : files) {
            try (InputStream is = cl.getResourceAsStream(resourcePath + "/" + file)) {
                if (is == null) continue;
                Path targetFile = targetDir.resolve(file);
                Files.createDirectories(targetFile.getParent());
                Files.copy(is, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
