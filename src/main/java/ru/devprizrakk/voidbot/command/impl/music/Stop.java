package ru.devprizrakk.voidbot.command.impl.music;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;
import ru.devprizrakk.voidbot.command.impl.CommandManager;
import ru.devprizrakk.voidbot.exceptions.discord.WrongErrorEmbedFactory;
import ru.devprizrakk.voidbot.lavalink.VoiceHelper;

import java.sql.SQLException;

public class Stop extends BaseCommand {

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("stop.description.command"
        );
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }

    @Override
    public void onExecute() throws SQLException {
        if (event.getChannelType() != ChannelType.TEXT) {
            new WrongErrorEmbedFactory(event).
                    wrongError(getLangManager(event)
                            .getDescriptionLocale("stop.error.no-dm"
                            )
                    );
            return;
        }

        if (!VoiceHelper.disconnectVoice(event.getGuild())) {
            new WrongErrorEmbedFactory(event).
                    wrongError(getLangManager(event).
                            getDescriptionLocale("stop.error.other"
                            )
                    );
            return;
        }

        event.reply(getLangManager(event).getDescriptionLocale("stop.message.successful")).queue();
        CommandManager.getOrCreateMusicManager(event.getGuild().getIdLong()).stop();
        event.getJDA().getDirectAudioController().disconnect(event.getGuild());
    }

}

