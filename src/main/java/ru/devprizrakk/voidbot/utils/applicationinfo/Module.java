package ru.devprizrakk.voidbot.utils.applicationinfo;

public enum Module {
    BOT("Bot", "2.0.0-beta.5", "Основной модуль"),
    FUN_CALC_ENGINE("Calc Engine", "0.2", "Калькулятор"),
    LIB_JDA("Java Discord Api", "6.3.1", "Библиотека дискорд"),
    JAVA("Java", "17", "Язык программирования"),
    GITHUB_API("Github Api", "1.101", "Библиотека для упрощенного доступа к API github"),
    LAVALINK_CLIENT("LavaLink Client", "3.2.0", "Для подключению и работы с LavaLink Play"),
    SPARK_CORE("Spark Core", "2.9.3", "RestAPI"),
    JAVA_WEBSOCKET("Java WebSocket", "1.5.3", "Подключение к внешним приложениям через WebSocket"),
    SQLITE_JDBC("Sqlite JDBC", "3.44.1.0", "Подключение к БД Sqlite через JDBC"),
    MYSQL_CONNECTOR("MySQL Connector", "8.0.33", "Подключения к БД MySQL через JDBC"),
    GSON("gson", "2.8.9", "Для парсинга JSON (В ближайшее время будет удалена)"),
    SLF4J_API("slf4j-api", "1.7.32", "Библиотека"),
    SNAKEYAML("snakeyaml", "2.0", "Обработка YAML файлов"),
    JLINE("jline", "3.21.0", "Библиотека"),
    LOGBACK_CLASSIC("logback-classic", "1.2.11", "Библиотека");

    private final String name;
    private final String version;
    private final String description;

    Module(String name, String version, String description) {
        this.name = name;
        this.version = version;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }
}
