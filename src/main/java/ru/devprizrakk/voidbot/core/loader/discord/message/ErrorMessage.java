package ru.devprizrakk.voidbot.core.loader.discord.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import ru.devprizrakk.voidbot.core.Utils;

import java.awt.*;

public class ErrorMessage {
    IReplyCallback event;
    public ErrorMessage(IReplyCallback event) {
        this.event = event;
    }
    public EmbedBuilder noPermissionExtension(String hasPermission) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.RED);
        embedBuilder.setTitle(Utils.getLangManager(event).getDescriptionLocale("system.yml","system.no-permission.title"));
        embedBuilder.setDescription(Utils.getLangManager(event).getDescriptionLocale("system.yml","system.no-permission.description").replace("%hasPermission%", hasPermission));
        embedBuilder.setFooter(Utils.getLangManager(event).getDescriptionLocale("system.yml","system.no-permission.footer"));
        return embedBuilder;
    }
    public EmbedBuilder wrongError(String errorCode) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.RED);
        embedBuilder.setTitle(Utils.getLangManager(event).getDescriptionLocale("system.yml", "system.wrong-error.embed.title"));
        embedBuilder.setDescription(Utils.getLangManager(event).getDescriptionLocale("system.yml", "system.wrong-error.embed.description")
                .replace("%error-code%", errorCode));
        embedBuilder.setFooter(Utils.getLangManager(event).getDescriptionLocale("system.yml", "system.wrong-error.embed.footer"));
        return embedBuilder;
    }
    public EmbedBuilder disableFunction() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.RED);
        embedBuilder.setTitle(Utils.getLangManager(event).getDescriptionLocale("system.yml", "system.disable-function.embed.title"));
        embedBuilder.setDescription(Utils.getLangManager(event).getDescriptionLocale("system.yml", "system.disable-function.embed.description"));
        embedBuilder.setFooter(Utils.getLangManager(event).getDescriptionLocale("system.yml", "system.disable-function.embed.footer"));
        return embedBuilder;
    }
}
