package ru.devprizrakk.voidbot.command.impl.music;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import ru.devprizrakk.voidbot.bootstrap.discord.JDALoader;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;
import ru.devprizrakk.voidbot.exceptions.discord.WrongErrorEmbedFactory;

import java.sql.SQLException;
import java.util.Objects;

public class Pause extends BaseCommand {

    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale("pause.description.command");
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
                            getDescriptionLocale("pause.error.no-dm"
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
                            getDescriptionLocale("pause.error.other"
                            )
                    );
            return;
        }

        JDALoader.getLavalinkManager().getLavalinkClient().getOrCreateLink(Objects.requireNonNull(event.getGuild()).getIdLong())
                .getPlayer()
                .flatMap((player) -> player.setPaused(!player.getPaused()))
                .subscribe((player) ->
                        event.reply(getLangManager(event).getDescriptionLocale("pause.message.successful")
                                .replace("%pause-status%",
                                        (
                                                player.getPaused() ? getLangManager(event).getDescriptionLocale("pause.status.false")
                                                        :
                                                        getLangManager(event).getDescriptionLocale("pause.status.true")
                                        )
                                )
                        ).queue());
    }
}
