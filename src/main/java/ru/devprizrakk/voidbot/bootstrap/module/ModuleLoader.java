package ru.devprizrakk.voidbot.bootstrap.module;

import ru.devprizrakk.voidbot.CoreContext;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

public class ModuleLoader {

    private final ModuleRegistry registry;
    private final CoreContext context;

    public ModuleLoader(ModuleRegistry registry, CoreContext context) {
        this.registry = registry;
        this.context = context;
    }

    public void loadModules(File dir) {
        if (!dir.exists()) dir.mkdirs();

        File[] jars = dir.listFiles((file, name) -> name.endsWith(".jar"));
        if (jars == null || jars.length == 0) {
            ModuleLogger.logInfo("No modules found in " + dir.getAbsolutePath());
            return;
        }

        for (File jar : jars) {
            loadJar(jar);
        }
    }

    private void loadJar(File jar) {
        try (URLClassLoader classLoader = new URLClassLoader(
                new URL[]{jar.toURI().toURL()},
                this.getClass().getClassLoader())) {

            try (JarFile jarFile = new JarFile(jar)) {
                var entries = jarFile.entries();

                while (entries.hasMoreElements()) {
                    var entry = entries.nextElement();
                    if (!entry.getName().endsWith(".class")) continue;

                    String className = entry.getName().replace("/", ".").replace(".class", "");

                    try {
                        Class<?> cls = classLoader.loadClass(className);

                        if (ModulePlugin.class.isAssignableFrom(cls)) {
                            ModulePlugin plugin = (ModulePlugin) cls.getDeclaredConstructor().newInstance();
                            plugin.onLoad(context);
                            registry.register(plugin);
                            ModuleLogger.logLoaded(plugin.getName());
                        }

                    } catch (Exception e) {
                        ModuleLogger.logFailed(className, e);
                    }
                }
            }

        } catch (Exception e) {
            ModuleLogger.logFailed(jar.getName(), e);
        }
    }
}
