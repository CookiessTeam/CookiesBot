package ru.devprizrakk.voidbot.utils;

import net.dv8tion.jda.api.entities.Member;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * 🌐 Модульный менеджер локалей для VoidBot.
 * Поддерживает структуру:
 * language/<lang>/(system.yml, command/play.yml, etc.)
 * Если нет локалей на диске — копирует из ресурсов.
 */
public class LangManager extends UtilsManager {

    private static final String LANG_DIR = "language";
    private static final String DEFAULT_LANG = "ru";
    private static final Yaml YAML = new Yaml();
    private static final Map<String, Map<String, String>> CACHE = new HashMap<>();

    // -------------------- INIT --------------------

    public static void init() {
        File langFolder = new File(LANG_DIR);
        if (!langFolder.exists() || Objects.requireNonNull(langFolder.listFiles()).length == 0) {
            getLogger().warn("Папка language пуста — копирую языки из ресурсов...");
            copyDefaultLanguagesFromResources();
        }
        loadAllLanguages();
    }

    // -------------------- LOADING --------------------

    private static void loadAllLanguages() {
        CACHE.clear();
        File[] langDirs = new File(LANG_DIR).listFiles(File::isDirectory);

        if (langDirs == null || langDirs.length == 0) {
            getLogger().error("Не найдено ни одной локали в папке /language/");
            return;
        }

        for (File langDir : langDirs) {
            String langCode = langDir.getName().toLowerCase(Locale.ROOT);
            Map<String, String> langData = new HashMap<>();
            loadRecursively(langDir, langData, "");
            CACHE.put(langCode, langData);
            getLogger().info("Загружено " + langData.size() + " ключей для языка " + langCode);
        }
    }

    private static void loadRecursively(File folder, Map<String, String> langData, String prefix) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                loadRecursively(file, langData, prefix + file.getName() + ".");
                continue;
            }
            if (!file.getName().endsWith(".yml")) continue;

            String fileKey = prefix + file.getName().replace(".yml", "");
            try (InputStream is = new FileInputStream(file)) {
                Map<String, Object> yamlData = YAML.load(is);
                if (yamlData != null) flattenMap(fileKey, yamlData, langData);
            } catch (IOException e) {
                getLogger().error("Ошибка при загрузке " + file.getPath(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void flattenMap(String prefix, Map<String, Object> src, Map<String, String> target) {
        for (var entry : src.entrySet()) {
            String key = prefix + "." + entry.getKey();
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

    // -------------------- MESSAGE ACCESS --------------------

    public static String get(String langCode, String key) {
        Map<String, String> langData = CACHE.get(langCode.toLowerCase(Locale.ROOT));
        if (langData == null) {
            getLogger().warn("⚠ Язык не найден: " + langCode + ", использую " + DEFAULT_LANG);
            langData = CACHE.get(DEFAULT_LANG);
        }
        if (langData == null) return "§c[No language loaded]";
        return langData.getOrDefault(key, "§cMissing key: " + key);
    }

    public static String get(String key) {
        return get(DEFAULT_LANG, key);
    }

    public static String get(Member member, String key) {
        // TODO: позже можно добавить систему userLang
        return get(DEFAULT_LANG, key);
    }

    public static String get(String lang, String key, Map<String, String> placeholders) {
        String message = get(lang, key);
        for (var entry : placeholders.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return message;
    }

    // -------------------- UTILITIES --------------------

    public static void reload() {
        getLogger().info("Перезагружаю языки...");
        loadAllLanguages();
    }

    public static Set<String> getAvailableLanguages() {
        return CACHE.keySet();
    }

    // -------------------- COPY DEFAULTS FROM RESOURCES --------------------

    private static void copyDefaultLanguagesFromResources() {
        try {
            // Берём ресурсы из JAR
            ClassLoader cl = LangManager.class.getClassLoader();
            List<String> langs = List.of("ru", "en");

            for (String lang : langs) {
                try (InputStream resourceStream = cl.getResourceAsStream("language/" + lang + "/system.yml")) {
                    if (resourceStream == null) continue; // если нет такого ресурса
                    Path targetDir = Paths.get(LANG_DIR, lang);
                    Files.createDirectories(targetDir);
                    copyDirectoryFromResources("language/" + lang, targetDir);
                    getLogger().info("Скопирована локаль " + lang + " из ресурсов");
                }
            }
        } catch (Exception e) {
            getLogger().error("Не удалось скопировать языки из ресурсов", e);
        }
    }

    private static void copyDirectoryFromResources(String resourcePath, Path targetDir) throws IOException {
        ClassLoader cl = LangManager.class.getClassLoader();
        try (InputStream index = cl.getResourceAsStream(resourcePath)) {
            if (index == null) return;
        }

        // Для сборок Maven/Gradle ресурсы упаковываются в JAR,
        // поэтому лучше вручную указывать файлы в списке ресурсов.
        // Например, system.yml, command/help.yml и т.д.
        String[] files = {"system.yml", "command/help.yml", ""};
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
