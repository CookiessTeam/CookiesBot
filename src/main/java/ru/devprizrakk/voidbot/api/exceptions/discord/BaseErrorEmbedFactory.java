package ru.devprizrakk.voidbot.api.exceptions.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import ru.devprizrakk.voidbot.api.utils.Utils;

import java.awt.*;

public class BaseErrorEmbedFactory {
    protected final IReplyCallback event;

    public BaseErrorEmbedFactory(IReplyCallback event) {
        this.event = event;
    }

    protected EmbedBuilder createErrorEmbed(String titleKey,
                                            String descriptionKey,
                                            String footerKey) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle(Utils.getLangManager(event)
                .getDescriptionLocale("system.yml", titleKey));
        embed.setDescription(Utils.getLangManager(event)
                .getDescriptionLocale("system.yml", descriptionKey));
        embed.setFooter(Utils.getLangManager(event)
                .getDescriptionLocale("system.yml", footerKey));
        return embed;
    }
}
