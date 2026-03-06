package ru.devprizrakk.voidbot.commands.music.lavalink.media;

import ru.devprizrakk.voidbot.commands.music.lavalink.media.platform.HttpRadioPlatform;
import ru.devprizrakk.voidbot.commands.music.lavalink.media.platform.TwitchPlatform;
import ru.devprizrakk.voidbot.commands.music.lavalink.media.platform.YoutubePlatform;

import java.util.List;

public class MediaService {

    private final List<MediaPlatform> platforms;

    public MediaService() {
        this.platforms = List.of(
                new YoutubePlatform(),
                new TwitchPlatform(),
                new HttpRadioPlatform()
        );
    }

    public MediaInfo get(String url) {
        return platforms.stream()
                .filter(platform -> platform.supports(url))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unsupported platform: " + url))
                .resolve(url);
    }
    public List<String> getAvailablePlatforms() {
        return platforms.stream()
                .map(MediaPlatform::getNamePlatform)
                .toList();
    }
}