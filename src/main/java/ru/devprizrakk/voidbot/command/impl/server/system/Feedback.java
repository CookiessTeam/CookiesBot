package ru.devprizrakk.voidbot.command.impl.server.system;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class Feedback extends BaseCommand {

    @Override
    public String getName() {
        return "feedback";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("feedback.description.command");
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SYSTEM;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of(Permission.MANAGE_SERVER);
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public DefaultMemberPermissions getDefaultPermissions() {
        return DefaultMemberPermissions.DISABLED;
    }

    @Override
    public void onExecute() throws SQLException {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(88, 101, 242));
        embed.setTitle(getLangManager(event).getInfoLocale("feedback.embed.title"));
        embed.setDescription(getLangManager(event).getInfoLocale("feedback.embed.description"));
        embed.setFooter(getLangManager(event).getInfoLocale("feedback.embed.footer"));

        Button idea = Button.primary("fb_idea",
                        getLangManager(event).getInfoLocale("feedback.button.idea"))
                .withEmoji(Emoji.fromUnicode("💡"));
        Button mod = Button.danger("fb_mod",
                        getLangManager(event).getInfoLocale("feedback.button.mod"))
                .withEmoji(Emoji.fromUnicode("⚠️"));
        Button user = Button.secondary("fb_user",
                        getLangManager(event).getInfoLocale("feedback.button.user"))
                .withEmoji(Emoji.fromUnicode("👤"));

        if (event.getGuild() == null) {
            event.reply(getLangManager(event).getInfoLocale("system.guild-only")).setEphemeral(true).queue();
            return;
        }

        String channelId = getConfig().getString("channel.feedback.message", "");
        if (channelId == null || channelId.isEmpty()) {
            event.reply(getLangManager(event).getInfoLocale(
                    "feedback.notice.no-channel")).setEphemeral(true).queue();
            return;
        }

        TextChannel channel = event.getGuild().getTextChannelById(channelId);
        if (channel == null) {
            event.reply(getLangManager(event).getInfoLocale(
                    "feedback.notice.no-channel")).setEphemeral(true).queue();
            return;
        }

        channel.sendMessageEmbeds(embed.build())
                .addComponents(ActionRow.of(idea, mod, user))
                .queue(
                        _ -> event.reply(getLangManager(event).getInfoLocale(
                                "feedback.notice.posted")).setEphemeral(true).queue(),
                        failure -> {
                            Logger.getLogger().log(LogType.ERROR, "command", "Failed to post feedback message", failure);
                            event.reply(getLangManager(event).getInfoLocale("system.generic-error")).setEphemeral(true).queue();
                        }
                );
    }
}