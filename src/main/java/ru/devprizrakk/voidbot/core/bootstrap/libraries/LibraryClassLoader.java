package ru.devprizrakk.voidbot.core.bootstrap.libraries;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class LibraryClassLoader {

    public static void loadAll(String libDir) throws Exception {
        File folder = new File(libDir);
        File[] jars = folder.listFiles((d, name) -> name.endsWith(".jar"));

        if (jars == null) return;

        URL[] urls = new URL[jars.length];

        for (int i = 0; i < jars.length; i++) {
            urls[i] = jars[i].toURI().toURL();
        }

        URLClassLoader cl = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
        Thread.currentThread().setContextClassLoader(cl);
    }
}
