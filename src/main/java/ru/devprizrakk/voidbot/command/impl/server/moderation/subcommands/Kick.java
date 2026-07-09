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
import ru.devprizrakk.voidbot.language.LangMessage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Kick extends BaseSubCommand {

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale(
                LangMessage.Commands.Moderation.Kick.FILE,
                LangMessage.Commands.Moderation.Kick.Description.COMMAND
        );
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "target-user", getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Moderation.Kick.FILE,
                LangMessage.Commands.Moderation.Kick.Description.Option.TARGET_USER), true));
        options.add(new OptionData(OptionType.STRING, "reason", getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Moderation.Kick.FILE,
                LangMessage.Commands.Moderation.Kick.Description.Option.REASON), true));
        return options;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of(Permission.KICK_MEMBERS);
    }

    @Override
    public void onExecute() {
        Guild guild = event.getGuild();
        Member authorMember = event.getMember();
        if (guild == null || authorMember == null) {
            replyError(LangMessage.Commands.Moderation.Kick.Error.OTHER);
            return;
        }

        User author = event.getUser();
        Member targetMember = event.getOption("target-user", OptionMapping::getAsMember);
        User targetUser = event.getOption("target-user", OptionMapping::getAsUser);
        if (targetMember == null || targetUser == null) {
            replyError(LangMessage.Commands.Moderation.Kick.Error.USER_NOT_FOUND);
            return;
        }

        Member selfMember = guild.getSelfMember();
        if (!authorMember.canInteract(targetMember)) {
            replyError(LangMessage.Commands.Moderation.Kick.Error.LowLevelPermission.AUTHOR);
            return;
        }
        if (!selfMember.canInteract(targetMember)) {
            replyError(LangMessage.Commands.Moderation.Kick.Error.LowLevelPermission.BOT);
            return;
        }

        String reason = event.getOption("reason", OptionMapping::getAsString);
        if (reason == null || reason.isBlank()) {
            reason = "-";
        }

        String finalReason = reason;
        guild.kick(targetUser)
                .reason(reason)
                .queue(
                        success -> replySuccess(author, targetUser, finalReason),
                        failure -> replyError(LangMessage.Commands.Moderation.Kick.Error.OTHER)
                );
    }

    private void replySuccess(User author, User targetUser, String reason) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255, 104, 0));
        embed.setTitle(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Moderation.Kick.FILE,
                LangMessage.Commands.Moderation.Kick.Embed.TITLE
        ));
        embed.setDescription(getLangManager(event).getDescriptionLocale(
                        LangMessage.Commands.Moderation.Kick.FILE,
                        LangMessage.Commands.Moderation.Kick.Embed.DESCRIPTION
                )
                .replace("%author%", author.getAsMention())
                .replace("%target-user%", targetUser.getAsMention())
                .replace("%reason%", reason));
        embed.setFooter(getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Moderation.Kick.FILE,
                LangMessage.Commands.Moderation.Kick.Embed.FOOTER
        ));
        event.replyEmbeds(embed.build()).queue();
    }

    private void replyError(String key) {
        new WrongErrorEmbedFactory(event).wrongError(key);
    }
}
