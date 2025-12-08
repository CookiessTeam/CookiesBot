package ru.devprizrakk.voidbot.core.system.logger;

import ru.devprizrakk.voidbot.core.utils.ColorConsole;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerManager {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


    // Настройка выравнивания — можно подстроить под ширину своих категорий
    private static final int CATEGORY_WIDTH = 10;

    private String getTimestamp() {
        return dateFormat.format(new Date());
    }

    private String padRight(String text, int length) {
        if (text == null) return " ".repeat(length);
        return String.format("%-" + length + "s", text);
    }

    private void print(String level, String category, String colorCode, String message) {
        String timestamp = getTimestamp();

        // Форматированная строка категории и типа с фиксированной шириной
        String categoryPadded = padRight(category, CATEGORY_WIDTH);

        String header = String.format("[%s %s%s%s] [%s] ",
                timestamp,
                colorCode,
                level,
                ColorConsole.ANSI_RESET,
                ColorConsole.ANSI_CYAN + categoryPadded + ColorConsole.ANSI_RESET
        );

        String[] lines = message.split("\n");
        for (String line : lines) {
            System.out.println(header + line);
        }
    }
    public void log(LogType type, String category, String message) {
        switch (type) {
            case INFO -> info(category, message);
            case DEBUG -> debug(category, message);
            case WARN -> warn(category, message);
            case ERROR -> error(category, message);
        }
    }
    public void log(LogType type, String category, String methode, String message, Exception e) {
        switch (type) {
            case INFO -> info(category, message);
            case DEBUG -> debug(category, message);
            case WARN -> warn(category, message);
            case ERROR -> error(category, message, e);
        }
    }

    private void info(String category, String message) {
        print("INFO", category, ColorConsole.ANSI_GREEN, message);
    }

    private void debug(String category, String message) {
        if (true) print("DEBUG", category, ColorConsole.ANSI_BLUE, message);
    }

    private void warn(String category, String message) {
        print("WARN", category, ColorConsole.ANSI_YELLOW, message);
    }

    private void error(String category, String message) {
        print("ERROR", category, ColorConsole.ANSI_RED, message);
    }

    private void error(String category, String message, Exception e) {
        error(category, message);
        if (e != null) {
            print("ERROR", category, ColorConsole.ANSI_RED, e.toString());
            for (StackTraceElement element : e.getStackTrace()) {
                print("ERROR", category, ColorConsole.ANSI_RED, "\tat " + element);
            }
        }
    }
}
