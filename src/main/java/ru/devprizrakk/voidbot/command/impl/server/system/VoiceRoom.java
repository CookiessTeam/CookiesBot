package ru.devprizrakk.voidbot.command.impl.server.system;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;
import ru.devprizrakk.voidbot.events.voiceroom.VoiceRoomListener;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;

import java.sql.SQLException;
import java.util.List;

public class VoiceRoom extends BaseCommand {

    @Override
    public String getName() {
        return "voiceroom";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("voiceroom.description.command");
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
        // По умолчанию команда видна и доступна только администраторам сервера
        return DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER);
    }

    @Override
    public void onExecute() throws SQLException {
        if (!VoiceRoomListener.isEnabled()) {
            event.reply(getLangManager(event).getInfoLocale("voiceroom.error.disabled"))
                    .setEphemeral(true).queue();
            return;
        }

        if (event.getGuild() == null) {
            event.reply(getLangManager(event).getInfoLocale("system.guild-only")).setEphemeral(true).queue();
            return;
        }

        String channelId = getConfig().getString("voice-room.text-channel", "");
        if (channelId == null || channelId.isEmpty() || channelId.startsWith("fill_id_")) {
            event.reply(getLangManager(event).getInfoLocale("voiceroom.notice.no-channel"))
                    .setEphemeral(true).queue();
            return;
        }

        TextChannel channel = event.getGuild().getTextChannelById(channelId);
        if (channel == null) {
            event.reply(getLangManager(event).getInfoLocale("voiceroom.notice.no-channel"))
                    .setEphemeral(true).queue();
            return;
        }

        channel.sendMessageEmbeds(VoiceRoomListener.buildPanelEmbed().build())
                .addComponents(VoiceRoomListener.buildControlRows(getLangManager(event)))
                .queue(
                        _ -> event.reply(getLangManager(event).getInfoLocale("voiceroom.notice.posted"))
                                .setEphemeral(true).queue(),
                        failure -> {
                            Logger.getLogger().log(LogType.ERROR, "command",
                                    "Failed to post voiceroom panel", failure);
                            event.reply(getLangManager(event).getInfoLocale("voiceroom.error.generic"))
                                    .setEphemeral(true).queue();
                        }
                );
    }
}