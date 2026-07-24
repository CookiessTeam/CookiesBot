package ru.devprizrakk.voidbot.exceptions.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public class NoPermissionErrorEmbedFactory extends BaseErrorEmbedFactory {

    public NoPermissionErrorEmbedFactory(IReplyCallback event) {
        super(event);
    }

    public EmbedBuilder noPermission(String hasPermission) {
        EmbedBuilder embed = createErrorEmbed(
                "system.no-permission.title",
                "system.no-permission.description",
                "system.no-permission.footer"
        );

        String description = embed.getDescriptionBuilder().toString();
        description = description.replace("%hasPermission%", hasPermission);
        embed.setDescription(description);

        return embed;
    }
}