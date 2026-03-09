# VoidBot

## О проекте
**VoidBot** - бот-помощник для Discord-сообществ, который сочетает развлекательные функции, инструменты модерации и инфраструктурную основу для связи между Discord, сайтом и игровыми сервисами.

Проект развивается как единая платформа управления сообществом: от повседневной модерации и музыкальных команд до будущей интеграции с веб-панелью и игровыми серверами.

## Функционал
- Проигрывание музыки в голосовых каналах (через Lavalink).
- Команды модерации сервера (ban/kick/mute/unmute/unban и др.).
- Развлекательные команды (fun-категория).
- Локализация команд и сообщений (языковые файлы + константы ключей).
- Связующий элемент между сайтом, игровыми серверами и Discord-сообществом (в процессе развития).

## Архитектура и ПО
- Java 25
- JDA 6.3.1
- Lavalink Client 3.2.0 (план обновления до 3.4.0)
- Lavalink Node 4.4.0
- Maven
- SQLite / MySQL (зависит от конфигурации)
- YAML-конфигурации и языковые файлы

## Структура проекта
- `src/main/java/ru/devprizrakk/voidbot/core` - ядро (bootstrap, команды, конфиг, логирование, язык, утилиты).
- `src/main/java/ru/devprizrakk/voidbot/commands` - функциональные команды (`fun`, `music`, `server`).
- `src/main/resources/config.yml` - базовый конфиг по умолчанию.
- `language/` - языковой модуль/локализации.

## Быстрый старт
1. Установить Java 25.
2. Поднять Lavalink Node (если используется музыка).
3. Настроить `config.yml` (токен бота, БД, lavalink, API ключи).
4. Собрать проект:
   ```bash
   mvn clean package
   ```
5. Запустить бота (для voice-части с JDAVE):
   ```bash
   java --enable-native-access=ALL-UNNAMED -jar target/VoidBot-1.0-SNAPSHOT.jar
   ```

## Конфигурация
Основные секции `config.yml`:
- `bot` - токен, id, активность.
- `database` - тип БД и параметры подключения.
- `lavalink` - ноды для аудио.
- `other.youtube-api` - ключ YouTube API (если используется мета по видео).

## Безопасность
- Не храните реальные токены/API-ключи в репозитории.
- Используйте переменные окружения или секрет-хранилище.
- Регулярно ротируйте токены и ключи.

## TODO / Backlog (на будущее)
- [ ] Сделать полноценную очередь музыки:
  - skip, previous, clear, remove-by-index, shuffle, loop(track/queue), autoplay.
- [ ] Реструктуризовать архитектуру бота:
  - разделить слои `domain / application / infrastructure`,
  - убрать сильные статические зависимости (`Utils`, глобальные singleton-паттерны),
  - выделить сервисы и интерфейсы для тестируемости.
- [ ] Добавить новые команды:
  - moderation: warn, purge, slowmode, role/massrole,
  - music: queue, skip, loop, shuffle, seek, lyrics,
  - utility: ping, serverinfo, userinfo, uptime.
- [ ] Реализовать систему уровней (после готовности сайта):
  - события XP, анти-спам, роли за уровни, leaderboard, web-профиль.
- [ ] Ввести миграции БД (Flyway/Liquibase) и версионирование схем.
- [ ] Покрыть критические сценарии тестами (unit + integration):
  - команды, permission-check, time-parser, localization fallback.
- [ ] Усилить обработку ошибок:
  - единый error-code формат, tracing-id, user-safe сообщения.
- [ ] Привести music runtime к стабильному состоянию:
  - корректный lifecycle плеера, reconnect, обработка node failover.
- [ ] Настроить CI/CD:
  - build, tests, static analysis, release artifact.
- [ ] Секьюрность:
  - вынести секреты в env/secret manager, ротация токенов.
- [ ] Документация:
  - ADR по архитектуре, CONTRIBUTING, release-process, command-spec.


