package ru.devprizrakk.voidbot.config;

import java.io.File;
import java.util.Map;

public class ConfigManager {

    private final File configFile;
    private final Config config;
    private final YamlConfigLoader loader = new YamlConfigLoader();

    public ConfigManager() {
        ConfigFileProvider provider = new ConfigFileProvider();
        this.configFile = provider.provideConfigFile();

        Map<String, Object> properties = loader.load(configFile);
        this.config = new Config(properties);
    }

    public Config getConfig() {
        return config;
    }

    public void save() {
        loader.save(configFile, config.properties());
    }
}
