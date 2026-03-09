package ru.devprizrakk.voidbot.core.bootstrap.libraries;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LibraryVerifier {

    private final String libDir;

    public LibraryVerifier(String libDir) {
        this.libDir = libDir;
    }

    public List<String> verify() {
        List<String> missing = new ArrayList<>();
        File dir = new File(libDir);

        if (!dir.exists() || !dir.isDirectory()) {
            missing.add("Каталог библиотек не найден: " + libDir);
            return missing;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".jar"));
        if (files == null || files.length == 0) {
            missing.add("Нет jar-файлов в папке " + libDir);
        }

        return missing;
    }
}
