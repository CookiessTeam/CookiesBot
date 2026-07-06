package ru.devprizrakk.voidbot.commands.server.commands.moderation.subcommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.core.command.discord.SubCommand;
import ru.devprizrakk.voidbot.core.exceptions.discord.WrongErrorEmbedFactory;
import ru.devprizrakk.voidbot.core.language.LangMessage;
import ru.devprizrakk.voidbot.core.utils.Utils;

import java.awt.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Warn extends SubCommand {

    @Override
    public String getName() {
        return "warn";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale(
                LangMessage.Commands.Warn.FILE,
                LangMessage.Commands.Warn.Description.COMMAND
        );
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "target-user", getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Warn.FILE,
                LangMessage.Commands.Warn.Description.Option.TARGET_USER), true));
        options.add(new OptionData(OptionType.STRING, "reason", getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Warn.FILE,
                LangMessage.Commands.Warn.Description.Option.REASON), false));
        return options;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of(Permission.MODERATE_MEMBERS);
    }

    @Override
    public void onExecute() {
        Guild guild = event.getGuild();
        Member authorMember = event.getMember();
        if (guild == null || authorMember == null) {
            replyOther("GUILD_OR_MEMBER_NULL");
            return;
        }

        User author = event.getUser();
        Member targetMember = event.getOption("target-user", OptionMapping::getAsMember);
        User targetUser = event.getOption("target-user", OptionMapping::getAsUser);
        if (targetMember == null || targetUser == null) {
            replyError(LangMessage.Commands.Warn.Error.USER_NOT_FOUND);
            return;
        }

        Member selfMember = guild.getSelfMember();
        if (!authorMember.canInteract(targetMember)) {
            replyError(LangMessage.Commands.Warn.Error.LowLevelPermission.AUTHOR);
            return;
        }
        if (!selfMember.canInteract(targetMember)) {
            replyError(LangMessage.Commands.Warn.Error.LowLevelPermission.BOT);
            return;
        }

        String reason = event.getOption("reason", OptionMapping::getAsString);
        if (reason == null || reason.isBlank()) {
            reason = "-";
        }

        String finalReason = reason;
        try {
            ru.devprizrakk.voidbot.core.database.model.Warn warn =
                    new ru.devprizrakk.voidbot.core.database.model.Warn(
                            0,
                            targetUser.getIdLong(),
                            author.getIdLong(),
                            finalReason,
                            new Timestamp(new Date().getTime()),
                            null);
            Utils.getDatabaseManager().getRepositoryManager().getWarns().save(warn);
            ru.devprizrakk.voidbot.events.StatisticsService.onWarn(targetUser.getIdLong());
        } catch (Exception e) {
            ru.devprizrakk.voidbot.core.logging.Logger.getLogger()
                    .log(ru.devprizrakk.voidbot.core.logging.LogType.ERROR, "MOD",
                            "Failed to persist warn", e);
            replyOther("WARN_PERSIST_FAILED", e);
            return;
        }

        long totalWarns;
        try {
            totalWarns = Utils.getDatabaseManager().getRepositoryManager().getWarns().countAll();
        } catch (Exception e) {
            totalWarns = -1L;
        }

        replySuccess(author, targetUser, finalReason, totalWarns);
    }

    private void replySuccess(User author, User targetUser, String reason, long totalWarns) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255, 104, 0));
        embed.setTitle(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Warn.FILE,
                LangMessage.Commands.Warn.Embed.TITLE
        ));
        String desc = getLangManager(event).getDescriptionLocale(
                        LangMessage.Commands.Warn.FILE,
                        LangMessage.Commands.Warn.Embed.DESCRIPTION)
                .replace("%target-user%", targetUser.getAsMention())
                .replace("%author%", author.getAsMention())
                .replace("%reason%", reason);
        if (totalWarns >= 0) {
            desc += "\nВсего варнов на сервере: **" + totalWarns + "**";
        }
        embed.setDescription(desc);
        embed.setFooter(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Warn.FILE,
                LangMessage.Commands.Warn.Embed.FOOTER
        ));
        event.replyEmbeds(embed.build()).queue();
    }

    private void replyError(String key) {
        new WrongErrorEmbedFactory(event).wrongError(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Warn.FILE, key));
    }

    private void replyOther(String code) {
        new WrongErrorEmbedFactory(event).wrongError(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Warn.FILE,
                LangMessage.Commands.Warn.Error.OTHER).replace("%code%", code));
    }

    private void replyOther(String code, Throwable throwable) {
        String detail = throwable == null
                ? "unknown"
                : throwable.getClass().getSimpleName() +
                (throwable.getMessage() == null || throwable.getMessage().isBlank() ? "" : ": " + throwable.getMessage());
        replyOther(code + " | " + detail);
    }
}