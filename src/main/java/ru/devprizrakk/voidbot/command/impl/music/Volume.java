package ru.devprizrakk.voidbot.command.impl.music;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.bootstrap.discord.JDALoader;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;
import ru.devprizrakk.voidbot.exceptions.discord.WrongErrorEmbedFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Volume extends BaseCommand {

    @Override
    public String getName() {
        return "volume";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("volume.description.command");
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.INTEGER, "volume", "volume.description.option.volume", false));
        return options;
    }

    @Override
    public CommandCategory getCategory() {
        return null;
    }

    @Override
    public void onExecute() throws SQLException {
        if (event.getChannelType() != ChannelType.TEXT) {
            new WrongErrorEmbedFactory(event).
                    wrongError(getLangManager(event).
                            getDescriptionLocale("volume.error.no-dm"
                            )
                    );
            return;
        }

        Member member = event.getMember();

        GuildVoiceState memberVoiceState = member.getVoiceState();
        if (!memberVoiceState.inAudioChannel()) {
            new WrongErrorEmbedFactory(event).
                    wrongError(getLangManager(event).
                            getDescriptionLocale("volume.error.no-found-voice"
                            )
                    );
            return;
        }

        int volume;
        if (event.getOption("volume") != null) {
            volume = event.getOption("volume").getAsInt();
            if (volume < 0 || volume > 100) {
                event.reply(getLangManager(event).getDescriptionLocale("volume.error.out-of-range")).setEphemeral(true).queue();
                new WrongErrorEmbedFactory(event).
                        wrongError(getLangManager(event).
                                getDescriptionLocale("volume.error.out-of-range"
                                )
                        );
                return;
            }

            JDALoader.getLavalinkManager().getLavalinkClient().getOrCreateLink(event.getGuild().getIdLong())
                    .getPlayer()
                    .flatMap((player) -> player.setVolume(volume))
                    .subscribe((player) -> event.reply(
                            getLangManager(event).getDescriptionLocale("volume.message.successful")
                                    .replace("%set-volume%", String.valueOf(player.getVolume()))).queue());
        } else {
            JDALoader.getLavalinkManager().getLavalinkClient().getOrCreateLink(event.getGuild().getIdLong())
                    .getPlayer()
                    .subscribe((player) -> event.reply(
                            getLangManager(event).getDescriptionLocale("volume.message.info")
                                    .replace("%get-volume%", String.valueOf(player.getVolume()))).queue());
        }
    }
}
