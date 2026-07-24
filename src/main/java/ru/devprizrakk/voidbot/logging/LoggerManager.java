package ru.devprizrakk.voidbot.logging;

import ru.devprizrakk.voidbot.utils.ColorConsole;
import ru.devprizrakk.voidbot.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LoggerManager {

    private static final String LOG_DIR = "logs";
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter FILE_DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FILE_TIME_FMT = DateTimeFormatter.ofPattern("HH-mm-ss");

    private final LogType minLevel;
    private final ConcurrentLinkedQueue<String> writeQueue = new ConcurrentLinkedQueue<>();
    private final PrintStream originalOut;
    private final PrintStream originalErr;
    private final Thread fileFlushThread;
    private final String logFileName;

    public LoggerManager() {
        minLevel = resolveMinLevel();
        originalOut = System.out;
        originalErr = System.err;
        setupLogDir();
        logFileName = resolveLogFileName();
        originalOut.println("Logging to: " + logFileName);
        interceptSystemStreams();
        installUncaughtExceptionHandler();
        fileFlushThread = new Thread(this::flushLoop, "LogFileWriter");
        fileFlushThread.setDaemon(true);
        fileFlushThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(this::flushRemaining, "LogShutdownHook"));
    }

    private String resolveLogFileName() {
        LocalDateTime now = LocalDateTime.now();
        return LOG_DIR + "/" + now.format(FILE_DATE_FMT) + "-" + now.format(FILE_TIME_FMT) + ".log";
    }

    private LogType resolveMinLevel() {
        try {
            boolean debug = Boolean.parseBoolean(
                    Utils.getConfig().getString("system.debug.enable", "false"));
            String level = Utils.getConfig().getString("system.debug.level", "low");

            if (debug) {
                return switch (level.toLowerCase()) {
                    case "high" -> LogType.TRACE;
                    case "medium" -> LogType.DEBUG;
                    default -> LogType.INFO;
                };
            }
            return LogType.INFO;
        } catch (Exception e) {
            return LogType.INFO;
        }
    }

    private void setupLogDir() {
        try {
            Files.createDirectories(Paths.get(LOG_DIR));
        } catch (IOException e) {
            originalErr.println("Cannot create logs directory: " + e.getMessage());
        }
    }

    private void interceptSystemStreams() {
        System.setOut(new InterceptedPrintStream(originalOut, LogType.INFO, "STDOUT"));
        System.setErr(new InterceptedPrintStream(originalErr, LogType.ERROR, "STDERR"));
    }

    private void installUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->
                log(LogType.ERROR, "EXCEPTION",
                        "Uncaught exception in thread \"" + thread.getName() + "\"", throwable));
    }

    public void log(LogType type, String category, String message) {
        if (type.ordinal() < minLevel.ordinal()) return;
        String consoleLine = formatConsole(type, category, message);
        String fileLine = stripAnsi(formatFile(type, category, message));
        if (type == LogType.ERROR) {
            originalErr.println(consoleLine);
        } else {
            originalOut.println(consoleLine);
        }
        writeQueue.add(fileLine);
    }

    public void log(LogType type, String category, String message, Throwable throwable) {
        if (type.ordinal() < minLevel.ordinal()) return;
        log(type, category, message);

        if (throwable != null) {
            printThrowable(type, category, throwable);
            for (Throwable cause = throwable.getCause(); cause != null && cause != throwable; cause = cause.getCause()) {
                String causeLine = "Caused by: " + cause;
                String consoleLine = formatConsole(type, category, causeLine);
                originalErr.println(consoleLine);
                writeQueue.add(stripAnsi(formatFile(type, category, causeLine)));
                for (StackTraceElement element : cause.getStackTrace()) {
                    String traceLine = "\tat " + element;
                    originalOut.println(formatConsole(type, category, traceLine));
                    writeQueue.add(stripAnsi(formatFile(type, category, traceLine)));
                }
            }
        }
    }

    private void printThrowable(LogType type, String category, Throwable throwable) {
        String consoleLine = formatConsole(type, category, throwable.toString());
        originalErr.println(consoleLine);
        writeQueue.add(stripAnsi(formatFile(type, category, throwable.toString())));
        for (StackTraceElement element : throwable.getStackTrace()) {
            String traceLine = "\tat " + element;
            originalOut.println(formatConsole(type, category, traceLine));
            writeQueue.add(stripAnsi(formatFile(type, category, traceLine)));
        }
    }

    void directLog(LogType type, String category, String message) {
        if (type.ordinal() < minLevel.ordinal()) return;
        String consoleLine = formatConsole(type, category, message);
        String fileLine = stripAnsi(formatFile(type, category, message));
        if (type == LogType.ERROR) {
            originalErr.println(consoleLine);
        } else {
            originalOut.println(consoleLine);
        }
        writeQueue.add(fileLine);
    }

    private String formatConsole(LogType type, String category, String message) {
        String time = LocalDateTime.now().format(TIME_FMT);
        String levelStr = padLeft(type.name(), 5);
        String color = colorFor(type);
        String cat = padRight(category != null ? category : "", 10);

        return String.format("[%s%s%s] [%s%s%s] [%s%s%s] %s",
                ColorConsole.ANSI_PURPLE, time, ColorConsole.ANSI_RESET,
                color, levelStr, ColorConsole.ANSI_RESET,
                ColorConsole.ANSI_CYAN, cat, ColorConsole.ANSI_RESET,
                message
        );
    }

    private String formatFile(LogType type, String category, String message) {
        String time = LocalDateTime.now().format(TIME_FMT);
        String levelStr = padLeft(type.name(), 5);
        String cat = padRight(category != null ? category : "", 10);
        return String.format("[%s] [%s] [%s] %s", time, levelStr, cat, message);
    }

    private String colorFor(LogType type) {
        return switch (type) {
            case TRACE -> ColorConsole.ANSI_PURPLE;
            case DEBUG -> ColorConsole.ANSI_BLUE;
            case INFO -> ColorConsole.ANSI_GREEN;
            case WARN -> ColorConsole.ANSI_YELLOW;
            case ERROR -> ColorConsole.ANSI_RED;
        };
    }

    private String padRight(String text, int length) {
        if (text == null) return " ".repeat(length);
        return String.format("%-" + length + "s", text);
    }

    private String padLeft(String text, int length) {
        if (text == null) return " ".repeat(length);
        return String.format("%" + length + "s", text);
    }

    private static String stripAnsi(String s) {
        return s.replaceAll("\u001b\\[[0-9;]*m", "");
    }

    private void flushLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000);
                drainQueue();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                originalErr.println("Logger flush error: " + e.getMessage());
            }
        }
    }

    private synchronized void drainQueue() {
        if (writeQueue.isEmpty()) return;

        try (OutputStream os = Files.newOutputStream(Paths.get(logFileName), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
             PrintStream ps = new PrintStream(os, true)) {
            String line;
            while ((line = writeQueue.poll()) != null) {
                ps.println(line);
            }
        } catch (IOException e) {
            originalErr.println("Cannot write log file: " + e.getMessage());
        }
    }

    public void flushRemaining() {
        drainQueue();
        if (fileFlushThread != null) fileFlushThread.interrupt();
        originalOut.println("Logs saved to: " + logFileName);
    }

    // --- Intercepted PrintStream: перехват System.out / System.err ---

    private class InterceptedPrintStream extends PrintStream {
        private final LogType level;
        private final String category;
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        InterceptedPrintStream(PrintStream original, LogType level, String category) {
            super(original, true);
            this.level = level;
            this.category = category;
        }

        @Override
        public void println(String x) {
            if (LoggerManager.this.minLevel.ordinal() <= level.ordinal()) {
                directLog(level, category, x != null ? x : "null");
            }
        }

        @Override
        public void println(Object x) {
            println(String.valueOf(x));
        }

        @Override
        public void print(String x) {
            if (x != null) buffer.write(x.getBytes(), 0, x.length());
            if (x != null && x.contains("\n")) flushBuffer();
        }

        @Override
        public void print(Object x) {
            print(String.valueOf(x));
        }

        @Override
        public void write(byte[] b, int off, int len) {
            for (int i = off; i < off + len; i++) {
                if (b[i] == '\n') {
                    flushBuffer();
                } else {
                    buffer.write(b[i]);
                }
            }
        }

        @Override
        public void write(int b) {
            if (b == '\n') flushBuffer();
            else buffer.write(b);
        }

        private void flushBuffer() {
            if (buffer.size() == 0) return;
            String line = buffer.toString();
            buffer.reset();
            directLog(level, category, line);
        }
    }
}