package ru.devprizrakk.voidbot.bootstrap.libraries;

import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;

import java.util.List;

public class LibraryManager {

    private final String jsonUrl = "https://mirror.devprizrakk.ru/voidforge/bot/libraries/libraries.json";
    private final String libDir = "libraries";

    public void init() {
        try {
            Logger.getLogger().log(LogType.INFO,"loader", "Проверка библиотек...");

            LibraryDownloader downloader = new LibraryDownloader(jsonUrl, libDir);
            List<String> failed = downloader.downloadAll();

            if (!failed.isEmpty()) {
                Logger.getLogger().log(LogType.ERROR,"loader",
                        "Не удалось скачать библиотеки:");
                failed.forEach(s -> Logger.getLogger().log(LogType.ERROR,"loader", s));

                System.exit(1);
            }

            Logger.getLogger().log(LogType.INFO,"loader", "Все библиотеки скачаны!");

            // Загружаем JAR в рантайме
            LibraryClassLoader.loadAll(libDir);

            Logger.getLogger().log(LogType.INFO,"loader", "Библиотеки подключены!");
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR,"loader", "Ошибка!", e);
            System.exit(1);
        }
    }
}
