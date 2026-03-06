package ru.devprizrakk.voidbot.core.exceptions.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class WrongErrorEmbedFactory extends BaseErrorEmbedFactory{

    public WrongErrorEmbedFactory(IReplyCallback event) {
        super(event);
    }

    public EmbedBuilder wrongError(String errorCode) {
        EmbedBuilder embed = createErrorEmbed(
                "system.wrong-error.embed.title",
                "system.wrong-error.embed.description",
                "system.wrong-error.embed.footer"
        );

        String description = embed.getDescriptionBuilder().toString();
        description = description.replace("%error-code%", errorCode);
        embed.setDescription(description);

        return embed;
    }
}
