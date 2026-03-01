package ru.devprizrakk.voidbot.module.fun;


import ru.devprizrakk.voidbot.api.CoreContext;
import ru.devprizrakk.voidbot.api.command.discord.ModuleCommandRegistrar;
import ru.devprizrakk.voidbot.module.fun.commands.*;

public class FunMain {
    public static void onLoad(CoreContext coreContext) {
        ModuleCommandRegistrar registrar = coreContext.getModuleCommandRegistrar();

        registrar.registerModuleCommand(new Avatar());
        registrar.registerModuleCommand(new Calc());
        registrar.registerModuleCommand(new CoinFlip());
        registrar.registerModuleCommand(new Emote());
        registrar.registerModuleCommand(new Joke());
        registrar.registerModuleCommand(new RPS());

    }
}