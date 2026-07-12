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

public class Unban extends BaseSubCommand {

    @Override
    public String getName() {
        return "unban";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("unban.description.command"
        );
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "target-user", getLangManager(event).getDescriptionLocale("unban.description.option.target-user"), true));
        return options;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of(Permission.BAN_MEMBERS);
    }

    @Override
    public void onExecute() {
        Guild guild = event.getGuild();
        Member authorMember = event.getMember();
        if (guild == null || authorMember == null) {
            replyOther("GUILD_OR_MEMBER_NULL");
            return;
        }

        if (event.getOption("target-user", OptionMapping::getAsString) != null && event.getOption("target-user", OptionMapping::getAsString).isEmpty()) {
            replyError("unban.error.user-not-found");
            return;
        }

        User author = event.getUser();
        User targetUser = event.getJDA().getUserById(event.getOption("target-user", OptionMapping::getAsString));
        if (targetUser == null) {
            replyError("unban.error.user-not-found");
            return;
        }

        guild.unban(targetUser)
                .reason("Unmute user")
                .queue(
                        success -> replySuccess(author, targetUser),
                        failure -> replyOther(failure)
                );
    }

    private void replySuccess(User author, User targetUser) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(255, 104, 0));
        embed.setTitle(getLangManager(event).getDescriptionLocale("unban.embed.title"
        ));
        embed.setDescription(getLangManager(event).getDescriptionLocale("unban.embed.description"
                        )
                        .replace("%author%", author.getAsMention())
                        .replace("%target-user%", targetUser.getAsMention())
        );
        embed.setFooter(getLangManager(event).getDescriptionLocale("unban.embed.footer"
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

    private void replyOther(Throwable throwable) {
        String detail = throwable == null
                ? "unknown"
                : throwable.getClass().getSimpleName() +
                (throwable.getMessage() == null || throwable.getMessage().isBlank() ? "" : ": " + throwable.getMessage());
        replyOther("UNBAN_FAILED" + " | " + detail);
    }
}
