package ru.devprizrakk.voidbot.commands.music.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.core.bootstrap.discord.JDALoader;
import ru.devprizrakk.voidbot.core.command.discord.BaseCommand;
import ru.devprizrakk.voidbot.core.command.discord.CommandCategory;
import ru.devprizrakk.voidbot.core.exceptions.discord.WrongErrorEmbedFactory;
import ru.devprizrakk.voidbot.core.language.LangMessage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Volume extends BaseCommand {

    @Override
    public String getName() {
        return "volume";
    }

    @Override
    public String getDescription() {
        return getLangManager(event)
                .getInfoLocale(LangMessage.Commands.Music.Volume.FILE, LangMessage.Commands.Music.Volume.Description.COMMAND);
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.INTEGER, "volume", LangMessage.Commands.Music.Volume.Description.Option.VOLUME, false));
        return options;
    }

    @Override
    public CommandCategory getCategory() {
        return null;
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return List.of();
    }

    @Override
    public void onExecute() throws SQLException {
        if (event.getChannelType() != ChannelType.TEXT) {
            new WrongErrorEmbedFactory(event).
                    wrongError(getLangManager(event).
                            getDescriptionLocale(
                                    LangMessage.Commands.Music.Volume.FILE,
                                    LangMessage.Commands.Music.Volume.Error.NO_DM
                            )
                    );
            return;
        }
        Member member = event.getMember();
        assert member != null;
        GuildVoiceState memberVoiceState = member.getVoiceState();

        assert memberVoiceState != null;
        if (!memberVoiceState.inAudioChannel()) {
            new WrongErrorEmbedFactory(event).
                    wrongError(getLangManager(event).
                            getDescriptionLocale(
                                    LangMessage.Commands.Music.Volume.FILE,
                                    LangMessage.Commands.Music.Volume.Error.NO_FOUND_VOICE
                            )
                    );
            return;
        }
        int volume;
        if(event.getOption("volume") != null) {
            volume = Objects.requireNonNull(event.getOption("volume")).getAsInt();
            if (volume < 0 || volume > 100) {
                event.reply(getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.Volume.FILE, LangMessage.Commands.Music.Volume.Error.OUT_OF_RANGE)).setEphemeral(true).queue();
                new WrongErrorEmbedFactory(event).
                        wrongError(getLangManager(event).
                                getDescriptionLocale(
                                        LangMessage.Commands.Music.Volume.FILE,
                                        LangMessage.Commands.Music.Volume.Error.OUT_OF_RANGE
                                )
                        );
                return;
            }
            JDALoader.getLavalinkManager().getLavalinkClient().getOrCreateLink(Objects.requireNonNull(event.getGuild()).getIdLong())
                    .getPlayer()
                    .flatMap((player) -> player.setVolume(volume))
                    .subscribe((player) -> event.reply(getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.Volume.FILE, LangMessage.Commands.Music.Volume.Message.SUCCESSFUL)
                            .replace("%set-volume%", String.valueOf(player.getVolume()))).queue());
        } else {
            JDALoader.getLavalinkManager().getLavalinkClient().getOrCreateLink(Objects.requireNonNull(event.getGuild()).getIdLong())
                    .getPlayer()
                    .subscribe((player) -> event.reply(getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.Volume.FILE, LangMessage.Commands.Music.Volume.Message.INFO)
                            .replace("%get-volume%", String.valueOf(player.getVolume()))).queue());
        }


    }
}
