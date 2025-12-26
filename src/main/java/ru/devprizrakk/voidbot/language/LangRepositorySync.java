package ru.devprizrakk.voidbot.language;

import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;

public class LangRepositorySync {

    private static final String GITHUB_ZIP =
            "https://github.com/voidforge-community/language/archive/refs/heads/main.zip";

    private static final Path ETAG_FILE = Paths.get(".lang_etag");

    public static void sync() {
        try {
            Path langDir = Paths.get(LangManager.LANG_DIR);

            // Если папка отсутствует — скачиваем всегда
            if (!Files.exists(langDir)) {
                Logger.getLogger().log(LogType.INFO,"loader",
                        "Папка локализаций отсутствует — скачиваю...");
                forceSync();
                return;
            }

            Logger.getLogger().log(LogType.INFO,"loader", "Проверяю обновления локализаций...");

            if (!checkIfUpdated()) {
                Logger.getLogger().log(LogType.INFO,"loader",
                        "Локализации актуальны, скачивание не требуется.");
                return;
            }

            Logger.getLogger().log(LogType.INFO,"loader",
                    "Найдены изменения, скачиваю локализации из GitHub...");

            forceSync();

        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR,"loader",
                    "Ошибка загрузки локализаций из GitHub", e);
        }
    }

    private static void forceSync() throws Exception {
        Path tmp = Paths.get("localisation.zip");

        // Скачивание
        try (InputStream in = new URL(GITHUB_ZIP).openStream()) {
            Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
        }

        Path langDir = Paths.get(LangManager.LANG_DIR);

        // Очистка
        if (Files.exists(langDir)) deleteDirectory(langDir.toFile());
        Files.createDirectories(langDir);

        // Распаковка
        unzip(tmp, langDir);

        // Удаление ZIP
        Files.deleteIfExists(tmp);

        Logger.getLogger().log(LogType.INFO,"loader",
                "Локализации успешно обновлены.");
    }


    /**
     * Проверяет, изменился ли ZIP на GitHub (по ETag)
     */
    private static boolean checkIfUpdated() {
        try {
            URL url = new URL(GITHUB_ZIP);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");

            String remoteEtag = conn.getHeaderField("ETag");

            // GitHub иногда отдаёт null → fallback на Last-Modified
            if (remoteEtag == null)
                remoteEtag = conn.getHeaderField("Last-Modified");

            if (remoteEtag == null)
                return true; // не можем проверить → считаем что обновилось

            String savedEtag = null;
            if (Files.exists(ETAG_FILE))
                savedEtag = Files.readString(ETAG_FILE);

            // Если совпадает → нет обновлений
            if (remoteEtag.equals(savedEtag)) {
                return false;
            }

            // Обновилось → сохраняем новый ETag
            Files.writeString(ETAG_FILE, remoteEtag, StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            return true;

        } catch (Exception e) {
            return true; // если ошибка — скачиваем на всякий случай
        }
    }

    /**
     * Распаковка ZIP
     */
    private static void unzip(Path zipFile, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();

                // Ищем только содержимое language/
                int idx = name.indexOf("language/");
                if (idx == -1) continue;

                // Оставляем часть после "language/"
                String relative = name.substring(idx + "language/".length());

                if (relative.isEmpty()) {
                    zis.closeEntry();
                    continue;
                }

                Path out = targetDir.resolve(relative);

                if (entry.isDirectory()) {
                    Files.createDirectories(out);
                } else {
                    Files.createDirectories(out.getParent());
                    Files.copy(zis, out, StandardCopyOption.REPLACE_EXISTING);
                }

                zis.closeEntry();
            }
        }
    }


    private static void deleteDirectory(File dir) {
        if (!dir.exists()) return;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) deleteDirectory(f);
                else f.delete();
            }
        }
        dir.delete();
    }
}
