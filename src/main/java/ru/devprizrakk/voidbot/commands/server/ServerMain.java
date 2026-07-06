package ru.devprizrakk.voidbot.commands.server;

import net.dv8tion.jda.api.JDA;
import ru.devprizrakk.voidbot.commands.server.commands.moderation.Mod;
import ru.devprizrakk.voidbot.commands.server.commands.user.Rank;
import ru.devprizrakk.voidbot.commands.server.commands.user.ServerInfo;
import ru.devprizrakk.voidbot.commands.server.commands.user.ServerStats;
import ru.devprizrakk.voidbot.commands.server.commands.user.UserInfo;
import ru.devprizrakk.voidbot.core.command.discord.CommandRegister;
import ru.devprizrakk.voidbot.commands.server.commands.system.Help;
import ru.devprizrakk.voidbot.commands.server.commands.system.HelpSelectMenu;

public class ServerMain {

    public static void init(CommandRegister commandRegister, JDA jda) {

        commandRegister.add(new Help());
        commandRegister.add(new Mod());
        commandRegister.add(new Rank());
        commandRegister.add(new ServerStats());
        commandRegister.add(new UserInfo());
        commandRegister.add(new ServerInfo());

        jda.addEventListener(new HelpSelectMenu(commandRegister.commands));
    }


}