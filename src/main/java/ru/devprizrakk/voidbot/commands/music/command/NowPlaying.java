package ru.devprizrakk.voidbot.commands.music.command;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.core.bootstrap.discord.JDALoader;
import ru.devprizrakk.voidbot.core.command.discord.BaseCommand;
import ru.devprizrakk.voidbot.core.command.discord.CommandCategory;
import ru.devprizrakk.voidbot.core.exceptions.discord.WrongErrorEmbedFactory;
import ru.devprizrakk.voidbot.core.language.LangMessage;
import ru.devprizrakk.voidbot.commands.music.lavalink.media.MediaInfo;
import ru.devprizrakk.voidbot.commands.music.lavalink.media.MediaService;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class NowPlaying extends BaseCommand {

    @Override
    public String getName() {
        return "nowplaying";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale(
                LangMessage.Commands.Music.NowPlaying.FILE,
                LangMessage.Commands.Music.NowPlaying.Description.COMMAND);
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }

    @Override
    public void onExecute() throws SQLException {
        if (event.getChannelType() != ChannelType.TEXT) {
            new WrongErrorEmbedFactory(event).
                    wrongError(getLangManager(event).
                            getDescriptionLocale(
                                    LangMessage.Commands.Music.NowPlaying.FILE,
                                    LangMessage.Commands.Music.NowPlaying.Error.NO_DM
                            )
                    );
            return;
        }
        Guild guild = event.getGuild();
        Member member = event.getMember();
        assert member != null;
        GuildVoiceState memberVoiceState = member.getVoiceState();

        assert memberVoiceState != null;
        if (!memberVoiceState.inAudioChannel()) {
            new WrongErrorEmbedFactory(event).
                    wrongError(getLangManager(event).
                            getDescriptionLocale(
                                    LangMessage.Commands.Music.NowPlaying.FILE,
                                    LangMessage.Commands.Music.NowPlaying.Error.NO_FOUND_VOICE
                            )
                    );
            return;
        }

        assert guild != null;
        final var link = JDALoader.getLavalinkManager().getLavalinkClient().getOrCreateLink(guild.getIdLong());
        final var player = link.getCachedPlayer();

        if (player == null) {
            new WrongErrorEmbedFactory(event).
                    wrongError(getLangManager(event).
                            getDescriptionLocale(
                                    LangMessage.Commands.Music.NowPlaying.FILE,
                                    LangMessage.Commands.Music.NowPlaying.Error.NO_FOUND_PLAYER
                            )
                    );
            return;
        }

        final var track = player.getTrack();
        if (track == null) {
            new WrongErrorEmbedFactory(event).
                    wrongError(getLangManager(event).
                            getDescriptionLocale(
                                    LangMessage.Commands.Music.NowPlaying.FILE,
                                    LangMessage.Commands.Music.NowPlaying.Error.NO_FOUND_TRACK
                            )
                    );
            return;
        }

        boolean isPaused = player.getPaused();
        String playStatus = player.getPaused() ?
                getLangManager(event).getDescriptionLocale(
                        LangMessage.Commands.Music.NowPlaying.FILE,
                        LangMessage.Commands.Music.NowPlaying.Status.Sound.FALSE
                ) : getLangManager(event).getDescriptionLocale(
                LangMessage.Commands.Music.NowPlaying.FILE,
                LangMessage.Commands.Music.NowPlaying.Status.Sound.TRUE
        );

        MediaService mediaService = new MediaService();
        MediaInfo info = mediaService.get(track.getInfo().getUri());
        event.replyEmbeds(buildTrackEmbed(track, isPaused, playStatus, getVolume(event, JDALoader.getLavalinkManager().getLavalinkClient()), info).build()).queue();
    }

    private EmbedBuilder buildTrackEmbed(Track track, boolean isPaused, String playStatus, int volume, MediaInfo info) {

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(isPaused ? Color.GRAY : new Color(111, 50, 1));
        embed.setAuthor(track.getInfo().getAuthor());
        embed.setThumbnail(info.getThumbnail());

        embed.setTitle(getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.TITLE)
                .replace("%music-play-status%", playStatus)
                .replace("%music-name%", info.getTitle()), track.getInfo().getUri());


        if (!track.getInfo().isStream()) {
            embed.setDescription(
                    getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.DESCRIPTION)
                            .replace("%music-progress-bar%", "\n" + getProgressBar(track.getInfo().getPosition(), track.getInfo().getLength()))
            );
            // time
            embed.addField(
                    getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.Fields.Time.Video.TITLE),
                    getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.Fields.Time.Video.DESCRIPTION).replace("%music-current-time%", formatTime(track.getInfo().getPosition())).replace("%music-max-time%", formatTime(track.getInfo().getLength())),
                    true
            );
        } else {
            embed.setDescription(
                    getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.DESCRIPTION)
                            .replace("%music-progress-bar%", "\n")
            );
            // time
            embed.addField(
                    getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.Fields.Time.Stream.TITLE),
                    getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.Fields.Time.Stream.DESCRIPTION),
                    true
            );
        }

        // Fields author
        if (info.getAuthor() != null && !info.getAuthor().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.Fields.Author.TITLE),
                    getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.Fields.Author.DESCRIPTION).replace("%music-author%", info.getAuthor()),
                    true
            );

        }
        // Views
        if (info.getViewsCount() != null && !info.getViewsCount().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.Fields.Views.TITLE),
                    getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.Fields.Views.DESCRIPTION).replace("%music-views%", info.getViewsCount()),
                    true
            );
        }
        // Like
        if (info.getLikesCount() != null && !info.getLikesCount().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.Fields.Like.TITLE),
                    getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.Fields.Like.DESCRIPTION).replace("%music-like-count%", info.getLikesCount()),
                    true
            );
        }
        // date-created
        if (info.getCreatedDate() != null && !info.getCreatedDate().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.Fields.DateCreated.TITLE),
                    getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.Fields.DateCreated.DESCRIPTION).replace("%music-date-created%", info.getCreatedDate()),
                    true
            );
        }
        // link
        if (track.getInfo().getUri() != null && !track.getInfo().getUri().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.Fields.Link.TITLE),
                    getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.Fields.Link.DESCRIPTION).replace("%music-link%", track.getInfo().getUri()),
                    true
            );
        }

        // volume
        embed.addField(
                getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.Fields.Volume.TITLE),
                getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.Fields.Volume.DESCRIPTION).replace("%music-volume%", volume + ""),
                true
        );

        embed.setFooter(getLangManager(event).getDescriptionLocale(LangMessage.Commands.Music.NowPlaying.FILE, LangMessage.Commands.Music.NowPlaying.Embed.FOOTER));
        return embed;
    }

    private String getProgressBar(long position, long duration) {
        int totalBars = 19;
        int filledBars = (int) ((position * totalBars) / duration);
        return "▬".repeat(filledBars) + "🔵" + "▬".repeat(totalBars - filledBars);
    }

    private int getVolume(SlashCommandInteractionEvent event, LavalinkClient lavalinkClient) {
        AtomicInteger volume = new AtomicInteger();
        lavalinkClient.getOrCreateLink(Objects.requireNonNull(event.getGuild()).getIdLong())
                .getPlayer()
                .flatMap(player -> player.setVolume(player.getVolume()))
                .subscribe(player -> volume.set(player.getVolume()));
        return volume.get();
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
