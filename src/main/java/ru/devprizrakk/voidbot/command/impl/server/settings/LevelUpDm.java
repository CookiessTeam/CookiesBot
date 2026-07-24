package ru.devprizrakk.voidbot.command.impl.server.settings;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.command.api.BaseSubCommand;
import ru.devprizrakk.voidbot.database.model.UserModel;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;
import ru.devprizrakk.voidbot.utils.Utils;

import java.awt.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class LevelUpDm extends BaseSubCommand {

    @Override
    public String getName() {
        return "levelup-dm";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("settings.levelup-dm.description.command");
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.BOOLEAN,
                        "enabled",
                        getLangManager(event).getInfoLocale("settings.levelup-dm.description.option.enabled"),
                        true)
        );
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of();
    }

    @Override
    public void onExecute() {
        User user = event.getUser();
        boolean enabled = event.getOption("enabled").getAsBoolean();

        try {
            var repo = Utils.getDatabaseManager().getRepositoryManager().getUsers();
            UserModel userModel = repo.findByDiscordId(user.getIdLong()).orElse(null);

            if (userModel == null) {
                userModel = new UserModel();
                userModel.setDiscordId(user.getIdLong());
                userModel.setUsername(user.getEffectiveName());
                userModel.setJoinedAt(new Timestamp(new Date().getTime()));
                userModel.setLeftAt(null);
                userModel.setBot(user.isBot());
                userModel.setLevelUpDmEnabled(enabled);
                repo.save(userModel);
            } else {
                userModel.setLevelUpDmEnabled(enabled);
                repo.update(userModel);
            }

            String status = enabled
                    ? getLangManager(event).getInfoLocale("settings.levelup-dm.status.enabled")
                    : getLangManager(event).getInfoLocale("settings.levelup-dm.status.disabled");

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(110, 220, 90));
            embed.setTitle(getLangManager(event).getInfoLocale("settings.levelup-dm.embed.title"));
            embed.setDescription(getLangManager(event).getInfoLocale("settings.levelup-dm.embed.description")
                    .replace("%status%", status));
            event.replyEmbeds(embed.build()).setEphemeral(true).queue();

        } catch (Exception e) {
            Logger.getLogger().log(LogType.ERROR, "SETTINGS", "Failed to update level-up DM setting", e);
            event.reply("Произошла ошибка при изменении настройки.").setEphemeral(true).queue();
        }
    }
}
