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
import ru.devprizrakk.voidbot.language.LangMessage;
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
        return getLangManager(event).getInfoLocale(
                LangMessage.Commands.Server.ServerInfo.FILE,
                LangMessage.Commands.Server.ServerInfo.Description.COMMAND
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
            event.reply("Команда доступна только на сервере.").setEphemeral(true).queue();
            return;
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(88, 101, 242));
        embed.setThumbnail(guild.getIconUrl());
        embed.setTitle("Информация о сервере «" + guild.getName() + "»");

        embed.addField("ID", guild.getId(), true);
        embed.addField("Владелец", guild.getOwner() != null ? guild.getOwner().getAsMention() : "—", true);
        embed.addField("Создан", guild.getTimeCreated().format(fmt), true);

        embed.addField("Участников", String.valueOf(guild.getMemberCount()), true);
        embed.addField("Ролей", String.valueOf(guild.getRoles().size()), true);
        embed.addField("Boost", "Tier " + guild.getBoostTier().getKey() + " (" + guild.getBoostCount() + " бустов)", true);

        List<Category> categories = guild.getCategories();
        List<TextChannel> text = guild.getTextChannels();
        List<VoiceChannel> voice = guild.getVoiceChannels();
        embed.addField("Каналов",
                "Текстовых: **" + text.size() + "**\n" +
                        "Голосовых: **" + voice.size() + "**\n" +
                        "Категорий: **" + categories.size() + "**", true);

        embed.addField("Уровень проверки", guild.getVerificationLevel().name(), true);
        embed.addField("Локаль", guild.getLocale().toString(), true);

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

            embed.addField("Аналитика (за всё время)",
                    "Сообщений: **" + totalMessages + "**\n" +
                            "Войс всего: **" + RankCardRenderer.formatTime(totalVoiceSec) + "**\n" +
                            "Открытых войс-сессий: **" + openVoice + "**",
                    true);

            embed.addField("События",
                    "Входов всего: **" + joinsTotal + "**\n" +
                            "Банов всего: **" + bansTotal + "**\n" +
                            "Киков всего: **" + kicksTotal + "**",
                    true);

            embed.addField("Модерация всего",
                    "Варнов: **" + totalWarns + "**\n" +
                            "Банов: **" + totalBans + "**\n" +
                            "Мутов: **" + totalMutes + "**",
                    true);
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "command", "Failed to load server analytics", e);
        }

        embed.setFooter("VoidBot | " + OffsetDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        event.replyEmbeds(embed.build()).queue();
    }
}