package ru.devprizrakk.voidbot.core.config;

import org.yaml.snakeyaml.Yaml;
import ru.devprizrakk.voidbot.core.logging.LogType;
import ru.devprizrakk.voidbot.core.logging.Logger;

import java.io.*;
import java.util.Map;

public class YamlConfigLoader {
    public Map<String, Object> load(File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            return new Yaml().load(inputStream);
        } catch (IOException e) {
            Logger.getLogger().log(LogType.ERROR, "config", "Ошибка загрузки конфигурационного файла", new RuntimeException("Failed to load YAML", e));
            Logger.getLogger().log(LogType.ERROR, "config", "Дальнейшая работа бота не возможна!");
            System.exit(500);
            return null;
        }
    }

    public void save(File file, Map<String, Object> data) {
        try (FileWriter writer = new FileWriter(file)) {
            new Yaml().dump(data, writer);
        } catch (IOException e) {
            Logger.getLogger().log(LogType.ERROR, "config", "Ошибка сохранения конфигурационного файла", new RuntimeException("Failed to save YAML", e));
        }
    }
}
