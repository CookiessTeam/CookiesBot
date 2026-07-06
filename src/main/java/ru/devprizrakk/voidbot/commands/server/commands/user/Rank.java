package ru.devprizrakk.voidbot.commands.server.commands.user;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import ru.devprizrakk.voidbot.core.command.discord.BaseCommand;
import ru.devprizrakk.voidbot.core.command.discord.CommandCategory;
import ru.devprizrakk.voidbot.core.database.model.Experience;
import ru.devprizrakk.voidbot.core.language.LangMessage;
import ru.devprizrakk.voidbot.core.logging.LogType;
import ru.devprizrakk.voidbot.core.logging.Logger;
import ru.devprizrakk.voidbot.events.LevelService;

import java.awt.Color;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Rank extends BaseCommand {

    @Override
    public String getName() {
        return "rank";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale(
                LangMessage.Commands.Server.Rank.FILE,
                LangMessage.Commands.Server.Rank.Description.COMMAND
        );
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER,
                        "user",
                        getLangManager(event).getInfoLocale(
                                LangMessage.Commands.Server.Rank.FILE,
                                LangMessage.Commands.Server.Rank.Description.Option.USER),
                        false));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SERVER;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of();
    }

    @Override
    public void onExecute() throws SQLException {
        User user = event.getOption("user") != null
                ? Objects.requireNonNull(event.getOption("user")).getAsUser()
                : event.getUser();

        Guild guild = event.getGuild();
        if (guild == null) {
            event.reply("Команда доступна только на сервере.").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        try {
            Experience exp = LevelService.get(user, guild).orElseGet(() ->
                    new Experience(0, user.getIdLong(), guild.getIdLong(), 0, 0, 0, null));
            long required = LevelService.requiredXpForLevel(exp.getLevel());
            long voiceSeconds = ru.devprizrakk.voidbot.core.utils.Utils.getDatabaseManager()
                    .getRepositoryManager().getVoiceSessions()
                    .sumDurationSecondsByUser(user.getIdLong());

            String avatarUrl = user.getEffectiveAvatarUrl();
            avatarUrl = avatarUrl.replace(".gif", ".png").replace(".webp", ".png");

            boolean online = false;
            Member member = guild.getMemberById(user.getIdLong());
            if (member != null && member.getOnlineStatus() != null) {
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
        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "command", "Failed to render rank card", e);
            try {
                event.getHook().sendMessage("Не удалось построить карточку рейтинга.").queue();
            } catch (Exception ignored) {
            }
        }
    }

    @SuppressWarnings("unused")
    private Color brandColor() {
        return new Color(110, 220, 90);
    }
}