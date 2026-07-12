package ru.devprizrakk.voidbot.exceptions.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

import java.awt.*;

public class BaseErrorEmbedFactory {
    protected final IReplyCallback event;

    public BaseErrorEmbedFactory(IReplyCallback event) {
        this.event = event;
    }

    protected EmbedBuilder createErrorEmbed(String titleKey, String descriptionKey, String footerKey) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle(safeGet(titleKey, "Ошибка"));
        embed.setDescription(safeGet(descriptionKey, "Произошла ошибка при выполнении команды."));
        embed.setFooter(safeGet(footerKey, "VoidBot"));
        return embed;
    }

    private String safeGet(String key, String fallback) {
        String value = ru.devprizrakk.voidbot.language.LangManager.get("ru", key);
        if (value == null || value.startsWith("§c")) {
            return fallback;
        }
        return value;
    }
}
