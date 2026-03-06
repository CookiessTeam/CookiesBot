package ru.devprizrakk.voidbot.commands.fun;


import ru.devprizrakk.voidbot.commands.fun.commands.*;
import ru.devprizrakk.voidbot.core.command.discord.CommandRegister;

public class FunMain {
    public static void init(CommandRegister commandRegister) {

        commandRegister.add(new Avatar());
        commandRegister.add(new Calc());
        commandRegister.add(new CoinFlip());
        commandRegister.add(new Emote());
        commandRegister.add(new Joke());
        commandRegister.add(new RPS());

    }
}