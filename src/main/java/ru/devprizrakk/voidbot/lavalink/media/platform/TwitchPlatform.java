package ru.devprizrakk.voidbot.lavalink.media.platform;

import ru.devprizrakk.voidbot.lavalink.media.DefaultMediaInfo;
import ru.devprizrakk.voidbot.lavalink.media.MediaInfo;
import ru.devprizrakk.voidbot.lavalink.media.MediaPlatform;

// TODO: Реализовать платформу Twitch для музыки
public class TwitchPlatform implements MediaPlatform {

    @Override
    public String getNamePlatform() {
        return "Twitch";
    }

    @Override
    public boolean supports(String url) {
        return url.contains("twitch.tv");
    }

    @Override
    public MediaInfo resolve(String url) {
        // Заглушка
        return new DefaultMediaInfo(
                "Unknown Twitch Stream",
                null,
                "Unknown streamer",
                "null",
                "null",
                "null",
                "null"
        );
    }
}