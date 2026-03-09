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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Unmute extends SubCommand {
    @Override
    public String getName() {
        return "unmute";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale(
                LangMessage.Commands.Moderation.Unmute.FILE,
                LangMessage.Commands.Moderation.Unmute.Description.COMMAND
        );
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "target-user", getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Moderation.Unmute.FILE,
                LangMessage.Commands.Moderation.Unmute.Description.Option.TARGET_USER), true));
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
            replyError(LangMessage.Commands.Moderation.Unmute.Error.USER_NOT_FOUND);
            return;
        }

        Member selfMember = guild.getSelfMember();
        if (selfMember.isTimedOut()) {
            replyError(LangMessage.Commands.Moderation.Unmute.Error.NO_MUTED);
            return;
        }

        String reason = event.getOption("reason", OptionMapping::getAsString);
        if (reason == null || reason.isBlank()) {
            reason = "-";
        }

        String finalReason = reason;
        targetMember.removeTimeout()
                .reason(reason)
                .queue(
                        success -> replySuccess(author, targetUser, finalReason),
                        failure -> replyOther("UNMUTE_FAILED", failure)
                );
    }

    private void replySuccess(User author, User targetUser, String reason) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255, 104, 0));
        embed.setTitle(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Moderation.Unmute.FILE,
                LangMessage.Commands.Moderation.Unmute.Embed.TITLE
        ));
        embed.setDescription(getLangManager(event).getDescriptionLocale(
                        LangMessage.Commands.Moderation.Unmute.FILE,
                        LangMessage.Commands.Moderation.Unmute.Embed.DESCRIPTION
                )
                .replace("%author%", author.getAsMention())
                .replace("%target-user%", targetUser.getAsMention())
                .replace("%reason%", reason));
        embed.setFooter(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Moderation.Unmute.FILE,
                LangMessage.Commands.Moderation.Unmute.Embed.FOOTER
        ));
        event.replyEmbeds(embed.build()).queue();
    }

    private void replyError(String key) {
        new WrongErrorEmbedFactory(event).
                wrongError(getLangManager(event).
                        getDescriptionLocale(
                                LangMessage.Commands.Moderation.Mute.FILE,
                                key
                        ));
    }

    private void replyOther(String code) {
        new WrongErrorEmbedFactory(event).wrongError(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Moderation.Mute.FILE,
                LangMessage.Commands.Moderation.Mute.Error.OTHER).replace("%code%", code));
    }

    private void replyOther(String code, Throwable throwable) {
        String detail = throwable == null
                ? "unknown"
                : throwable.getClass().getSimpleName() +
                (throwable.getMessage() == null || throwable.getMessage().isBlank() ? "" : ": " + throwable.getMessage());
        replyOther(code + " | " + detail);
    }
}
