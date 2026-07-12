package ru.devprizrakk.voidbot.lavalink;

import dev.arbjerg.lavalink.client.AbstractAudioLoadResultHandler;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.player.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import ru.devprizrakk.voidbot.command.impl.CommandManager;
import ru.devprizrakk.voidbot.exceptions.discord.WrongErrorEmbedFactory;
import ru.devprizrakk.voidbot.lavalink.media.MediaInfo;
import ru.devprizrakk.voidbot.lavalink.media.MediaService;

import java.awt.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.devprizrakk.voidbot.utils.Utils.getLangManager;

public class AudioLoader extends AbstractAudioLoadResultHandler {

    private final SlashCommandInteractionEvent event;
    private final GuildMusicManager guildMusicManager;
    private final LavalinkClient lavalinkClient;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> updateFuture;
    private Message lastEmbedMessage;

    public AudioLoader(SlashCommandInteractionEvent event, GuildMusicManager guildMusicManager, LavalinkClient lavalinkClient) {
        this.event = event;
        this.guildMusicManager = guildMusicManager;
        this.lavalinkClient = lavalinkClient;
    }

    @Override
    public void ontrackLoaded(@NotNull TrackLoaded result) {
        Track track = result.getTrack();
        this.guildMusicManager.scheduler.enqueue(track);
        sendTrackEmbed(event, track);
    }

    @Override
    public void onPlaylistLoaded(@NotNull PlaylistLoaded result) {
        this.guildMusicManager.scheduler.enqueuePlaylist(result.getTracks());
        String message = getLangManager(event).getDescriptionLocale("play.message.load-playlist")
                .replace("%size%", String.valueOf(result.getTracks().size()))
                .replace("%playlistName%", result.getInfo().getName());
        event.getHook().sendMessage(message).queue();
    }

    @Override
    public void onSearchResultLoaded(@NotNull SearchResult result) {
        List<Track> tracks = result.getTracks();
        if (tracks.isEmpty()) {
            new WrongErrorEmbedFactory(event).wrongErrorHook(
                    getLangManager(event).getDescriptionLocale("play.error.no-found-queue"));
            return;
        }

        Track firstTrack = tracks.getFirst();
        this.guildMusicManager.scheduler.enqueue(firstTrack);
        sendTrackEmbed(event, firstTrack);
    }

    @Override
    public void noMatches() {
        new WrongErrorEmbedFactory(event).wrongErrorHook(
                getLangManager(event).getDescriptionLocale("play.error.no-matches"));
    }

    @Override
    public void loadFailed(@NotNull LoadFailed result) {
        new WrongErrorEmbedFactory(event).wrongErrorHook(
                getLangManager(event).getDescriptionLocale("play.error.other")
                        .replace("%error-code%", result.getException().getMessage()));
    }

    private void sendTrackEmbed(SlashCommandInteractionEvent event, Track track) {
        if (lastEmbedMessage != null) {
            lastEmbedMessage.delete().queue();
        }

        AtomicBoolean isPaused = new AtomicBoolean(false);
        AtomicInteger volume = new AtomicInteger();

        lavalinkClient.getOrCreateLink(event.getGuild().getIdLong())
                .getPlayer()
                .subscribe(player -> {
                    isPaused.set(player.getPaused());
                    volume.set(player.getVolume());
                });

        String playStatus = isPaused.get()
                ? getLangManager(event).getDescriptionLocale("play.status.sound.false")
                : getLangManager(event).getDescriptionLocale("play.status.sound.true");
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
                        String playStatus = player.getPaused()
                                ? getLangManager(event).getDescriptionLocale("play.status.sound.false")
                                : getLangManager(event).getDescriptionLocale("play.status.sound.true");

                        if (currentPosition >= track.getInfo().getLength()) {
                            updateFuture.cancel(false);
                            lastEmbedMessage = null;
                            if (guildMusicManager.scheduler.queue.isEmpty()) {
                                CommandManager.getOrCreateMusicManager(event.getGuild().getIdLong());
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

        embed.setTitle(
                getLangManager(event).getDescriptionLocale("play.embed.title")
                        .replace("%music-play-status%", playStatus)
                        .replace("%music-name%", info.getTitle()), track.getInfo().getUri());

        if (!track.getInfo().isStream()) {
            embed.setDescription(
                    getLangManager(event).getDescriptionLocale("play.embed.description")
                            .replace("%music-progress-bar%", "\n" + getProgressBar(track.getInfo().getPosition(), track.getInfo().getLength()))
            );
            embed.addField(
                    getLangManager(event).getDescriptionLocale("play.embed.fields.time.video.title"),
                    getLangManager(event).getDescriptionLocale("play.embed.fields.time.video.description")
                            .replace("%music-current-time%", formatTime(track.getInfo().getPosition()))
                            .replace("%music-max-time%", formatTime(track.getInfo().getLength())),
                    true
            );
        } else {
            embed.setDescription(
                    getLangManager(event).getDescriptionLocale("play.embed.description")
                            .replace("%music-progress-bar%", "\n")
            );
            embed.addField(
                    getLangManager(event).getDescriptionLocale("play.embed.fields.time.stream.title"),
                    getLangManager(event).getDescriptionLocale("play.embed.fields.time.stream.description"),
                    true
            );
        }

        if (info.getAuthor() != null && !info.getAuthor().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale("play.embed.fields.author.title"),
                    getLangManager(event).getDescriptionLocale("play.embed.fields.author.description")
                            .replace("%music-author%", info.getAuthor()),
                    true
            );
        }

        if (info.getViewsCount() != null && !info.getViewsCount().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale("play.embed.fields.views.title"),
                    getLangManager(event).getDescriptionLocale("play.embed.fields.views.description")
                            .replace("%music-views%", info.getViewsCount()),
                    true
            );
        }

        if (info.getLikesCount() != null && !info.getLikesCount().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale("play.embed.fields.like.title"),
                    getLangManager(event).getDescriptionLocale("play.embed.fields.like.description")
                            .replace("%music-like-count%", info.getLikesCount()),
                    true
            );
        }

        if (info.getCreatedDate() != null && !info.getCreatedDate().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale("play.embed.fields.date-created.title"),
                    getLangManager(event).getDescriptionLocale("play.embed.fields.date-created.description")
                            .replace("%music-date-created%", info.getCreatedDate()),
                    true
            );
        }

        if (track.getInfo().getUri() != null && !track.getInfo().getUri().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale("play.embed.fields.link.title"),
                    getLangManager(event).getDescriptionLocale("play.embed.fields.link.description")
                            .replace("%music-link%", track.getInfo().getUri()),
                    true
            );
        }

        embed.addField(
                getLangManager(event).getDescriptionLocale("play.embed.fields.volume.title"),
                getLangManager(event).getDescriptionLocale("play.embed.fields.volume.description").replace("%music-volume%", volume + ""),
                true
        );

        embed.setFooter(getLangManager(event).getDescriptionLocale("play.embed.footer"));
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