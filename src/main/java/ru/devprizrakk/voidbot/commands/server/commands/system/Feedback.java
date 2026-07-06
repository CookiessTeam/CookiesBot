package ru.devprizrakk.voidbot.commands.server.commands.system;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import ru.devprizrakk.voidbot.core.command.discord.BaseCommand;
import ru.devprizrakk.voidbot.core.command.discord.CommandCategory;
import ru.devprizrakk.voidbot.core.language.LangMessage;
import ru.devprizrakk.voidbot.core.logging.LogType;
import ru.devprizrakk.voidbot.core.logging.Logger;

import java.awt.Color;
import java.sql.SQLException;
import java.util.List;

public class Feedback extends BaseCommand {

    @Override
    public String getName() {
        return "feedback";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale(
                LangMessage.Commands.System.Feedback.FILE,
                LangMessage.Commands.System.Feedback.Description.COMMAND
        );
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
        embed.setTitle(getLangManager(event).getInfoLocale(
                LangMessage.Commands.System.Feedback.FILE,
                LangMessage.Commands.System.Feedback.Embed.TITLE));
        embed.setDescription(getLangManager(event).getInfoLocale(
                LangMessage.Commands.System.Feedback.FILE,
                LangMessage.Commands.System.Feedback.Embed.DESCRIPTION));
        embed.setFooter(getLangManager(event).getInfoLocale(
                LangMessage.Commands.System.Feedback.FILE,
                LangMessage.Commands.System.Feedback.Embed.FOOTER));

        Button idea = Button.primary("fb_idea",
                        getLangManager(event).getInfoLocale(LangMessage.Commands.System.Feedback.FILE,
                                LangMessage.Commands.System.Feedback.Button.IDEA))
                .withEmoji(Emoji.fromUnicode("💡"));
        Button mod = Button.danger("fb_mod",
                        getLangManager(event).getInfoLocale(LangMessage.Commands.System.Feedback.FILE,
                                LangMessage.Commands.System.Feedback.Button.MOD))
                .withEmoji(Emoji.fromUnicode("⚠️"));
        Button user = Button.secondary("fb_user",
                        getLangManager(event).getInfoLocale(LangMessage.Commands.System.Feedback.FILE,
                                LangMessage.Commands.System.Feedback.Button.USER))
                .withEmoji(Emoji.fromUnicode("👤"));

        if (event.getGuild() == null) {
            event.reply("Команда доступна только на сервере.").setEphemeral(true).queue();
            return;
        }

        String channelId = getConfigManager().getConfig().getString("channel.feedback.message", "");
        if (channelId == null || channelId.isEmpty()) {
            event.reply(getLangManager(event).getInfoLocale(
                    LangMessage.Commands.System.Feedback.FILE,
                    "feedback.notice.no-channel")).setEphemeral(true).queue();
            return;
        }
        TextChannel channel = event.getGuild().getTextChannelById(channelId);
        if (channel == null) {
            event.reply(getLangManager(event).getInfoLocale(
                    LangMessage.Commands.System.Feedback.FILE,
                    "feedback.notice.no-channel")).setEphemeral(true).queue();
            return;
        }

        channel.sendMessageEmbeds(embed.build())
                .addComponents(ActionRow.of(idea, mod, user))
                .queue(
                        success -> event.reply(getLangManager(event).getInfoLocale(
                                LangMessage.Commands.System.Feedback.FILE,
                                "feedback.notice.posted")).setEphemeral(true).queue(),
                        failure -> {
                            Logger.getLogger().log(LogType.ERROR, "command", "Failed to post feedback message", failure);
                            event.reply("Не удалось отправить сообщение обратной связи.").setEphemeral(true).queue();
                        }
                );
    }
}