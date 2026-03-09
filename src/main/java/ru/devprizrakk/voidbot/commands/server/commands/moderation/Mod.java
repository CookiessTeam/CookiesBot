package ru.devprizrakk.voidbot.commands.server.commands.moderation;

import ru.devprizrakk.voidbot.commands.server.commands.moderation.subcommands.Ban;
import ru.devprizrakk.voidbot.commands.server.commands.moderation.subcommands.Kick;
import ru.devprizrakk.voidbot.commands.server.commands.moderation.subcommands.Mute;
import ru.devprizrakk.voidbot.commands.server.commands.moderation.subcommands.Unban;
import ru.devprizrakk.voidbot.commands.server.commands.moderation.subcommands.Unmute;
import ru.devprizrakk.voidbot.core.command.discord.BaseCommand;
import ru.devprizrakk.voidbot.core.command.discord.CommandCategory;
import ru.devprizrakk.voidbot.core.command.discord.ISubCommand;

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
    public List<ISubCommand> getSubCommand() {
        return List.of(new Ban(), new Kick(), new Mute(), new Unmute(), new Unban());
    }

    @Override
    public void onExecute() throws SQLException {
        event.reply("Never give you up!").queue();
    }
}
