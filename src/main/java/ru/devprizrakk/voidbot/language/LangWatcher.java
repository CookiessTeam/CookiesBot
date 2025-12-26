package ru.devprizrakk.voidbot.language;

import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;

import java.nio.file.*;

public class LangWatcher {

    public static void startWatcher() {
        new Thread(() -> {
            try {
                WatchService watcher = FileSystems.getDefault().newWatchService();
                Path path = Paths.get(LangManager.LANG_DIR);

                path.register(watcher,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE);

                while (true) {
                    WatchKey key = watcher.take();

                    boolean reload = false;

                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.context().toString().endsWith(".yml")) {
                            Logger.getLogger().log(LogType.INFO,"loader",
                                    "Изменён файл локали: " + event.context());
                            reload = true;
                        }
                    }

                    if (reload) LangLoader.loadAllLanguages();
                    key.reset();
                }

            } catch (Exception e) {
                Logger.getLogger().log(LogType.ERROR,"loader","language",
                        "Ошибка в Language Watcher", e);
            }
        }, "LangWatcher").start();
    }
}
