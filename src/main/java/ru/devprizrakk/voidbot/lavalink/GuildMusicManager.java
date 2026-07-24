package ru.devprizrakk.voidbot.lavalink;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.LavalinkPlayer;

import java.util.Optional;

public class GuildMusicManager {

    private final long guildId;
    private final LavalinkClient lavalink;
    public final TrackScheduler scheduler = new TrackScheduler(this);

    public GuildMusicManager(long guildId, LavalinkClient lavalink) {
        this.lavalink = lavalink;
        this.guildId = guildId;
    }

    public void stop() {
        this.scheduler.queue.clear();
        this.getPlayer().ifPresent(
                (player) -> player.setPaused(false)
                        .setTrack(null)
                        .subscribe()
        );
    }

    public Optional<Link> getLink() {
        return Optional.ofNullable(this.lavalink.getLinkIfCached(this.guildId));
    }

    public Optional<LavalinkPlayer> getPlayer() {
        return this.getLink().map(Link::getCachedPlayer);
    }
}