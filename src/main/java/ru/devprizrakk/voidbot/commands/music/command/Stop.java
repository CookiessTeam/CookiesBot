package ru.devprizrakk.voidbot.commands.music.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.core.command.discord.BaseCommand;
import ru.devprizrakk.voidbot.core.command.discord.CommandCategory;
import ru.devprizrakk.voidbot.core.exceptions.discord.WrongErrorEmbedFactory;
import ru.devprizrakk.voidbot.core.language.LangMessage;
import ru.devprizrakk.voidbot.commands.music.MusicMain;
import ru.devprizrakk.voidbot.commands.music.lavalink.VoiceHelper;

import java.sql.SQLException;
import java.util.List;

public class Stop extends BaseCommand {

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale(LangMessage.Commands.Music.Stop.FILE, LangMessage.Commands.Music.Stop.Description.COMMAND);
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of();
    }

    @Override
    public void onExecute() throws SQLException {
        if (event.getChannelType() != ChannelType.TEXT) {
            new WrongErrorEmbedFactory(event).
                    wrongError(getLangManager(event)
                            .getDescriptionLocale(
                                    LangMessage.Commands.Music.Stop.FILE,
                                    LangMessage.Commands.Music.Stop.Error.NO_DM
                            )
                    );
            return;
        }

        if (!VoiceHelper.disconnectVoice(event.getGuild())) {
            new WrongErrorEmbedFactory(event).
                    wrongError(getLangManager(event).
                            getDescriptionLocale(
                                    LangMessage.Commands.Music.Stop.FILE,
                                    LangMessage.Commands.Music.Stop.Error.OTHER
                            )
                    );
            return;
        }
        event.reply(getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.Stop.FILE, LangMessage.Commands.Music.Stop.Message.SUCCESSFUL)).queue();
        MusicMain.getOrCreateMusicManager(event.getGuild().getIdLong()).stop();
        event.getJDA().getDirectAudioController().disconnect(event.getGuild());
    }

}

