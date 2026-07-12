package ru.devprizrakk.voidbot.command.impl.server.user;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;
import ru.devprizrakk.voidbot.database.model.ExperienceModel;
import ru.devprizrakk.voidbot.events.LevelService;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class Rank extends BaseCommand {

    @Override
    public String getName() {
        return "rank";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("rank.description.command"
        );
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER,
                        "user",
                        getLangManager(event).getInfoLocale("rank.description.option.user"),
                        false));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SERVER;
    }

    @Override
    public void onExecute() throws SQLException {
        User user = event.getOption("user") != null
                ? Objects.requireNonNull(event.getOption("user")).getAsUser()
                : event.getUser();

        Guild guild = event.getGuild();
        if (guild == null) {
            event.reply(getLangManager(event).getInfoLocale("system.guild-only")).setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        try {
            ExperienceModel exp = LevelService.get(user, guild).orElseGet(() -> new ExperienceModel(0, user.getIdLong(), guild.getIdLong(), 0, 0, 0, null));
            long required = LevelService.requiredXpForLevel(exp.getLevel());
            long voiceSeconds = Utils.getDatabaseManager()
                    .getRepositoryManager().getVoiceSessions()
                    .sumDurationSecondsByUser(user.getIdLong());

            String avatarUrl = user.getEffectiveAvatarUrl();
            avatarUrl = avatarUrl.replace(".gif", ".png").replace(".webp", ".png");

            boolean online = false;
            Member member = guild.getMemberById(user.getIdLong());
            if (member != null) {
                online = !member.getOnlineStatus().name().equalsIgnoreCase("offline");
            }

            byte[] image = RankCardRenderer.render(
                    user.getEffectiveName(),
                    avatarUrl,
                    online,
                    exp.getLevel(),
                    exp.getExperience(),
                    required,
                    exp.getTotalExperience(),
                    voiceSeconds
            );

            String filename = "rank-" + user.getIdLong() + ".png";
            try (FileUpload upload = FileUpload.fromData(image, filename)) {
                event.getHook().setEphemeral(false).sendMessage(" ")
                        .addFiles(upload)
                        .queue();
            }
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "command", "Failed to render rank card", e);
            try {
                event.getHook().sendMessage(getLangManager(event).getInfoLocale("system.generic-error")).queue();
            } catch ( Exception ignored ) {
            }
        }
    }

    @SuppressWarnings("unused")
    private Color brandColor() {
        return new Color(110, 220, 90);
    }
}