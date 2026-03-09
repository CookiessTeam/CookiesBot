package ru.devprizrakk.voidbot.commands;

import net.dv8tion.jda.api.JDA;
import ru.devprizrakk.voidbot.commands.fun.FunMain;
import ru.devprizrakk.voidbot.commands.music.MusicMain;
import ru.devprizrakk.voidbot.commands.server.ServerMain;
import ru.devprizrakk.voidbot.core.command.discord.CommandRegister;

public class CommandManager {
    public static void init(JDA jda, CommandRegister commandRegister) {
        MusicMain.init(commandRegister);
        ServerMain.init(commandRegister, jda);
        FunMain.init(commandRegister);
    }
}
