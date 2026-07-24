package ru.devprizrakk.voidbot.language;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import ru.devprizrakk.voidbot.exceptions.discord.WrongErrorEmbedFactory;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;
import ru.devprizrakk.voidbot.utils.applicationinfo.Module;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LangHelper {

    private static final Pattern pattern = Pattern.compile("%([a-zA-Z0-9_-]+)(?::([a-zA-Z0-9_-]+))?%");
    private final IReplyCallback event;

    public LangHelper(IReplyCallback event) {
        this.event = event;
    }

    public LangHelper() {
        this.event = null;
    }

    private String formatter(String message) {
        Matcher matcher = pattern.matcher(message);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String key = matcher.group(1);
            String arg = matcher.group(2);

            String replacement = switch (key) {
                case "current-time" -> Utils.getCurrentTime();
                case "version" -> arg != null ? Utils.getModule(Module.valueOf(arg)).getVersion() : "unknown";
                default -> matcher.group();
            };

            matcher.appendReplacement(sb, replacement);
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    public String getDescriptionLocale(String path) {
        String message = LangManager.get("ru", path);
        if (isError(message)) {
            String errorMsg = "Ключ локализации не найден: " + path;
            if (event != null) {
                new WrongErrorEmbedFactory(event).wrongError(errorMsg);
            } else {
                Logger.getLogger().log(LogType.ERROR, "LANG", errorMsg);
            }
            throw new LocalizationException(errorMsg);
        }
        return formatter(message);
    }

    public String getDescriptionLocale(String path, Map<String, String> replacements) {
        String result = getDescriptionLocale(path);
        return applyReplacements(result, replacements);
    }

    public String getInfoLocale(String path) {
        String message = LangManager.get("ru", path);
        if (isError(message)) {
            String errorMsg = "Ключ локализации не найден: " + path;
            if (event != null) {
                new WrongErrorEmbedFactory(event).wrongError(errorMsg);
                throw new LocalizationException(errorMsg);
            } else {
                Logger.getLogger().log(LogType.ERROR, "LANG", errorMsg);
                throw new LocalizationException(errorMsg);
            }
        }
        return formatter(message);
    }

    public String getInfoLocale(String path, Map<String, String> replacements) {
        String result = getInfoLocale(path);
        return applyReplacements(result, replacements);
    }

    private boolean isError(String message) {
        return message == null
                || message.startsWith("§cMissing file")
                || message.startsWith("§cMissing key")
                || message.startsWith("§c[No language loaded]");
    }

    private String applyReplacements(String message, Map<String, String> replacements) {
        if (replacements == null || replacements.isEmpty()) {
            return message;
        }
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%",
                    entry.getValue() == null ? "" : entry.getValue());
        }
        return message;
    }
}