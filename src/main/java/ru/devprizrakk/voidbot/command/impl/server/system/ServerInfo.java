package ru.devprizrakk.voidbot.command.impl.server.system;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;
import ru.devprizrakk.voidbot.command.impl.server.user.RankCardRenderer;
import ru.devprizrakk.voidbot.database.model.MemberEventType;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ServerInfo extends BaseCommand {

    @Override
    public String getName() {
        return "serverinfo";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("serverinfo.description.command"
        );
    }

    @Override
    public List<net.dv8tion.jda.api.interactions.commands.build.OptionData> getOptions() {
        return List.of();
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SERVER;
    }

    @Override
    public void onExecute() {
        Guild guild = event.getGuild();
        if (guild == null) {
            event.reply(getLangManager(event).getInfoLocale("system.guild-only")).setEphemeral(true).queue();
            return;
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(88, 101, 242));
        embed.setThumbnail(guild.getIconUrl());
        embed.setTitle(getLangManager(event).getInfoLocale("serverinfo.embed.title").replace("%server-name%", guild.getName()));

        embed.addField(getLangManager(event).getInfoLocale("serverinfo.field.id"), guild.getId(), true);
        embed.addField(getLangManager(event).getInfoLocale("serverinfo.field.owner"), guild.getOwner() != null ? guild.getOwner().getAsMention() : "—", true);
        embed.addField(getLangManager(event).getInfoLocale("serverinfo.field.created"), guild.getTimeCreated().format(fmt), true);

        embed.addField(getLangManager(event).getInfoLocale("serverinfo.field.members"), String.valueOf(guild.getMemberCount()), true);
        embed.addField(getLangManager(event).getInfoLocale("serverinfo.field.roles"), String.valueOf(guild.getRoles().size()), true);
        embed.addField("Boost", getLangManager(event).getInfoLocale("serverinfo.field.boost").replace("%tier%", String.valueOf(guild.getBoostTier().getKey())).replace("%count%", String.valueOf(guild.getBoostCount())), true);

        List<Category> categories = guild.getCategories();
        List<TextChannel> text = guild.getTextChannels();
        List<VoiceChannel> voice = guild.getVoiceChannels();
        embed.addField(getLangManager(event).getInfoLocale("serverinfo.field.channels"),
                getLangManager(event).getInfoLocale("serverinfo.field.channels-text").replace("%count%", String.valueOf(text.size())) +
                        getLangManager(event).getInfoLocale("serverinfo.field.channels-voice").replace("%count%", String.valueOf(voice.size())) +
                        getLangManager(event).getInfoLocale("serverinfo.field.channels-category").replace("%count%", String.valueOf(categories.size())), true);

        embed.addField(getLangManager(event).getInfoLocale("serverinfo.field.verification"), guild.getVerificationLevel().name(), true);
        embed.addField(getLangManager(event).getInfoLocale("serverinfo.field.locale"), guild.getLocale().toString(), true);

        try {
            var rm = Utils.getDatabaseManager().getRepositoryManager();

            long totalMessages = rm.getMessages().countAll();
            long totalVoiceSec = rm.getVoiceSessions().sumAllDurationSeconds();
            long totalBans = rm.getBans().countAll();
            long totalMutes = rm.getMutes().countAll();
            long totalWarns = rm.getWarns().countAll();
            long openVoice = rm.getVoiceSessions().findAllOpen().size();

            long joinsTotal = rm.getMemberEvents().countByType(MemberEventType.JOIN);
            long bansTotal = rm.getMemberEvents().countByType(MemberEventType.BAN);
            long kicksTotal = rm.getMemberEvents().countByType(MemberEventType.KICK);

            embed.addField(getLangManager(event).getInfoLocale("serverinfo.field.analytics-title"),
                    getLangManager(event).getInfoLocale("serverinfo.field.analytics-messages").replace("%count%", String.valueOf(totalMessages)) +
                            getLangManager(event).getInfoLocale("serverinfo.field.analytics-voice").replace("%count%", RankCardRenderer.formatTime(totalVoiceSec)) +
                            getLangManager(event).getInfoLocale("serverinfo.field.analytics-open-voice").replace("%count%", String.valueOf(openVoice)),
                    true);

            embed.addField(getLangManager(event).getInfoLocale("serverinfo.field.events-title"),
                    getLangManager(event).getInfoLocale("serverinfo.field.events-joins").replace("%count%", String.valueOf(joinsTotal)) +
                            getLangManager(event).getInfoLocale("serverinfo.field.events-bans").replace("%count%", String.valueOf(bansTotal)) +
                            getLangManager(event).getInfoLocale("serverinfo.field.events-kicks").replace("%count%", String.valueOf(kicksTotal)),
                    true);

            embed.addField(getLangManager(event).getInfoLocale("serverinfo.field.moderation-title"),
                    getLangManager(event).getInfoLocale("serverinfo.field.moderation-warns").replace("%count%", String.valueOf(totalWarns)) +
                            getLangManager(event).getInfoLocale("serverinfo.field.moderation-bans").replace("%count%", String.valueOf(totalBans)) +
                            getLangManager(event).getInfoLocale("serverinfo.field.moderation-mutes").replace("%count%", String.valueOf(totalMutes)),
                    true);
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "command", "Failed to load server analytics", e);
        }

        embed.setFooter(OffsetDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        event.replyEmbeds(embed.build()).queue();
    }
}