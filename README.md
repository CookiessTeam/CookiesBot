# VoidBot

[![Java](https://img.shields.io/badge/Java-25-orange)](https://adoptium.net/)
[![JDA](https://img.shields.io/badge/JDA-6.3.1-blue)](https://github.com/DV8FromTheWorld/JDA)
[![Lavalink](https://img.shields.io/badge/Lavalink-3.4.0-green)](https://github.com/lavalink-devs/Lavalink)
[![Version](https://img.shields.io/badge/version-2.0.0--beta.5-red)]()

**VoidBot** — бот-помощник для Discord-сообществ, объединяющий модерацию, развлекательные функции, систему уровней, аналитику и инфраструктуру для связи Discord ↔ веб-панель ↔ игровые серверы.

---

## Содержание

- [Возможности](#возможности)
- [Технологии](#технологии)
- [Структура проекта](#структура-проекта)
- [Быстрый старт](#быстрый-старт)
- [Конфигурация](#конфигурация)
- [Команды](#команды)
- [Локализация](#локализация)
- [База данных](#база-данных)
- [Безопасность](#безопасность)
- [Roadmap](#roadmap)

---

## Возможности

### Музыка
- Проигрывание музыки из YouTube и других платформ через **Lavalink**
- Управление: play, pause, stop, volume, now-playing с динамическим embed (прогресс-бар, метаданные)

### Модерация
- `/mod ban` / `/mod unban` — бан и разбан (с поддержкой tempban)
- `/mod kick` — кик с причиной
- `/mod mute` / `/mod unmute` — timeout (мут) с длительностью
- `/mod warn` — предупреждение с сохранением в БД и счётчиком
- Все действия модерации записываются в БД (`bans`, `mutes`, `warns`)

### Система уровней (XP)
- **+20% к требуемому опыту за каждый уровень** (формула: `base × growth^level`)
- XP за сообщения (с анти-спам кулдауном) и за минуты в войсе
- **Реалтайм-начисление XP** — каждые 60 секунд, а не при выходе
- Антифарм:
  - Один в войс-канале → без XP
  - Полностью замучен/оглушён (self+guild) → без XP
- Уведомление о новом уровне (в канал или в ЛС)
- Команда `/rank` — **карточка уровня картинкой** (PNG через Java2D)

### Аналитика
- `/serverstats [period]` — аналитика сервера (today / week / all): участники, активность, модерация, топ-5 по XP
- `/serverinfo` — информация о сервере + аналитика из БД
- `/userinfo [user]` — профиль участника: уровни, опыт, войс-тайм, варны, баны, муты

### Обратная связь
- `/feedback` — создаёт embed-«приёмную» с кнопками в отдельном канале
- 3 типа обращений: 💡 идея, ⚠️ жалоба на модератора, 👤 жалоба на пользователя
- Кнопки одобрения/отклонения с embed-уведомлением в ЛС автору

### Развлечения
- `/avatar`, `/calc`, `/coinflip`, `/emote`, `/joke`, `/rps`

---

## Технологии

| Компонент | Версия |
|-----------|--------|
| Java | 25 |
| JDA | 6.3.1 |
| Lavalink Client | 3.4.0 |
| Lavalink Node | 4.4.0 |
| JDAVE (Native Audio) | 0.1.7 |
| UDPQueue (Native Audio) | 0.2.12 |
| Maven | — |
| БД | SQLite / MySQL |
| YAML | SnakeYAML 2.0 |
| GitHub API | 1.101 |

---

## Структура проекта

```
VoidBot/
├── src/main/java/ru/devprizrakk/voidbot/
│   ├── bootstrap/discord/      # запуск JDA, регистрация слушателей
│   ├── command/
│   │   ├── api/                 # ICommand, BaseCommand, CommandRegister, CommandCategory
│   │   └── impl/                # реализации команд по категориям
│   │       ├── fun/             # avatar, calc, coinflip, emote, joke, rps
│   │       ├── music/           # play, pause, stop, volume, nowplaying
│   │       └── server/          # mod (ban/kick/mute/unmute/unban/warn), feedback,
│   │           ├── moderation/  #     help, serverinfo, serverstats
│   │           ├── system/      #     rank, userinfo
│   │           └── user/        #     rank, userinfo
│   ├── config/                  # Config, ConfigManager, YamlConfigLoader
│   ├── database/                # DatabaseManager, DatabaseDialect
│   │   ├── model/               # *Model классы (Ban, User, Experience, ...)
│   │   ├── repository/          # JdbcRepository + репозитории по сущностям
│   │   └── migration/           # MigrationManager + миграции (v1–v11)
│   ├── events/                  # ListenerAdapter'ы + сервисы
│   │   ├── VoiceStateListener   #   войс-сессии, реалтайм XP
│   │   ├── MemberEventListener  #   join/leave/ban/kick
│   │   ├── MessageReceiveListener  # сообщения → XP
│   │   ├── MessageDeleteListener  # удаление сообщений
│   │   ├── FeedbackListener     #   кнопки/модалы обратной связи
│   │   ├── LevelService         #   XP-формула, level-up
│   │   └── StatisticsService    #   дневная/пользовательская статистика
│   ├── exceptions/
│   │   ├── console/             # исключения ядра
│   │   └── discord/             # embed-фабрики ошибок
│   ├── language/                # LangManager, LangLoader, LangHelper, LangWatcher
│   ├── lavalink/                # LavalinkManager, AudioLoader, TrackScheduler
│   ├── logging/                 # Logger, LoggerManager, LogType
│   └── utils/                   # Utils, ApplicationInfo
├── src/main/resources/
│   └── config.yml               # конфигурация по умолчанию
├── language/                    # git-субмодуль локализаций (ru/)
└── pom.xml
```

---

## Быстрый старт

### Требования
- **Java 25** (рекомендуется Liberica JDK 25)
- **Lavalink Node** 4.4.0+ (если используется музыка)
- **Maven** для сборки

### Установка

1. Клонировать репозиторий с субмодулями:
   ```bash
   git clone --recurse-submodules https://github.com/voidforge-community/VoidBot.git
   cd VoidBot
   ```

2. Настроить `config.yml` (скопировать из `src/main/resources/config.yml`):
   ```bash
   cp src/main/resources/config.yml config.yml
   ```
   Заполнить: токен бота, БД, Lavalink-ноды, каналы обратной связи.

3. Собрать:
   ```bash
   mvn clean package
   ```

4. Запустить:
   ```bash
   java --enable-native-access=ALL-UNNAMED -jar target/VoidBot-2.0.0-beta.5.jar
   ```

> **Примечание:** `--enable-native-access=ALL-UNNAMED` требуется для JDAVE (native audio send factory).

---

## Конфигурация

Основные секции `config.yml`:

```yaml
system:
  runtime:
    profile: RELEASE    # dev — не синхронизировать локали с GitHub

bot:
  token: "..."
  activity:
    type: playing       # playing / streaming / competing / watching / listening
    text: "..."

database:
  type: sqlite           # sqlite / mysql
  sqlite:
    url-connect: "jdbc:sqlite:voidbot.db"
  mysql:
    login: "..."
    password: "..."
    url-connect: "jdbc:mysql://127.0.0.1:3306/BotDatabase"

channel:
  feedback:
    message: ""          # канал для embed-«приёмной» обратной связи
    ideas: ""            # куда падают идеи
    moderation: ""       # жалобы на модераторов
    user: ""            # жалобы на участников

levels:
  base-xp: 100           # базовый XP для 1-го уровня
  growth: 1.2            # +20% XP за каждый уровень
  xp-per-message: 5      # XP за сообщение
  xp-per-voice-minute: 5 # XP за минуту в войсе
  message-cooldown-seconds: 60  # анти-спам кулдаун
  channel: ""            # куда level-up уведомления ("" = в ЛС)

lavalink:
  node:
    1:
      name: "..."
      url: "ws://127.0.0.1:2333"
      password: "..."
```

### Runtime profile

| Profile | Поведение |
|---------|-----------|
| `RELEASE` | Синхронизирует локализации с GitHub при запуске (ETag-based) |
| `dev` | Использует локальные файлы, GitHub не трогает — правки не затираются |

---

## Команды

### Fun
| Команда | Описание |
|---------|----------|
| `/avatar [user]` | Аватар участника |
| `/calc <expression>` | Калькулятор |
| `/coinflip` | Орёл или решка |
| `/emote <choice> [user]` | Аниме-эмоции (hug, kiss, pat, ...) через Kawaii API |
| `/joke` | Случайная шутка |
| `/rps <choice>` | Камень-ножницы-бумага |

### Music
| Команда | Описание |
|---------|----------|
| `/play <song>` | Проиграть музыку |
| `/pause` | Пауза |
| `/stop` | Остановить |
| `/volume [0-100]` | Громкость |
| `/now-playing` | Текущий трек с embed |

### Server
| Команда | Описание |
|---------|----------|
| `/mod ban <user> [time] [reason]` | Бан |
| `/mod unban <user>` | Разбан |
| `/mod kick <user> [reason]` | Кик |
| `/mod mute <user> [time] [reason]` | Мут |
| `/mod unmute <user>` | Снять мут |
| `/mod warn <user> [reason]` | Предупреждение |
| `/rank [user]` | Карточка уровня (PNG) |
| `/serverinfo` | Информация о сервере |
| `/serverstats [period]` | Аналитика сервера |
| `/userinfo [user]` | Профиль участника |

### System
| Команда | Описание |
|---------|----------|
| `/help` | Справка по командам |
| `/feedback` | Создать канал обратной связи (скрытая, `MANAGE_SERVER`) |

---

## Локализация

Локализации хранятся в отдельном [git-субмодуле](https://github.com/voidforge-community/language) (`language/`).

```
language/ru/
├── system.yml                          # системные сообщения и ошибки
├── command/
│   ├── fun/                            # avatar, calc, coinflip, emote, joke, rps
│   ├── moderation/                     # ban, kick, mute, unban, unmute, warn
│   ├── music/                          # nowplaying, pause, play, stop, volume
│   ├── server/                         # rank, serverinfo, serverstats, userinfo
│   └── system/                         # feedback, help
├── event/
│   ├── level.yml                       # уведомления о новом уровне
│   └── threads/                        # автосоздание веток
└── readme.md
```

- **Плоский кеш**: при загрузке все YAML-файли склеиваются в один `Map<key, value>`. Ключи глобально уникальны (каждый YAML имеет уникальный корневой префикс).
- **LangWatcher**: отслеживает изменения `.yml` файлов и автоматически перезагружает кеш (горячая замена).
- **`dev` режим**: отключает синхронизацию с GitHub — локальные правки не затираются.

---

## База данных

### Диалекты
Поддерживаются **SQLite** и **MySQL**. `DatabaseDialect` автоматически определяет диалект по URL подключения и адаптирует:
- `idColumn` — `INTEGER PRIMARY KEY AUTOINCREMENT` (SQLite) / `BIGINT PRIMARY KEY AUTO_INCREMENT` (MySQL)
- `boolType` — `INTEGER` (SQLite) / `BOOLEAN` (MySQL)

### Миграции
Своя компактная реализация (без Flyway/Liquibase). Все миграции используют `CREATE TABLE IF NOT EXISTS`.

| Версия | Таблица | Описание |
|--------|---------|---------|
| 1 | `users` | Пользователи (discord_id, joined_at, left_at) |
| 2 | `messages` | Записи сообщений (message_id, channel_id, deleted_at) |
| 3 | `voice_sessions` | Сессии в войсе (joined_at, left_at, duration_seconds) |
| 4 | `member_events` | События участников (JOIN/LEAVE/KICK/BAN/UNBAN) |
| 5 | `warns` | Предупреждения (user, moderator, reason) |
| 6 | `daily_statistics` | Агрегаты по дню (messages, new_members, voice_minutes, ...) |
| 7 | `user_daily_statistics` | Статистика per-user per-day |
| 8 | `server_statistics` | Snapshot сервера (member_count, online, boosts, ...) |
| 9 | `experience` | Уровни и опыт (discord_id, guild_id, level, experience, total) |
| 10 | `bans` | Баны (user, moderator, description, expired_at) |
| 11 | `mutes` | Муты (user, moderator, description, expired_at) |

Миграции применяются автоматически при запуске (`DatabaseManager.init()`), пропуская уже применённые (через таблицу `schema_migrations`).

### Совместимость с SQLite
Драйвер SQLite JDBC не реализует `getGeneratedKeys()`. Эта проблема обработана в `JdbcRepository.generatedId()` — ловит `SQLFeatureNotSupportedException` и возвращает `0`, а репозитории с natural key делают `re-fetch` после вставки.

---

## Безопасность

- Не храните реальные токены и API-ключи в репозитории
- Используйте переменные окружения или секрет-хранилище
- `config.yml` в корне проекта добавлен в `.gitignore` — коммитьте только `src/main/resources/config.yml` с placeholder-значениями
- Регулярно ротируйте токены и ключи

---

## Roadmap

### Музыка
- [ ] Очередь: skip, previous, clear, remove-by-index, shuffle, loop (track/queue), autoplay
- [ ] Корректный lifecycle плеера, reconnect, обработка node failover
- [ ] Команды: queue, skip, loop, shuffle, seek, lyrics

### Модерация
- [ ] purge (массовое удаление сообщений)
- [ ] slowmode
- [ ] role / massrole (массовая выдача ролей)

### Система уровней
- [ ] Роли за уровни (auto-role на N-м уровне)
- [ ] Leaderboard (таблица лидеров)
- [ ] Web-профиль (после готовности сайта)

### Архитектура
- [ ] Реструктуризовать слои: `domain / application / infrastructure`
- [ ] Убрать `Utils` god-class, заменить на DI / context
- [ ] Embedded TTF-шрифт для `RankCardRenderer` (детерминированный рендер)
- [ ] Тесты: unit + integration (команды, permission-check, localization fallback)

### Инфраструктура
- [ ] CI/CD: build, tests, static analysis, release artifact
- [ ] ADR по архитектуре, CONTRIBUTING, release-process
- [ ] Вынести секреты в env / secret manager

---

## Лицензия

Выберите и добавьте лицензию проекта (MIT / Apache-2.0) в отдельный файл `LICENSE`.