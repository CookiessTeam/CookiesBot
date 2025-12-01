package ru.devprizrakk.voidbot.commands.discord.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import ru.devprizrakk.voidbot.commands.discord.loader.BaseCommand;
import ru.devprizrakk.voidbot.commands.discord.loader.CommandCategory;

import java.util.ArrayList;
import java.util.List;

public class Ban extends BaseCommand {
    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("command/moderation/ban.yml","ban.description.command");
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "target-user", getLangManager(event).getDescriptionLocale("command/moderation/ban.yml","ban.description.option.target-user"), true));
        options.add(new OptionData(OptionType.STRING, "time", getLangManager(event).getDescriptionLocale("command/moderation/ban.yml","ban.description.option.time"), false));
        options.add(new OptionData(OptionType.STRING, "user", getLangManager(event).getDescriptionLocale("command/moderation/ban.yml","ban.description.option.reason"), true));
        return options;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.ADMINISTRATION;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of();
    }

    @Override
    public void onExecute() {
        User author = event.getUser();
        User target_user;
        if (event.getOption("target-user").getAsUser() != null) {
            target_user = event.getOption("target-user").getAsUser();
        } else {

        }
    }
}