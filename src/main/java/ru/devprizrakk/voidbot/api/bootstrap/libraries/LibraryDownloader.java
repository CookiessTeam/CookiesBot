package ru.devprizrakk.voidbot.api.bootstrap.libraries;

import ru.devprizrakk.voidbot.api.logging.LogType;
import ru.devprizrakk.voidbot.api.logging.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryDownloader {

    private final String jsonUrl;
    private final String libDir;

    public LibraryDownloader(String jsonUrl, String libDir) {
        this.jsonUrl = jsonUrl;
        this.libDir = libDir;
    }

    public List<String> downloadAll() throws IOException {
        List<String> failed = new ArrayList<>();
        Files.createDirectories(Paths.get(libDir));

        List<String> libs = fetchLibraryList();
        int totalLibs = libs.size();
        int done = 0;

        for (String jarName : libs) {
            Path localPath = Paths.get(libDir, jarName);

            double globalPercent = ((double) done / totalLibs) * 100;

            Logger.getLogger().log(LogType.INFO, "loader", "----------------------------");
            Logger.getLogger().log(LogType.INFO, "loader",
                    "| Загружено библиотек: " + (int) globalPercent + "% |");

            if (Files.exists(localPath)) {
                Logger.getLogger().log(LogType.INFO, "loader",
                        "| Уже скачано: " + jarName + " |");
                Logger.getLogger().log(LogType.INFO, "loader", "----------------------------");
                done++;
                continue;
            }

            Logger.getLogger().log(LogType.INFO, "loader",
                    "| Скачиваю файл: " + jarName + " |");

            try {
                downloadProgressBar(jsonUrl.replace("libraries.json","") + jarName, localPath);
                Logger.getLogger().log(LogType.INFO,"loader",
                        "| Скачано: " + jarName + " |");
                Logger.getLogger().log(LogType.INFO,"loader",
                        "| Статус: скачано        |");
            } catch (Exception e) {
                failed.add(jarName + " (" + e.getMessage() + ")");
                Logger.getLogger().log(LogType.INFO,"loader",
                        "| Статус: не удалось     |");
            }

            Logger.getLogger().log(LogType.INFO, "loader", "----------------------------");
            done++;
        }

        return failed;
    }


    private List<String> fetchLibraryList() throws IOException {
        URL url = new URL(jsonUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String json = reader.lines().reduce("", String::concat).trim();

            if (!json.startsWith("[") || !json.endsWith("]"))
                throw new IOException("Неверный формат JSON");

            json = json.substring(1, json.length() - 1);
            String[] arr = json.split(",");

            List<String> libs = new ArrayList<>();
            for (String s : arr) {
                s = s.trim();
                if (s.startsWith("\"") && s.endsWith("\""))
                    libs.add(s.substring(1, s.length() - 1));
            }
            return libs;
        }
    }

    private void downloadProgressBar(String urlStr, Path dest) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        int total = conn.getContentLength();

        try (InputStream in = conn.getInputStream();
             OutputStream out = Files.newOutputStream(dest)) {

            byte[] buf = new byte[4096];
            int read;
            int downloaded = 0;
            int lastPercent = -1;

            while ((read = in.read(buf)) != -1) {
                out.write(buf, 0, read);
                downloaded += read;

                int percent = (total > 0) ? (downloaded * 100 / total) : 0;

                if (percent != lastPercent) {
                    System.out.print("\r");
                    printBar(percent);
                    lastPercent = percent;
                }
            }

            printBar(100);
            System.out.println();
        }
    }

    private void printBar(int percent) {
        int width = 30;
        int filled = percent * width / 100;

        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < width; i++)
            sb.append(i < filled ? "=" : " ");
        sb.append("| ").append(percent).append("%");

        System.out.print(sb);
    }
}
