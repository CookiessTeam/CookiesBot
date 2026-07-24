package ru.devprizrakk.voidbot.command.impl.server.settings;

import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.BaseSubCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;

import java.sql.SQLException;
import java.util.List;

public class Settings extends BaseCommand {

    @Override
    public String getName() {
        return "settings";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("settings.description.command");
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.SERVER;
    }

    @Override
    public List<BaseSubCommand> getSubCommands() {
        return List.of(new LevelUpDm());
    }

    @Override
    public void onExecute() throws SQLException {
        event.reply(getLangManager(event).getInfoLocale("settings.description.command")).setEphemeral(true).queue();
    }
}
