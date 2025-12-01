package ru.devprizrakk.voidbot.core.loader.libraries;

import ru.devprizrakk.voidbot.core.system.logger.LogType;
import ru.devprizrakk.voidbot.core.system.logger.Logger;

import java.util.List;

public class LibraryManager {

    private final String jsonUrl = "https://mirror.devprizrakk.ru/voidforge/bot/libraries/libraries.json";
    private final String libDir = "libraries";

    public void init() {
        try {
            Logger.getLogger().log(LogType.INFO,"loader","libraries", "Проверка библиотек...");

            LibraryDownloader downloader = new LibraryDownloader(jsonUrl, libDir);
            List<String> failed = downloader.downloadAll();

            if (!failed.isEmpty()) {
                Logger.getLogger().log(LogType.ERROR,"loader","libraries",
                        "Не удалось скачать библиотеки:");
                failed.forEach(s -> Logger.getLogger().log(LogType.ERROR,"loader","libraries", s));

                System.exit(1);
            }

            Logger.getLogger().log(LogType.INFO,"loader","libraries", "Все библиотеки скачаны!");

            // Загружаем JAR в рантайме
            LibraryClassLoader.loadAll(libDir);

            Logger.getLogger().log(LogType.INFO,"loader","libraries", "Библиотеки подключены!");
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR,"loader","libraries", "Ошибка!", e);
            System.exit(1);
        }
    }
}
