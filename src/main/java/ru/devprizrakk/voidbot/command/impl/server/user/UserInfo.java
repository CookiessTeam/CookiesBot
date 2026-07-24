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
        return getLangManager(event).getInfoLocale("userinfo.description.command"
        );
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER,
                        "user",
                        getLangManager(event).getInfoLocale("userinfo.description.option.user"),
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
            event.reply(getLangManager(event).getInfoLocale("system.guild-only")).setEphemeral(true).queue();
            return;
        }

        Member member = guild.getMember(user);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(88, 101, 242));
        embed.setThumbnail(user.getEffectiveAvatarUrl());
        embed.setTitle(getLangManager(event).getInfoLocale("userinfo.embed.title"));

        embed.addField(getLangManager(event).getInfoLocale("userinfo.field.name"), user.getEffectiveName(), true);
        embed.addField(getLangManager(event).getInfoLocale("userinfo.field.username"), "@" + user.getName(), true);
        embed.addField(getLangManager(event).getInfoLocale("userinfo.field.id"), user.getId(), true);

        embed.addField(getLangManager(event).getInfoLocale("userinfo.field.created"),
                user.getTimeCreated().format(fmt), true);

        if (member != null) {
            embed.addField(getLangManager(event).getInfoLocale("userinfo.field.joined"),
                    member.getTimeJoined().format(fmt), true);
            String roles = member.getRoles().stream()
                    .map(IMentionable::getAsMention)
                    .limit(15)
                    .collect(java.util.stream.Collectors.joining(", "));
            if (roles.isEmpty()) roles = getLangManager(event).getInfoLocale("userinfo.field.no-roles");
            embed.addField(getLangManager(event).getInfoLocale("userinfo.field.roles").replace("%count%", String.valueOf(member.getRoles().size())), roles, false);
            String status = member.getOnlineStatus().name();
            embed.addField(getLangManager(event).getInfoLocale("userinfo.field.status"), status, true);
        }

        try {
            ExperienceModel exp = Utils.getDatabaseManager().getRepositoryManager()
                    .getExperience()
                    .findByDiscordIdAndGuild(user.getIdLong(), guild.getIdLong())
                    .orElse(null);

            if (exp != null) {
                embed.addField(getLangManager(event).getInfoLocale("userinfo.field.level"), String.valueOf(exp.getLevel()), true);
                embed.addField(getLangManager(event).getInfoLocale("userinfo.field.experience"), exp.getExperience() + "/" + LevelService.requiredXpForLevel(exp.getLevel()), true);
                embed.addField(getLangManager(event).getInfoLocale("userinfo.field.total-xp"), String.valueOf(exp.getTotalExperience()), true);
            } else {
                embed.addField(getLangManager(event).getInfoLocale("userinfo.field.level"), getLangManager(event).getInfoLocale("userinfo.field.level-no-data"), true);
                embed.addField(getLangManager(event).getInfoLocale("userinfo.field.experience"), getLangManager(event).getInfoLocale("userinfo.field.experience-no-data"), true);
                embed.addField(getLangManager(event).getInfoLocale("userinfo.field.total-xp"), getLangManager(event).getInfoLocale("userinfo.field.total-xp-no-data"), true);
            }
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "command", "Failed to load experience", e);
        }

        try {
            long voiceSeconds = Utils.getDatabaseManager().getRepositoryManager()
                    .getVoiceSessions().sumDurationSecondsByUser(user.getIdLong());
            embed.addField(getLangManager(event).getInfoLocale("userinfo.field.voice-time"), RankCardRenderer.formatTime(voiceSeconds), true);

            long warns = Utils.getDatabaseManager().getRepositoryManager().getWarns().countAll();
            embed.addField(getLangManager(event).getInfoLocale("userinfo.field.warns"), String.valueOf(warns), true);

            long bans = Utils.getDatabaseManager().getRepositoryManager().getBans().countActive();
            embed.addField(getLangManager(event).getInfoLocale("userinfo.field.active-bans"), String.valueOf(bans), true);

            long mutes = Utils.getDatabaseManager().getRepositoryManager().getMutes().countActive();
            embed.addField(getLangManager(event).getInfoLocale("userinfo.field.active-mutes"), String.valueOf(mutes), true);
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "command", "Failed to load user stats from DB", e);
        }

        embed.setFooter(java.time.OffsetDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        event.replyEmbeds(embed.build()).queue();
    }
}