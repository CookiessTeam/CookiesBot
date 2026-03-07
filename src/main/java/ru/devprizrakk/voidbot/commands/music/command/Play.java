package ru.devprizrakk.voidbot.commands.music.command;

import dev.arbjerg.lavalink.client.Link;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
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
import ru.devprizrakk.voidbot.commands.music.MusicMain;
import ru.devprizrakk.voidbot.commands.music.lavalink.AudioLoader;
import ru.devprizrakk.voidbot.commands.music.lavalink.VoiceHelper;
import ru.devprizrakk.voidbot.commands.music.lavalink.media.MediaService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Play extends BaseCommand {


    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale(LangMessage.Commands.Music.Play.FILE, LangMessage.Commands.Music.Play.Description.COMMAND);
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(
                OptionType.STRING,
                "song",
                getLangManager(event)
                        .getInfoLocale(
                                LangMessage.Commands.Music.Play.FILE,
                                LangMessage.Commands.Music.Play.Description.Option.SONG),
                true)
        );
        return options;
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
            if (new WrongErrorEmbedFactory(event).
                    wrongError(getLangManager(event).
                            getDescriptionLocale(
                                    LangMessage.Commands.Music.Play.FILE,
                                    LangMessage.Commands.Music.Play.Error.NO_DM)
                    )
            ) {
                return;
            }
        }
        Guild guild = event.getGuild();

        Member member = event.getMember();
        assert member != null;
        GuildVoiceState memberVoiceState = member.getVoiceState();

        assert memberVoiceState != null;
        if (!memberVoiceState.inAudioChannel()) {
            if (new WrongErrorEmbedFactory(event)
                    .wrongError(getLangManager(event).
                            getDescriptionLocale(
                                    LangMessage.Commands.Music.Play.FILE,
                                    LangMessage.Commands.Music.Play.Error.NO_FOUND_VOICE
                            )
                            .replace("%voiceChannel%", Objects.requireNonNull(memberVoiceState.getChannel()).getAsMention())
                    )
            ) {
                return;
            }
        }

        String name = Objects.requireNonNull(event.getOption("song")).getAsString();
        if (!name.startsWith("http")) {
            name = "ytsearch:" + name; // Поиск на YouTube, если это не ссылка
        } else {
            try {
                MediaService mediaService = new MediaService();
                mediaService.get(name);
            } catch (IllegalArgumentException e) {
                MediaService mediaService = new MediaService();
                String platforms = mediaService.getAvailablePlatforms().stream().map(item -> "`" + item + "`").collect(Collectors.joining(", "));
                if (new WrongErrorEmbedFactory(event)
                        .wrongError(getLangManager(event)
                                .getDescriptionLocale(
                                        LangMessage.Commands.Music.Play.FILE,
                                        LangMessage.Commands.Music.Play.Error.NO_SUPPORT_PLATFORM)
                                .replace("%music-platform-support%", platforms)
                        )
                ) {
                    return;
                }
            }
        }

        event.deferReply().queue();

        if (!VoiceHelper.connectVoiceOfMember(guild, member)) {
            event.reply(
                    getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.Play.FILE, LangMessage.Commands.Music.Play.Error.NO_FOUND_ME)
            ).queue();
        } else {
            MusicMain.getOrCreateMusicManager(event.getGuild().getIdLong());
        }

        Link link = JDALoader.getLavalinkManager().getLavalinkClient().getOrCreateLink(Objects.requireNonNull(event.getGuild()).getIdLong());
        var mngr = MusicMain.getOrCreateMusicManager(event.getGuild().getIdLong());

        link.loadItem(name).subscribe(new AudioLoader(event, mngr, JDALoader.getLavalinkManager().getLavalinkClient()));

    }

}