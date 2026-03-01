package ru.devprizrakk.voidbot.module.music.command;

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
import ru.devprizrakk.voidbot.api.bootstrap.discord.JDALoader;
import ru.devprizrakk.voidbot.api.command.discord.BaseCommand;
import ru.devprizrakk.voidbot.api.command.discord.CommandCategory;
import ru.devprizrakk.voidbot.module.music.lavalink.media.MediaInfo;
import ru.devprizrakk.voidbot.module.music.lavalink.media.MediaService;

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
        return getLangManager(event).getInfoLocale("command/music/nowplaying.yml", "now-playing.description.command");
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
            event.reply(getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.error.no-dm"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        Guild guild = event.getGuild();
        Member member = event.getMember();
        assert member != null;
        GuildVoiceState memberVoiceState = member.getVoiceState();

        assert memberVoiceState != null;
        if (!memberVoiceState.inAudioChannel()) {
            event.reply(getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.error.no-found-voice")).queue();
            return;
        }

        assert guild != null;
        final var link = JDALoader.getLavalinkManager().getLavalinkClient().getOrCreateLink(guild.getIdLong());
        final var player = link.getCachedPlayer();

        if (player == null) {
            event.reply(getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.error.no-found-player")).queue();
            return;
        }

        final var track = player.getTrack();
        if (track == null) {
            event.reply(getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.error.no-found-track")).queue();
            return;
        }

        boolean isPaused = player.getPaused();
        String playStatus = player.getPaused() ? getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.status.sound.false") : getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.status.sound.true");

        MediaService mediaService = new MediaService();
        MediaInfo info = mediaService.get(track.getInfo().getUri());
        event.replyEmbeds(buildTrackEmbed(track, isPaused, playStatus, getVolume(event, JDALoader.getLavalinkManager().getLavalinkClient()), info).build()).queue();
    }

    private EmbedBuilder buildTrackEmbed(Track track, boolean isPaused, String playStatus, int volume, MediaInfo info) {

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(isPaused ? Color.GRAY : new Color(111, 50, 1));
        embed.setAuthor(track.getInfo().getAuthor());
        embed.setThumbnail(info.getThumbnail());

        embed.setTitle(getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.title")
                .replace("%music-play-status%", playStatus)
                .replace("%music-name%", info.getTitle()), track.getInfo().getUri());


        if (!track.getInfo().isStream()) {
            embed.setDescription(
                    getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.description")
                            .replace("%music-progress-bar%", "\n" + getProgressBar(track.getInfo().getPosition(), track.getInfo().getLength()))
            );
            // time
            embed.addField(
                    getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.fields.time.video.title"),
                    getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.fields.time.video.description").replace("%music-current-time%", formatTime(track.getInfo().getPosition())).replace("%music-max-time%", formatTime(track.getInfo().getLength())),
                    true
            );
        } else {
            embed.setDescription(
                    getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.description")
                            .replace("%music-progress-bar%", "\n")
            );
            // time
            embed.addField(
                    getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.fields.time.stream.title"),
                    getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.fields.time.stream.description"),
                    true
            );
        }

        // Fields author
        if (info.getAuthor() != null && !info.getAuthor().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.fields.author.title"),
                    getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.fields.author.description").replace("%music-author%", info.getAuthor()),
                    true
            );

        }
        // Views
        if (info.getViewsCount() != null && !info.getViewsCount().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.fields.views.title"),
                    getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.fields.views.description").replace("%music-views%", info.getViewsCount()),
                    true
            );
        }
        // Like
        if (info.getLikesCount() != null && !info.getLikesCount().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.fields.like.title"),
                    getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.fields.like.description").replace("%music-like-count%", info.getLikesCount()),
                    true
            );
        }
        // date-created
        if (info.getCreatedDate() != null && !info.getCreatedDate().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.fields.date-created.title"),
                    getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.fields.date-created.description").replace("%music-date-created%", info.getCreatedDate()),
                    true
            );
        }
        // link
        if (track.getInfo().getUri() != null && !track.getInfo().getUri().isEmpty()) {
            embed.addField(
                    getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.fields.link.title"),
                    getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.fields.link.description").replace("%music-link%", track.getInfo().getUri()),
                    true
            );
        }

        // volume
        embed.addField(
                getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.fields.volume.title"),
                getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.fields.volume.description").replace("%music-volume%", volume + ""),
                true
        );

        embed.setFooter(getLangManager(event).getDescriptionLocale("command/music/nowplaying.yml", "now-playing.embed.footer"));
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
