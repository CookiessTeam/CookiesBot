package ru.devprizrakk.voidbot.bootstrap.module;

import java.util.ArrayList;
import java.util.List;

public class ModuleRegistry {

    private final List<ModulePlugin> loadedModules = new ArrayList<>();

    public void register(ModulePlugin plugin) {
        loadedModules.add(plugin);
    }

    public List<ModulePlugin> getLoadedModules() {
        return loadedModules;
    }

    public void enableAll() {
        for (ModulePlugin plugin : loadedModules) {
            try {
                plugin.onEnable();
                ModuleLogger.logEnabled(plugin.getName());
            } catch (Exception e) {
                ModuleLogger.logFailed(plugin.getName(), e);
            }
        }
    }

    public void disableAll() {
        for (ModulePlugin plugin : loadedModules) {
            try {
                plugin.onDisable();
                ModuleLogger.logDisabled(plugin.getName());
            } catch (Exception e) {
                ModuleLogger.logFailed(plugin.getName(), e);
            }
        }
    }
}
