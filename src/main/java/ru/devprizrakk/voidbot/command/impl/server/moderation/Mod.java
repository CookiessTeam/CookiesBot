package ru.devprizrakk.voidbot.command.impl.server.moderation;

import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.BaseSubCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;
import ru.devprizrakk.voidbot.command.impl.server.moderation.subcommands.*;

import java.sql.SQLException;
import java.util.List;

public class Mod extends BaseCommand {

    @Override
    public String getName() {
        return "mod";
    }

    @Override
    public String getDescription() {
        return "miamore";
    }

    @Override
    public CommandCategory getCategory() {
        return null;
    }

    @Override
    public List<BaseSubCommand> getSubCommands() {
        return List.of(new Ban(), new Kick(), new Mute(), new Unmute(), new Unban(), new Warn());
    }

    @Override
    public void onExecute() throws SQLException {
        event.reply("Never give you up!").queue();
    }
}
