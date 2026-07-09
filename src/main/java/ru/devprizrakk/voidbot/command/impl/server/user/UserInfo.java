package ru.devprizrakk.voidbot.command.impl.server.user;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;
import ru.devprizrakk.voidbot.database.model.ExperienceModel;
import ru.devprizrakk.voidbot.events.LevelService;
import ru.devprizrakk.voidbot.language.LangMessage;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class UserInfo extends BaseCommand {

    @Override
    public String getName() {
        return "userinfo";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale(
                LangMessage.Commands.Server.UserInfo.FILE,
                LangMessage.Commands.Server.UserInfo.Description.COMMAND
        );
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER,
                        "user",
                        getLangManager(event).getInfoLocale(
                                LangMessage.Commands.Server.UserInfo.FILE,
                                LangMessage.Commands.Server.UserInfo.Description.Option.USER),
                        false));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SERVER;
    }

    @Override
    public void onExecute() {
        User user = event.getOption("user") != null
                ? Objects.requireNonNull(event.getOption("user")).getAsUser()
                : event.getUser();

        Guild guild = event.getGuild();
        if (guild == null) {
            event.reply("Команда доступна только на сервере.").setEphemeral(true).queue();
            return;
        }

        Member member = guild.getMember(user);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(88, 101, 242));
        embed.setThumbnail(user.getEffectiveAvatarUrl());
        embed.setTitle("Информация о пользователе");

        embed.addField("Имя", user.getEffectiveName(), true);
        embed.addField("Username", "@" + user.getName(), true);
        embed.addField("ID", user.getId(), true);

        embed.addField("Создан",
                user.getTimeCreated().format(fmt), true);

        if (member != null) {
            embed.addField("На сервере с",
                    member.getTimeJoined().format(fmt), true);
            String roles = member.getRoles().stream()
                    .map(IMentionable::getAsMention)
                    .limit(15)
                    .collect(java.util.stream.Collectors.joining(", "));
            if (roles.isEmpty()) roles = "Нет ролей";
            embed.addField("Роли (" + member.getRoles().size() + ")", roles, false);
            String status = member.getOnlineStatus().name();
            embed.addField("Статус", status, true);
        }

        try {
            ExperienceModel exp = Utils.getDatabaseManager().getRepositoryManager()
                    .getExperience()
                    .findByDiscordIdAndGuild(user.getIdLong(), guild.getIdLong())
                    .orElse(null);

            if (exp != null) {
                embed.addField("Уровень", String.valueOf(exp.getLevel()), true);
                embed.addField("Опыт", exp.getExperience() + "/" + LevelService.requiredXpForLevel(exp.getLevel()), true);
                embed.addField("Всего XP", String.valueOf(exp.getTotalExperience()), true);
            } else {
                embed.addField("Уровень", "0 (нет данных)", true);
                embed.addField("Опыт", "0/0", true);
                embed.addField("Всего XP", "0", true);
            }
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "command", "Failed to load experience", e);
        }

        try {
            long voiceSeconds = Utils.getDatabaseManager().getRepositoryManager()
                    .getVoiceSessions().sumDurationSecondsByUser(user.getIdLong());
            embed.addField("Войс время", RankCardRenderer.formatTime(voiceSeconds), true);

            long warns = Utils.getDatabaseManager().getRepositoryManager()
                    .getWarns().countAll();
            embed.addField("Варнов", String.valueOf(warns), true);

            long bans = Utils.getDatabaseManager().getRepositoryManager()
                    .getBans().countActive();
            embed.addField("Актив. банов", String.valueOf(bans), true);

            long mutes = Utils.getDatabaseManager().getRepositoryManager()
                    .getMutes().countActive();
            embed.addField("Актив. мутов", String.valueOf(mutes), true);
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "command", "Failed to load user stats from DB", e);
        }

        embed.setFooter("VoidBot | " + java.time.OffsetDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        event.replyEmbeds(embed.build()).queue();
    }
}