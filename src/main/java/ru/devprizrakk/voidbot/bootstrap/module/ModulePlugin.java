package ru.devprizrakk.voidbot.bootstrap.module;

import ru.devprizrakk.voidbot.CoreContext;

public interface ModulePlugin {
    String getName();
    String getVersion();
    void onLoad(CoreContext coreContext);
    void onEnable();
    void onDisable();
}