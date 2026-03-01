package ru.devprizrakk.voidbot.api.bootstrap.libraries;

import ru.devprizrakk.voidbot.api.logging.LogType;
import ru.devprizrakk.voidbot.api.logging.Logger;

import java.util.List;

public class LibraryManager {

    public void init() {
        try {
            Logger.getLogger().log(LogType.INFO,"loader", "Проверка библиотек...");

            String jsonUrl = "https://mirror.devprizrakk.ru/voidforge/bot/libraries/libraries.json";
            String libDir = "libraries";
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
