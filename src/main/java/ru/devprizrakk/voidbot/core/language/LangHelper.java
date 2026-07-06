package ru.devprizrakk.voidbot.core.language;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import ru.devprizrakk.voidbot.core.exceptions.discord.WrongErrorEmbedFactory;
import ru.devprizrakk.voidbot.core.utils.applicationinfo.ApplicationInfo;
import ru.devprizrakk.voidbot.core.utils.applicationinfo.Module;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LangHelper {
    private static final Pattern pattern = Pattern.compile("%([a-zA-Z0-9_-]+)(?::([a-zA-Z0-9_-]+))?%");
    IReplyCallback event;

    public LangHelper(IReplyCallback event) {
        this.event = event;
    }

    public LangHelper() {
    }


    private String formatter(String message) {
        Matcher matcher = pattern.matcher(message);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String key = matcher.group(1);     // "version"
            String arg = matcher.group(2);     // "BOT"

            String replacement = switch (key) {
                case "current-time" -> ApplicationInfo.getCurrentTime();
                case "version" -> arg != null ? ApplicationInfo.getModule(Module.valueOf(arg)).getVersion() : "unknown";
                default -> matcher.group(); // если неизвестный — оставить как есть
            };

            matcher.appendReplacement(sb, replacement);
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    public String getDescriptionLocale(String src, String path) {
        String message = LangManager.get("ru", src, path);
        if (message == null) {
            if (new WrongErrorEmbedFactory(event).wrongError("Ключ локализации пустой! (" + "SRC: " + src + " KEY: " + path + ")")) {
                return null;
            }
        } else if (message.startsWith("§cMissing file")) {
            if (new WrongErrorEmbedFactory(event).wrongError("Файл локализации пустой! (" + "SRC: " + src + " KEY: " + path + ")")) {
                return null;
            }

        } else if (message.startsWith("§cMissing key")) {
            if (new WrongErrorEmbedFactory(event).wrongError("Ключ локализации не найден! (" + "SRC: " + src + " KEY: " + path + ")")) {
                return null;
            }
        } else if (message.startsWith("§c[No language loaded]")) {
            if (new WrongErrorEmbedFactory(event).wrongError("Локализация не найдена! (" + "SRC: " + src + " KEY: " + path + ")")) {
                return null;
            }
        } else {
            return formatter(message);
        }
        return null;
    }

    public String getInfoLocale(String src, String path) {
        String message = LangManager.get("ru", src, path);
        if (message == null) {
            return "Localisation key is null! Please report administration!";
        } else if (message.startsWith("§cMissing file")) {
            return "Localisation file is not found! Please report administration!";
        } else if (message.startsWith("§cMissing key")) {
            return "Localisation key is not found! Please report administration!";
        } else if (message.startsWith("§c[No language loaded]")) {
            return "Localisation is not found! Please report administration!";
        } else {
            return formatter(message);
        }
    }

    public String getInfoLocale(String src, String path, Map<String, String> replacements) {
        String message = getInfoLocale(src, path);
        if (replacements == null || replacements.isEmpty()) {
            return message;
        }
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%", entry.getValue() == null ? "" : entry.getValue());
        }
        return message;
    }
}
