package ru.devprizrakk.voidbot.core.system.lang;

import ru.devprizrakk.voidbot.core.system.logger.*;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;

public class LangRepositorySync {

    private static final String GITHUB_ZIP =
            "https://github.com/voidforge-community/localisation/archive/refs/heads/main.zip";

    public static void sync() {
        try {
            Logger.getLogger().log(LogType.INFO,"loader","language",
                    "Скачиваю локализации из GitHub...");

            Path tmp = Paths.get("localisation.zip");

            // скачиваем zip
            try (InputStream in = new URL(GITHUB_ZIP).openStream()) {
                Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
            }

            // очищаем старую language/
            Path langDir = Paths.get(LangManager.LANG_DIR);
            if (Files.exists(langDir)) {
                deleteDirectory(langDir.toFile());
            }
            Files.createDirectories(langDir);

            // распаковываем
            unzip(tmp, langDir.getParent());

            Files.deleteIfExists(tmp);

            Logger.getLogger().log(LogType.INFO,"loader","language",
                    "Локализации успешно обновлены из GitHub.");

        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR,"loader","language",
                    "Ошибка загрузки локализаций из GitHub", e);
        }
    }

    private static void unzip(Path zipFile, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                // В GitHub ZIP первые папки — localisation-main/language/ru
                if (!name.contains("language/")) continue;

                String relative = name.substring(name.indexOf("language/"));
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
