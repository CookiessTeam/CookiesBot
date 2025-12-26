package ru.devprizrakk.voidbot.config;

import ru.devprizrakk.voidbot.utils.Utils;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;

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
                Logger.getLogger().log(LogType.ERROR, "config", "Default config.yml not found!", new FileNotFoundException("Default config.yml not found!"));

            Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Logger.getLogger().log(LogType.ERROR, "config", "Failed to copy default config.yml", e);
        }
    }
}
