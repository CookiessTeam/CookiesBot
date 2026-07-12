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
import ru.devprizrakk.voidbot.exceptions.discord.WrongErrorEmbedFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Unmute extends BaseSubCommand {

    @Override
    public String getName() {
        return "unmute";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("unmute.description.command"
        );
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "target-user", getLangManager(event).getDescriptionLocale("unmute.description.option.target-user"), true));
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
            replyError("unmute.error.user-not-found");
            return;
        }

        Member selfMember = guild.getSelfMember();
        if (selfMember.isTimedOut()) {
            replyError("unmute.error.no-muted");
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
        embed.setTitle(getLangManager(event).getDescriptionLocale("unmute.embed.title"
        ));
        embed.setDescription(getLangManager(event).getDescriptionLocale("unmute.embed.description"
                )
                .replace("%author%", author.getAsMention())
                .replace("%target-user%", targetUser.getAsMention())
                .replace("%reason%", reason));
        embed.setFooter(getLangManager(event).getDescriptionLocale("unmute.embed.footer"
        ));
        event.replyEmbeds(embed.build()).queue();
    }

    private void replyError(String key) {
        new WrongErrorEmbedFactory(event).
                wrongError(getLangManager(event).
                        getDescriptionLocale(key
                        ));
    }

    private void replyOther(String code) {
        new WrongErrorEmbedFactory(event).wrongError(getLangManager(event).getDescriptionLocale("mute.error.other").replace("%code%", code));
    }

    private void replyOther(String code, Throwable throwable) {
        String detail = throwable == null
                ? "unknown"
                : throwable.getClass().getSimpleName() +
                (throwable.getMessage() == null || throwable.getMessage().isBlank() ? "" : ": " + throwable.getMessage());
        replyOther(code + " | " + detail);
    }
}
