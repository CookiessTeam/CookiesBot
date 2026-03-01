package ru.devprizrakk.voidbot.api.exceptions.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class DisabledFunctionErrorEmbedFactory extends BaseErrorEmbedFactory {

    public DisabledFunctionErrorEmbedFactory(IReplyCallback event) {
        super(event);
    }

    public EmbedBuilder disabledFunction() {
        return createErrorEmbed(
                "system.disable-function.embed.title",
                "system.disable-function.embed.description",
                "system.disable-function.embed.footer"
        );
    }
}