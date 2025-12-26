package ru.devprizrakk.voidbot.core.system.lang;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import ru.devprizrakk.voidbot.core.Utils;
import ru.devprizrakk.voidbot.core.system.applicationinfo.ApplicationInfo;
import ru.devprizrakk.voidbot.core.system.applicationinfo.Module;
import ru.devprizrakk.voidbot.utils.Utils;
import ru.devprizrakk.voidbot.utils.applicationinfo.ApplicationInfo;
import ru.devprizrakk.voidbot.utils.applicationinfo.Module;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LangHelper {
    private static final Pattern pattern = Pattern.compile("%([a-zA-Z0-9_-]+)(?::([a-zA-Z0-9_-]+))?%");
    IReplyCallback event;
    public LangHelper(IReplyCallback event) {
        this.event = event;
    }


    private String formatter(String message) {
        Matcher matcher = pattern.matcher(message);
        StringBuffer sb = new StringBuffer();

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
        String message = LangManager.get("ru",src, path);
        if (!(message == null || message.startsWith("§cMissing key") || message.startsWith("§c[No language loaded]"))) {
            return formatter(message);
        } else {
            event.replyEmbeds(Utils.getErrorMessage(event).wrongError("Ключ/файл локализации не найден!").build()).queue();
            return null;
        }
    }
    public String getInfoLocale(String src, String path) {
        String message = LangManager.get("ru",src, path);
        if (!(message == null || message.startsWith("§cMissing key") || message.startsWith("§c[No language loaded]"))) {
            return formatter(message);
        } else {
            return "Localisation key is not found please report administration!";
        }
    }
}
