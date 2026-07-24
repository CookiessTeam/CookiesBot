package ru.devprizrakk.voidbot.command.impl.server.moderation.subcommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.command.api.BaseSubCommand;
import ru.devprizrakk.voidbot.database.model.WarnModel;
import ru.devprizrakk.voidbot.events.StatisticsService;
import ru.devprizrakk.voidbot.exceptions.discord.WrongErrorEmbedFactory;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

import java.awt.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Warn extends BaseSubCommand {

    @Override
    public String getName() {
        return "warn";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("warn.description.command"
        );
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "target-user", getLangManager(event).getDescriptionLocale("warn.description.option.target-user"), true));
        options.add(new OptionData(OptionType.STRING, "reason", getLangManager(event).getDescriptionLocale("warn.description.option.reason"), false));
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
            replyError("warn.error.user-not-found");
            return;
        }

        Member selfMember = guild.getSelfMember();
        if (!authorMember.canInteract(targetMember)) {
            replyError("warn.error.low-level-permission.author");
            return;
        }
        if (!selfMember.canInteract(targetMember)) {
            replyError("warn.error.low-level-permission.bot");
            return;
        }

        String reason = event.getOption("reason", OptionMapping::getAsString);
        if (reason == null || reason.isBlank()) {
            reason = "-";
        }

        try {
            WarnModel warn = new WarnModel(0, targetUser.getIdLong(), author.getIdLong(), reason, new Timestamp(new Date().getTime()), null);
            Utils.getDatabaseManager().getRepositoryManager().getWarns().save(warn);
            StatisticsService.onWarn();
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "MOD", "Failed to persist warn", e);
            replyOther("WARN_PERSIST_FAILED", e);
            return;
        }

        long totalWarns;
        try {
            totalWarns = Utils.getDatabaseManager().getRepositoryManager().getWarns().countAll();
        } catch ( Exception e ) {
            totalWarns = -1L;
        }

        replySuccess(author, targetUser, reason, totalWarns);
    }

    private void replySuccess(User author, User targetUser, String reason, long totalWarns) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255, 104, 0));
        embed.setTitle(getLangManager(event).getDescriptionLocale("warn.embed.title"
        ));
        String desc = getLangManager(event).getDescriptionLocale("warn.embed.description")
                .replace("%target-user%", targetUser.getAsMention())
                .replace("%author%", author.getAsMention())
                .replace("%reason%", reason);
        if (totalWarns >= 0) {
            desc += getLangManager(event).getInfoLocale("warn.embed.total-warns").replace("%count%", String.valueOf(totalWarns));
        }
        embed.setDescription(desc);
        embed.setFooter(getLangManager(event).getDescriptionLocale("warn.embed.footer"
        ));
        event.replyEmbeds(embed.build()).queue();
    }

    private void replyError(String key) {
        new WrongErrorEmbedFactory(event).wrongError(getLangManager(event).getDescriptionLocale(key));
    }

    private void replyOther(String code) {
        new WrongErrorEmbedFactory(event).wrongError(getLangManager(event).getDescriptionLocale("warn.error.other").replace("%code%", code));
    }

    private void replyOther(String code, Throwable throwable) {
        String detail = throwable == null
                ? "unknown"
                : throwable.getClass().getSimpleName() +
                (throwable.getMessage() == null || throwable.getMessage().isBlank() ? "" : ": " + throwable.getMessage());
        replyOther(code + " | " + detail);
    }
}