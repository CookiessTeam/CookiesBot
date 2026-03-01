package ru.devprizrakk.voidbot.module.music.lavalink;

import dev.arbjerg.lavalink.client.AbstractAudioLoadResultHandler;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.player.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import ru.devprizrakk.voidbot.api.exceptions.discord.WrongErrorEmbedFactory;
import ru.devprizrakk.voidbot.module.music.MusicMain;
import ru.devprizrakk.voidbot.module.music.lavalink.media.MediaService;
import ru.devprizrakk.voidbot.module.music.lavalink.media.MediaInfo;

import java.awt.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.devprizrakk.voidbot.api.utils.Utils.getLangManager;


public class AudioLoader extends AbstractAudioLoadResultHandler {
    private final SlashCommandInteractionEvent event;
    private final GuildMusicManager mngr;
    private final LavalinkClient lavalinkClient;
    private ScheduledFuture<?> updateFuture;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Message lastEmbedMessage;

    public AudioLoader(SlashCommandInteractionEvent event, GuildMusicManager mngr, LavalinkClient lavalinkClient) {
        this.event = event;
        this.mngr = mngr;
        this.lavalinkClient = lavalinkClient;
    }

    @Override
    public void ontrackLoaded(@NotNull TrackLoaded result) {
        Track track = result.getTrack();
        this.mngr.scheduler.enqueue(track);
        sendTrackEmbed(event, track);
    }

    @Override
    public void onPlaylistLoaded(@NotNull PlaylistLoaded result) {
        this.mngr.scheduler.enqueuePlaylist(result.getTracks());
        String message = getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.message.load-playlist")
                .replace("%size%", String.valueOf(result.getTracks().size()))
                .replace("%playlistName%", result.getInfo().getName());
        event.getHook().sendMessage(message).queue();
    }

    @Override
    public void onSearchResultLoaded(@NotNull SearchResult result) {
        List<Track> tracks = result.getTracks();
        if (tracks.isEmpty()) {
            event.reply(getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.error.no-found-queue")).queue();
            return;
        }
        Track firstTrack = tracks.get(0);
        this.mngr.scheduler.enqueue(firstTrack);
        sendTrackEmbed(event, firstTrack);
    }

    @Override
    public void noMatches() {
        event.getHook().sendMessage(getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.error.no-matches")).queue();
    }

    @Override
    public void loadFailed(@NotNull LoadFailed result) {
        event.getHook().sendMessageEmbeds(new WrongErrorEmbedFactory(event).wrongError(getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.error.other")
                .replace("%error-code%", result.getException().getMessage())).build()).queue();

    }

    private void sendTrackEmbed(SlashCommandInteractionEvent event, Track track) {
        if (lastEmbedMessage != null) {
            lastEmbedMessage.delete().queue();
        }

        Guild guild = event.getGuild();
        AtomicBoolean isPaused = new AtomicBoolean(false);
        AtomicInteger volume = new AtomicInteger();

        lavalinkClient.getOrCreateLink(event.getGuild().getIdLong())
                .getPlayer()
                .subscribe(player -> {
                    isPaused.set(player.getPaused());
                    volume.set(player.getVolume());
                });

        String playStatus = isPaused.get() ? getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.status.sound.false") : getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.status.sound.true");
        MediaService mediaService = new MediaService();
        MediaInfo info = mediaService.get(track.getInfo().getUri());
        EmbedBuilder embed = buildTrackEmbed(track, isPaused.get(), playStatus, volume.get(), info);
        event.getHook().sendMessageEmbeds(embed.build()).queue(originalMessage -> {
            lastEmbedMessage = originalMessage;
            startUpdatingEmbed(track, info);
        });
    }


    private void startUpdatingEmbed(Track track, MediaInfo info) {
        if (updateFuture != null && !updateFuture.isCancelled()) {
            updateFuture.cancel(false);
        }

        updateFuture = scheduler.scheduleAtFixedRate(() -> {
            if (lastEmbedMessage == null || track.getInfo().isStream()) return;
            lavalinkClient.getOrCreateLink(event.getGuild().getIdLong())
                    .getPlayer()
                    .subscribe(player -> {
                        long currentPosition = player.getPosition();
                        Guild guild = event.getGuild();
                        String playStatus = player.getPaused() ? getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.status.sound.false") : getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.status.sound.true");

                        if (currentPosition >= track.getInfo().getLength()) {
                            updateFuture.cancel(false);
                            lastEmbedMessage = null;
                            if (mngr.scheduler.queue.isEmpty()) {
                                MusicMain.getOrCreateMusicManager(event.getGuild().getIdLong());
                                //event.getGuild().getAudioManager().closeAudioConnection();
                            }
                            return;
                        }

                        lastEmbedMessage.editMessageEmbeds(buildTrackEmbed(track, player.getPaused(), playStatus, player.getVolume(), info).build()).queue();
                    });
        }, 5, 5, TimeUnit.SECONDS);
    }



    private EmbedBuilder buildTrackEmbed(Track track, boolean isPaused, String playStatus, int volume, MediaInfo info) {

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(isPaused ? Color.GRAY : new Color(111, 50, 1));
        embed.setAuthor(track.getInfo().getAuthor());
        embed.setThumbnail(info.getThumbnail());

        embed.setTitle(getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.title")
                .replace("%music-play-status%", playStatus)
                .replace("%music-name%", info.getTitle()), track.getInfo().getUri());


        if (!track.getInfo().isStream()) {
            embed.setDescription(
                    getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.description")
                            .replace("%music-progress-bar%", "\n" + getProgressBar(track.getInfo().getPosition(), track.getInfo().getLength()))
            );
            // time
            embed.addField(
                    getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.fields.time.video.title"),
                    getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.fields.time.video.description").replace("%music-current-time%", formatTime(track.getInfo().getPosition())).replace("%music-max-time%", formatTime(track.getInfo().getLength())),
                    true
            );
        } else {
            embed.setDescription(
                    getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.description")
                            .replace("%music-progress-bar%", "\n")
            );
            // time
            embed.addField(
                    getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.fields.time.stream.title"),
                    getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.fields.time.stream.description"),
                    true
            );
        }

        // Fields author
        if (info.getAuthor() != null && !info.getAuthor().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.fields.author.title"),
                    getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.fields.author.description").replace("%music-author%", info.getAuthor()),
                    true
            );

        }
        // Views
        if (info.getViewsCount() != null && !info.getViewsCount().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.fields.views.title"),
                    getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.fields.views.description").replace("%music-views%", info.getViewsCount()),
                    true
            );
        }
        // Like
        if (info.getLikesCount() != null && !info.getLikesCount().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.fields.like.title"),
                    getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.fields.like.description").replace("%music-like-count%", info.getLikesCount()),
                    true
            );
        }
        // date-created
        if (info.getCreatedDate() != null && !info.getCreatedDate().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.fields.date-created.title"),
                    getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.fields.date-created.description").replace("%music-date-created%", info.getCreatedDate()),
                    true
            );
        }
        // link
        if (track.getInfo().getUri() != null && !track.getInfo().getUri().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.fields.link.title"),
                    getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.fields.link.description").replace("%music-link%", track.getInfo().getUri()),
                    true
            );
        }

        // volume
        embed.addField(
                getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.fields.volume.title"),
                getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.fields.volume.description").replace("%music-volume%", volume + ""),
                true
        );

        embed.setFooter(getLangManager(event).getDescriptionLocale("command/music/play.yml", "play.embed.footer"));
        return embed;
    }
    private String getProgressBar(long position, long duration) {
        int totalBars = 19;
        int filledBars = (int) ((position * totalBars) / duration);
        return "▬".repeat(filledBars) + "🔵" + "▬".repeat(totalBars - filledBars);
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}