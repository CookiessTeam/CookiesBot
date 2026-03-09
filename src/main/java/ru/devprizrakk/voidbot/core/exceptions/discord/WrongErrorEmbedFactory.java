package ru.devprizrakk.voidbot.core.exceptions.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class WrongErrorEmbedFactory extends BaseErrorEmbedFactory{

    public WrongErrorEmbedFactory(IReplyCallback event) {
        super(event);
    }

    public boolean wrongError(String errorCode) {
        EmbedBuilder embed = createErrorEmbed(
                "system.wrong-error.embed.title",
                "system.wrong-error.embed.description",
                "system.wrong-error.embed.footer"
        );

        String description = embed.getDescriptionBuilder().toString();
        description = description.replace("%error-code%", errorCode);
        embed.setDescription(description);

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        return true;
    }
    public void wrongErrorHook(String errorCode) {
        EmbedBuilder embed = createErrorEmbed(
                "system.wrong-error.embed.title",
                "system.wrong-error.embed.description",
                "system.wrong-error.embed.footer"
        );

        String description = embed.getDescriptionBuilder().toString();
        description = description.replace("%error-code%", errorCode);
        embed.setDescription(description);

        event.getHook().sendMessageEmbeds(embed.build()).setEphemeral(true).queue();
    }
}
