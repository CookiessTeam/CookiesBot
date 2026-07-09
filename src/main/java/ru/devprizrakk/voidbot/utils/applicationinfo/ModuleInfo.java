package ru.devprizrakk.voidbot.utils.applicationinfo;

public class ModuleInfo {

    Module moduleName;

    public ModuleInfo(Module moduleName) {
        this.moduleName = moduleName;
    }

    public String getVersion() {
        // TODO: Реализовать метод получения версии
        return moduleName.getVersion();
    }

    public String getDescription() {
        // TODO: Реализовать метод получения описания
        return moduleName.getDescription();
    }
}
