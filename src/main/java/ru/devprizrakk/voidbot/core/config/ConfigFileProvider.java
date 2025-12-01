package ru.devprizrakk.voidbot.core.config;

import ru.devprizrakk.voidbot.core.Utils;
import ru.devprizrakk.voidbot.core.system.logger.LogType;
import ru.devprizrakk.voidbot.core.system.logger.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ConfigFileProvider extends Utils {
    private static final String CONFIG_NAME = "config.yml";

    public File provideConfigFile() {
        File file = new File(CONFIG_NAME);

        if (!file.exists()) {
            extractDefault(file);
        }

        return file;
    }

    private void extractDefault(File file) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_NAME)) {
            if (input == null)
                Logger.getLogger().log(LogType.ERROR, "config", "init", "Default config.yml not found!", new FileNotFoundException("Default config.yml not found!"));

            Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Logger.getLogger().log(LogType.ERROR, "config", "init", "Failed to copy default config.yml", e);
        }
    }
}
