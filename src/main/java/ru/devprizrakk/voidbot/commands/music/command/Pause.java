package ru.devprizrakk.voidbot.commands.music.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.core.bootstrap.discord.JDALoader;
import ru.devprizrakk.voidbot.core.command.discord.BaseCommand;
import ru.devprizrakk.voidbot.core.command.discord.CommandCategory;
import ru.devprizrakk.voidbot.core.exceptions.discord.WrongErrorEmbedFactory;
import ru.devprizrakk.voidbot.core.language.LangMessage;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class Pause extends BaseCommand {

    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale(LangMessage.Commands.Music.Pause.FILE, LangMessage.Commands.Music.Pause.Description.COMMAND);
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
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
                                    LangMessage.Commands.Music.Pause.FILE,
                                    LangMessage.Commands.Music.Pause.Error.NO_DM
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
                                    LangMessage.Commands.Music.Pause.FILE,
                                    LangMessage.Commands.Music.Pause.Error.OTHER
                            )
                    );
            return;
        }

        JDALoader.getLavalinkManager().getLavalinkClient().getOrCreateLink(Objects.requireNonNull(event.getGuild()).getIdLong())
                .getPlayer()
                .flatMap((player) -> player.setPaused(!player.getPaused()))
                .subscribe((player) ->
                        event.reply(getLangManager(event).getDescriptionLocale(
                                        LangMessage.Commands.Music.Pause.FILE, LangMessage.Commands.Music.Pause.Message.SUCCESSFUL)
                                .replace("%pause-status%",
                                        (
                                                player.getPaused() ? getLangManager(event).getDescriptionLocale(
                                                        LangMessage.Commands.Music.Pause.FILE, LangMessage.Commands.Music.Pause.Status.FALSE)
                                                        :
                                                        getLangManager(event).getDescriptionLocale(
                                                                LangMessage.Commands.Music.Pause.FILE, LangMessage.Commands.Music.Pause.Status.TRUE)
                                        )
                                )
                        ).queue());
    }
}
